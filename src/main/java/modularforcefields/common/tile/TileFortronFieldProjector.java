package modularforcefields.common.tile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.utils.Sets;

import electrodynamics.api.ISubtype;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.object.Location;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.block.FortronFieldColor;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.tile.projection.ProjectionType;
import modularforcefields.common.tile.projection.ThreadProjectorCalculationThread;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.IFluidBlock;

public class TileFortronFieldProjector extends TileFortronConnective {
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.values());
	public static final int BASEENERGY = 100;
	private ThreadProjectorCalculationThread calculationThread;
	public Set<BlockPos> calculatedFieldPoints = Collections.synchronizedSet(new HashSet<>());
	public Set<TileFortronField> activeFields = new HashSet<>();
	public int calculatedSize;
	private FortronFieldColor fieldColor = FortronFieldColor.LIGHT_BLUE;
	public FortronFieldStatus status = FortronFieldStatus.PREPARE;
	public ProjectionType type = ProjectionType.NONE;
	public int xRadiusPos;
	public int yRadiusPos;
	public int zRadiusPos;
	public int xRadiusNeg;
	public int yRadiusNeg;
	public int zRadiusNeg;
	public int radius;
	public int scaleEnergy;
	public int speedEnergy;
	public boolean shouldSponge = false;
	public boolean shouldDisintegrate = false;
	public boolean shouldStabilize = false;
	public boolean hasCollectionModule = false;
	public int totalGeneratedPerTick = 0;
	public int fortronCapacity;
	public int fortron;
	public int moduleCount;
	public int ticksUntilProjection;
	public BlockPos shiftedPosition;

	public void destroyField(boolean instant) {
		status = FortronFieldStatus.DESTROYING;
		calculatedSize = 0;
		if (calculationThread != null) {
			calculationThread.interrupt();
			calculationThread = null;
		}
		calculatedFieldPoints.clear();

		if (instant) {
			for (TileFortronField field : activeFields) {
				level.setBlockAndUpdate(field.getBlockPos(), Blocks.AIR.defaultBlockState());
			}
			activeFields.clear();
		}
	}

	public TileFortronFieldProjector(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_FORTRONFIELDPROJECTOR.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::saveAdditional).guiPacketReader(this::load));
		addComponent(new ComponentInventory(this).size(21).shouldSendInfo().valid((index, stack, inv) -> true).onChanged(this::onChanged));
		addComponent(new ComponentContainerProvider("container.fortronfieldprojector").createMenu((id, player) -> new ContainerFortronFieldProjector(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
	}

	@Override
	protected void tickCommon(ComponentTickable tickable) {
		super.tickCommon(tickable);
		if (tickable.getTicks() % 20 == 0) {
			fortronCapacity = getMaxFortron();
			fortron = Mth.clamp(fortron, 0, fortronCapacity);
		}
	}

	@Override
	protected void tickServer(ComponentTickable tickable) {
		super.tickServer(tickable);
		if (status == FortronFieldStatus.PROJECTED) {
			if (activeFields.size() >= calculatedSize) {
				status = FortronFieldStatus.PROJECTED_SEALED;
			}
		}
		ProjectionType projectedType = getProjectionType();
		if (type != projectedType) {
			destroyField(false);
			type = projectedType;
		}
		if (status == FortronFieldStatus.DESTROYING) {
			if (activeFields.isEmpty()) {
				status = FortronFieldStatus.PREPARE;
				ticksUntilProjection = 40;
			} else {
				int count = 0;
				Iterator<TileFortronField> it = activeFields.iterator();
				while (it.hasNext()) {
					if (count++ > 250) {
						break;
					}
					TileFortronField field = it.next();
					level.setBlockAndUpdate(field.getBlockPos(), Blocks.AIR.defaultBlockState());
					it.remove();
				}
			}
		}
		if (tickable.getTicks() > 5) {
			int use = getFortronUse();
			if (isPoweredByRedstone() && type != ProjectionType.NONE && fortron >= use) {
				fortron -= use;
				if (status != FortronFieldStatus.DESTROYING) {
					if (status == FortronFieldStatus.PREPARE && calculatedFieldPoints.isEmpty()) {
						if (ticksUntilProjection > 0) {
							if (fortron > use) {
								ticksUntilProjection--;
							}
						} else {
							ticksUntilProjection = 40;
							status = FortronFieldStatus.CALCULATING;
							calculationThread = new ThreadProjectorCalculationThread(this);
							calculationThread.start();
							Logger.getGlobal().log(Level.INFO, "Started forcefield calculation thread at: " + new Location(worldPosition).toString());
						}
					} else if (status != FortronFieldStatus.CALCULATING && !calculatedFieldPoints.isEmpty()) {
						projectField();
					}
				}
			} else {
				if (fortron < use) {
					ticksUntilProjection = 100;
				}
				destroyField(false);
			}
		}
	}

	private void projectField() {
		status = FortronFieldStatus.PROJECTING;
		Set<BlockPos> finishedQueueItems = new HashSet<>();
		int currentlyGenerated = 0;
		int currentlyMissed = 0;

//		ArrayList<TileFortronFieldConstructor> relevantConstructors = shouldDisintegrate ? ForcefieldEventHandler.INSTANCE.getRelevantConstructors(World(), loc.xCoord, loc.yCoord, loc.zCoord) : null;
		for (BlockPos fieldPoint : calculatedFieldPoints) {
			if ((currentlyGenerated >= totalGeneratedPerTick) || (currentlyMissed >= 500)) {
				break;
			}
			finishedQueueItems.add(fieldPoint);
			BlockState state = level.getBlockState(fieldPoint);
			Block block = state.getBlock();
			if (block == DeferredRegisters.blockFortronField) {
				TileFortronField field = (TileFortronField) level.getBlockEntity(fieldPoint);
				if (field != null && field.getProjectorPos().equals(new Location(worldPosition))) {
					activeFields.add(field);
					currentlyGenerated++;
					continue;
				}
			}
			if (shouldSponge) {
				if (block instanceof IFluidBlock) {
					level.setBlockAndUpdate(fieldPoint, Blocks.AIR.defaultBlockState());
					for (Direction direction : Direction.values()) {
						BlockPos adjPos = fieldPoint.offset(direction.getNormal());
						Block adjacentBlock = level.getBlockState(adjPos).getBlock();
						if (adjacentBlock instanceof IFluidBlock) {
							level.setBlockAndUpdate(adjPos, Blocks.AIR.defaultBlockState());
						}
					}
					currentlyGenerated++;
				} else if (state.isAir()) {
					currentlyMissed++;
				}
			} else if (shouldDisintegrate) {
//					boolean skip = false;
//					if (relevantConstructors != null) {
//						for (TileFortronFieldConstructor constructor : relevantConstructors) {
//							if (constructor != this && constructor.isProtecting(fieldPoint.xCoord, fieldPoint.yCoord, fieldPoint.zCoord)) {
//								currentlyMissed++;
//								skip = true;
//								break;
//							}
//						}
//					}
//					if (skip) {
//						continue;
//					}
				if (state.getDestroySpeed(level, fieldPoint) != -1) {
					if (hasCollectionModule) {
						for (ItemStack stack : Block.getDrops(state, (ServerLevel) level, fieldPoint, null)) {
							// TODO: implement collection module
						}
					}
					if (state.isAir()) {
						currentlyMissed++;
						continue;
					}
					level.setBlockAndUpdate(fieldPoint, Blocks.AIR.defaultBlockState());
					currentlyGenerated++;
				}
			} else if (state.canBeReplaced(new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, new ItemStack(block), new BlockHitResult(Vec3.ZERO, Direction.DOWN, fieldPoint, false)))) {
				if (shouldStabilize) {
					for (Direction dir : Direction.values()) {
						BlockEntity entity = level.getBlockEntity(shiftedPosition.offset(dir.getNormal()));
						if (entity instanceof Container) {
							// TOOD: Implement stabilize module
						}
					}
				} else {
					level.setBlockAndUpdate(fieldPoint, DeferredRegisters.blockFortronField.defaultBlockState());
					if (level.getBlockEntity(fieldPoint) instanceof TileFortronField field) {
						field.setConstructor(this);
						activeFields.add(field);
					}
					currentlyGenerated++;
				}
			} else if (state.getDestroySpeed(level, fieldPoint) == -1) {
				calculatedSize--;
			}
		}
		calculatedFieldPoints.removeAll(finishedQueueItems);
	}

	public int getMaxFortron() {
		return getFortronUse() * 40 + BASEENERGY;
	}

	public int getFortronUse() {
		return -1;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("frequency", frequency);
		tag.putInt("type", type.ordinal());
		tag.putInt("fieldColor", fieldColor.ordinal());
		tag.putInt("moduleCount", moduleCount);
		tag.putInt("status", status.ordinal());
		tag.putInt("fortronCapacity", fortronCapacity);
		tag.putInt("fortron", fortron);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		frequency = tag.getInt("frequency");
		type = ProjectionType.values()[tag.getInt("type")];
		fieldColor = FortronFieldColor.values()[tag.getInt("fieldColor")];
		moduleCount = tag.getInt("moduleCount");
		status = FortronFieldStatus.values()[tag.getInt("status")];
		fortronCapacity = tag.getInt("fortronCapacity");
		fortron = tag.getInt("fortron");
	}

	public BlockPos getShiftedPos() {
		if (shiftedPosition == null) {
			shiftedPosition = worldPosition;
		}
		return shiftedPosition;
	}

	@Override
	protected boolean canRecieveFortron(TileFortronConnective tile) {
		return tile instanceof TileFortronCapacitor;
	}

	public boolean isInterior() {
		return hasModule(SubtypeModule.upgradeinterior);
	}

	public void updateFieldTerms() {
		shouldSponge = hasModule(SubtypeModule.upgradesponge);
		shouldDisintegrate = hasModule(SubtypeModule.upgradedisintegration);
		shouldStabilize = hasModule(SubtypeModule.upgradestabilize);
		hasCollectionModule = hasModule(SubtypeModule.upgradecollection);
		totalGeneratedPerTick = 1 + 2 * countModules(SubtypeModule.upgradespeed, ContainerFortronFieldProjector.SLOT_UPGRADES[0], ContainerFortronFieldProjector.SLOT_UPGRADES[ContainerFortronFieldProjector.SLOT_UPGRADES.length - 1]) / ((shouldSponge ? 2 : 5) / (shouldStabilize ? 2 : 1));
		if (shouldSponge) {
			totalGeneratedPerTick /= 2;
		} else if (shouldDisintegrate) {
			totalGeneratedPerTick /= hasCollectionModule ? 5 : 4;
		} else if (shouldStabilize) {
			totalGeneratedPerTick /= 3;
		}
	}

	private void onChanged(ComponentInventory inv) {
		int ret = 0;
		updateFieldTerms();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (i != ContainerFortronFieldProjector.SLOT_TYPE) {
				ItemStack stack = inv.getItem(i);
				if (stack != null) {
					if (DeferredRegisters.ITEMSUBTYPE_MAPPINGS.get(stack.getItem()) instanceof SubtypeModule) {
						ret += stack.getCount();
					}
				}
			}
		}
		if (ret != moduleCount) {
			moduleCount = ret;
			destroyField(false);
		}
		shiftedPosition = worldPosition.offset(countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_EAST[0], ContainerFortronFieldProjector.SLOT_EAST[1]) - countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_WEST[0], ContainerFortronFieldProjector.SLOT_WEST[1]), countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_UP[0], ContainerFortronFieldProjector.SLOT_UP[1]) - countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_DOWN[0], ContainerFortronFieldProjector.SLOT_DOWN[1]), countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_SOUTH[0], ContainerFortronFieldProjector.SLOT_SOUTH[1]) - countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_NORTH[0], ContainerFortronFieldProjector.SLOT_NORTH[1]));
		xRadiusPos = shiftedPosition.getX() + Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_EAST[0], ContainerFortronFieldProjector.SLOT_EAST[1]));
		yRadiusPos = Math.min(getLevel().getMaxBuildHeight(), Math.max(getLevel().getMinBuildHeight(), shiftedPosition.getY() + countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_UP[0], ContainerFortronFieldProjector.SLOT_UP[1])));
		zRadiusPos = shiftedPosition.getZ() + Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_SOUTH[0], ContainerFortronFieldProjector.SLOT_SOUTH[1]));
		xRadiusNeg = shiftedPosition.getX() - Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_WEST[0], ContainerFortronFieldProjector.SLOT_WEST[1]));
		yRadiusNeg = Math.max(getLevel().getMinBuildHeight(), shiftedPosition.getY() - countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_DOWN[0], ContainerFortronFieldProjector.SLOT_DOWN[1]));
		zRadiusNeg = shiftedPosition.getZ() - Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_NORTH[0], ContainerFortronFieldProjector.SLOT_NORTH[1]));
		radius = Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_MODULES[0], ContainerFortronFieldProjector.SLOT_MODULES[ContainerFortronFieldProjector.SLOT_MODULES.length - 1]) / 6);
		scaleEnergy = BASEENERGY * countModules(SubtypeModule.manipulationscale, 0, 11);
		speedEnergy = 1 + BASEENERGY * countModules(SubtypeModule.upgradespeed, ContainerFortronFieldProjector.SLOT_UPGRADES[0], ContainerFortronFieldProjector.SLOT_UPGRADES[ContainerFortronFieldProjector.SLOT_UPGRADES.length - 1]);
	}

	public ProjectionType getProjectionType() {
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		ItemStack stack = inv.getItem(ContainerFortronFieldProjector.SLOT_TYPE);
		ISubtype subtype = DeferredRegisters.ITEMSUBTYPE_MAPPINGS.get(stack.getItem());
		if (subtype instanceof SubtypeModule module) {
			switch (module) {
			case shapecube:
				return ProjectionType.CUBE;
			case shapehemisphere:
				return ProjectionType.HEMISPHERE;
			case shapepyramid:
				return ProjectionType.PYRAMID;
			case shapesphere:
				return ProjectionType.SPHERE;
			default:
				break;
			}
		}
		return ProjectionType.NONE;
	}

	public FortronFieldColor getFieldColor() {
		return fieldColor;
	}
}
