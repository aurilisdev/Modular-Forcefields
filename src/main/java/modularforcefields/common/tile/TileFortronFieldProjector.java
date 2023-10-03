package modularforcefields.common.tile;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Sets;

import electrodynamics.api.ISubtype;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.InventoryUtils;
import electrodynamics.prefab.utilities.object.Location;
import modularforcefields.common.block.FortronFieldColor;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.tile.projection.ProjectionType;
import modularforcefields.common.tile.projection.ThreadProjectorCalculationThread;
import modularforcefields.registers.ModularForcefieldsBlockTypes;
import modularforcefields.registers.ModularForcefieldsBlocks;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.RegistryObject;

public class TileFortronFieldProjector extends TileFortronConnective {
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.values());
	public static final int BASEENERGY = 100;
	private ThreadProjectorCalculationThread calculationThread;
	public Set<BlockPos> calculatedFieldPoints = Collections.synchronizedSet(new HashSet<>());
	public Set<TileFortronField> activeFields = new HashSet<>();
	public final Property<Integer> typeOrdinal = property(new Property<>(PropertyType.Integer, "type", ProjectionType.NONE.ordinal()));
	public final Property<Integer> fieldColorOrdinal = property(new Property<>(PropertyType.Integer, "fieldColorOrdinal", FortronFieldColor.LIGHT_BLUE.ordinal()));
	public final Property<Integer> moduleCount = property(new Property<>(PropertyType.Integer, "moduleCount", 0));
	public final Property<Integer> fortronCapacity = property(new Property<>(PropertyType.Integer, "fortronCapacity", 0));
	public final Property<Integer> fortron = property(new Property<>(PropertyType.Integer, "fortron", 0));
	public final Property<Integer> fortronUse = property(new Property<>(PropertyType.Integer, "fortronUse", 0));
	public int calculatedSize;
	private final Property<Integer> statusInteger = property(new Property<>(PropertyType.Integer, "statusInteger", FortronFieldStatus.PROJECTING.ordinal()));

	public FortronFieldStatus getStatus() {
		return FortronFieldStatus.values()[statusInteger.get()];
	}

	public void setStatus(FortronFieldStatus status) {
		statusInteger.set(status.ordinal());
	}

	public final Property<Integer> xRadiusPos = property(new Property<>(PropertyType.Integer, "xRadiusPos", 0));
	public final Property<Integer> yRadiusPos = property(new Property<>(PropertyType.Integer, "yRadiusPos", 0));
	public final Property<Integer> zRadiusPos = property(new Property<>(PropertyType.Integer, "zRadiusPos", 0));
	public final Property<Integer> xRadiusNeg = property(new Property<>(PropertyType.Integer, "xRadiusNeg", 0));
	public final Property<Integer> yRadiusNeg = property(new Property<>(PropertyType.Integer, "yRadiusNeg", 0));
	public final Property<Integer> zRadiusNeg = property(new Property<>(PropertyType.Integer, "zRadiusNeg", 0));
	public final Property<Integer> radius = property(new Property<>(PropertyType.Integer, "radius", 0));
	public int scaleEnergy;
	public int speedEnergy;
	public boolean shouldSponge = false;
	public boolean shouldDisintegrate = false;
	public boolean shouldStabilize = false;
	public boolean hasCollectionModule = false;
	public boolean isInterior = false;
	public int totalGeneratedPerTick = 0;
	public int ticksUntilProjection;
	public final Property<BlockPos> shiftedPosition = property(new Property<>(PropertyType.BlockPos, "shiftedPosition", BlockPos.ZERO));

	public void destroyField(boolean instant) {
		setStatus(FortronFieldStatus.DESTROYING);
		calculatedSize = 0;
		if (calculationThread != null) {
			calculationThread.interrupt();
			calculationThread.stop();
			calculationThread = null;
		}
		calculatedFieldPoints.clear();

		if (instant) {
			for (TileFortronField field : activeFields) {
				level.setBlock(field.getBlockPos(), Blocks.AIR.defaultBlockState(), 2);
			}
			activeFields.clear();
		}
	}

	@Override
	protected int recieveFortron(int amount) {
		int received = Math.max(0, Math.min(amount, fortronCapacity.get() - fortron.get()));
		fortron.set(fortron.get() + received);
		return received;
	}

	@Override
	public void onBlockDestroyed() {
		destroyField(true);
	}

	@Override
	protected Predicate<BlockEntity> getConnectionTest() {
		return b -> b instanceof TileFortronCapacitor;
	}

	public TileFortronFieldProjector(BlockPos pos, BlockState state) {
		super(ModularForcefieldsBlockTypes.TILE_FORTRONFIELDPROJECTOR.get(), pos, state);
		addComponent(new ComponentDirection(this));
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentInventory(this, InventoryBuilder.newInv().forceSize(21)).valid((index, stack, inv) -> true).onChanged(this::onChanged));
		addComponent(new ComponentContainerProvider("container.fortronfieldprojector", this).createMenu((id, player) -> new ContainerFortronFieldProjector(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
	}

	@Override
	protected void tickServer(ComponentTickable tickable) {
		super.tickServer(tickable);
		if (tickable.getTicks() % 20 == 0) {
			fortronCapacity.set(getMaxFortron());
			fortron.set(Mth.clamp(fortron.get(), 0, fortronCapacity.get()));
		}
		if (tickable.getTicks() % 1000 == 1) {
			onChanged(getComponent(ComponentType.Inventory), -1);
		}
		if (getStatus() == FortronFieldStatus.PROJECTED) {
			if (activeFields.size() >= calculatedSize) {
				setStatus(FortronFieldStatus.PROJECTED_SEALED);
			}
		}
		ProjectionType projectedType = getProjectionType();
		if (typeOrdinal.get() != projectedType.ordinal()) {
			destroyField(false);
			typeOrdinal.set(projectedType.ordinal());
		}
		if (getStatus() == FortronFieldStatus.DESTROYING) {
			if (activeFields.isEmpty()) {
				setStatus(FortronFieldStatus.PREPARE);
				ticksUntilProjection = 40;
			} else {
				int count = 0;
				Iterator<TileFortronField> it = activeFields.iterator();
				while (it.hasNext()) {
					if (count++ > 100) {
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
			if (isPoweredByRedstone() && typeOrdinal.get() != ProjectionType.NONE.ordinal() && fortron.get() >= use) {
				fortron.set(fortron.get() - use);
				if (getStatus() != FortronFieldStatus.DESTROYING) {
					if (getStatus() == FortronFieldStatus.PREPARE && calculatedFieldPoints.isEmpty()) {
						if (ticksUntilProjection > 0) {
							if (fortron.get() > use) {
								ticksUntilProjection--;
							}
						} else {
							ticksUntilProjection = 40;
							setStatus(FortronFieldStatus.CALCULATING);
							calculationThread = new ThreadProjectorCalculationThread(this);
							calculationThread.start();
							Logger.getGlobal().log(Level.INFO, "Started forcefield calculation thread at: " + new Location(worldPosition));
						}
					} else if (getStatus() != FortronFieldStatus.CALCULATING && !calculatedFieldPoints.isEmpty()) {
						projectField();
					} else if (getStatus() == FortronFieldStatus.PROJECTING) {
						setStatus(FortronFieldStatus.PROJECTED);
						for (TileFortronField field : activeFields) {
							field.setConstructor(this);
						} // Looping through after is bad so fix if possible

					}
				}
			} else if (getStatus() != FortronFieldStatus.PREPARE) {
				if (fortron.get() < use) {
					ticksUntilProjection = 100;
				}
				destroyField(false);
			}
		}
	}

	private void projectField() {
		setStatus(FortronFieldStatus.PROJECTING);
		Set<BlockPos> finishedQueueItems = new HashSet<>();
		int currentlyGenerated = 0;
		for (BlockPos fieldPoint : calculatedFieldPoints) {
			if (currentlyGenerated >= totalGeneratedPerTick) {
				break;
			}
			finishedQueueItems.add(fieldPoint);
			BlockState state = level.getBlockState(fieldPoint);
			Block block = state.getBlock();
			if (block == ModularForcefieldsBlocks.blockFortronField) {
				if (integrateExistingFieldPoint(fieldPoint)) {
					currentlyGenerated += 1;
					continue;
				}
			}
			if (shouldSponge) {
				// TODO: IMPLEMENT SPONGE MODULE
			}
			if (shouldDisintegrate) {
				state = disintegrate(fieldPoint, state);
			}
			if (state.canBeReplaced(new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, new ItemStack(block), new BlockHitResult(Vec3.ZERO, Direction.DOWN, fieldPoint, false)))) {
				if (shouldStabilize) {
					stabilizeFieldPoint(fieldPoint);
				} else {
					currentlyGenerated = createNewFieldPoint(currentlyGenerated, fieldPoint);
				}
			} else if (state.getDestroySpeed(level, fieldPoint) == -1) {
				calculatedSize--;
			}
		}
		calculatedFieldPoints.removeAll(finishedQueueItems);

	}

	private boolean integrateExistingFieldPoint(BlockPos fieldPoint) {
		TileFortronField field = (TileFortronField) level.getBlockEntity(fieldPoint);
		if (field == null) {
			return false;
		}
		boolean isSupposedToBeConnectedLocally = worldPosition.equals(field.getProjectorPos());

		boolean invalidProjector = field.getProjectorPos() == null || !(level.getBlockEntity(field.getProjectorPos()) instanceof TileFortronFieldProjector);
		if (isSupposedToBeConnectedLocally || invalidProjector) {
			activeFields.add(field);
			field.setConstructor(this);
			return true;
		}
		return false;
	}

	private int createNewFieldPoint(int currentlyGenerated, BlockPos fieldPoint) {
		level.setBlockAndUpdate(fieldPoint, ModularForcefieldsBlocks.blockFortronField.defaultBlockState());
		if (level.getBlockEntity(fieldPoint) instanceof TileFortronField field) {
			field.setConstructor(this);
			activeFields.add(field);
		}
		return currentlyGenerated + 1;
	}

	private void stabilizeFieldPoint(BlockPos fieldPoint) {
		boolean broken = false;
		for (Direction dir : Direction.values()) { // TODO: Optimize this so it doesnt check all inventories around every placement.
			if (broken) {
				break;
			}
			BlockEntity entity = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
			if (entity != null) {
				LazyOptional<IItemHandler> cap = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, dir);
				if (cap.isPresent()) {
					Optional<IItemHandler> nonLazyCap = cap.resolve();
					if (nonLazyCap.isPresent()) {
						IItemHandler handler = nonLazyCap.get();
						for (int i = 0; i < handler.getSlots(); i++) {
							ItemStack stack = handler.getStackInSlot(i);
							if (stack.getItem() instanceof BlockItem bi) {
								Block b = bi.getBlock();
								if (b.canSurvive(b.defaultBlockState(), level, fieldPoint)) {
									level.setBlockAndUpdate(fieldPoint, b.defaultBlockState());
									stack.shrink(1);
									broken = true;
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	@NotNull
	private BlockState disintegrate(BlockPos fieldPoint, BlockState state) {
		if (state.getDestroySpeed(level, fieldPoint) != -1) {
			collect(fieldPoint, state);
			level.setBlockAndUpdate(fieldPoint, Blocks.AIR.defaultBlockState());
			state = Blocks.AIR.defaultBlockState();
		}
		return state;
	}

	private void collect(BlockPos fieldPoint, BlockState state) {
		if (hasCollectionModule) {
			List<ItemStack> items = Block.getDrops(state, (ServerLevel) level, fieldPoint, null);
			for (Direction dir : Direction.values()) {
				BlockEntity entity = level.getBlockEntity(worldPosition.offset(dir.getNormal()));
				if (entity != null) {
					LazyOptional<IItemHandler> cap = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, dir);
					if (cap.isPresent() && cap.resolve().isPresent()) {
						items = InventoryUtils.addItemsToItemHandler(cap.resolve().get(), items);
					}
				}
			}
		}
	}

	public int getMaxFortron() {
		return getFortronUse() * 200 + BASEENERGY;
	}

	public int getFortronUse() {
		if (!level.isClientSide) {
			fortronUse.set(scaleEnergy + speedEnergy + (shouldDisintegrate || shouldStabilize ? 5000 : 0));
		}
		return fortronUse.get();
	}

	public BlockPos getShiftedPos() {
		if (shiftedPosition == null) {
			shiftedPosition.set(worldPosition);
		}
		return shiftedPosition.get();
	}

	@Override
	protected boolean canRecieveFortron(TileFortronConnective tile) {
		return tile instanceof TileFortronCapacitor;
	}

	public boolean isInterior() {
		return isInterior;
	}

	public void updateFieldTerms() {
		isInterior = hasModule(SubtypeModule.upgradeinterior);
		shouldSponge = hasModule(SubtypeModule.upgradesponge);
		shouldDisintegrate = hasModule(SubtypeModule.upgradedisintegration);
		shouldStabilize = hasModule(SubtypeModule.upgradestabilize);
		hasCollectionModule = hasModule(SubtypeModule.upgradecollection);
		totalGeneratedPerTick = 1 + 2 * countModules(SubtypeModule.upgradespeed) / ((shouldSponge ? 2 : 5) / (shouldStabilize ? 2 : 1));
		if (shouldSponge) {
			totalGeneratedPerTick /= 2;
		} else if (shouldDisintegrate) {
			totalGeneratedPerTick /= hasCollectionModule ? 5 : 4;
		} else if (shouldStabilize) {
			totalGeneratedPerTick /= 3;
		}
	}

	private void onChanged(ComponentInventory inv, int index) {
		int count = 0;
		updateFieldTerms();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (i != ContainerFortronFieldProjector.SLOT_TYPE) {
				ItemStack stack = inv.getItem(i);
				if (stack != null) {
					for (Entry<ISubtype, RegistryObject<Item>> en : ModularForcefieldsItems.SUBTYPEITEMREGISTER_MAPPINGS.entrySet()) {
						if (VALIDMODULES.contains(en.getKey())) {
							if (en.getValue().get() == stack.getItem()) {
								count += stack.getCount();
							}
						}
					}
				}
			}
		}
		BlockPos newshiftedPosition = worldPosition.offset(countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_EAST[0], ContainerFortronFieldProjector.SLOT_EAST[1]) - countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_WEST[0], ContainerFortronFieldProjector.SLOT_WEST[1]), countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_UP[0], ContainerFortronFieldProjector.SLOT_UP[1]) - countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_DOWN[0], ContainerFortronFieldProjector.SLOT_DOWN[1]), countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_SOUTH[0], ContainerFortronFieldProjector.SLOT_SOUTH[1]) - countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_NORTH[0], ContainerFortronFieldProjector.SLOT_NORTH[1]));
		int newxRadiusPos = newshiftedPosition.getX() + Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_EAST[0], ContainerFortronFieldProjector.SLOT_EAST[1]));
		int newyRadiusPos = Math.min(getLevel().getMaxBuildHeight(), Math.max(getLevel().getMinBuildHeight(), newshiftedPosition.getY() + countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_UP[0], ContainerFortronFieldProjector.SLOT_UP[1])));
		int newzRadiusPos = newshiftedPosition.getZ() + Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_SOUTH[0], ContainerFortronFieldProjector.SLOT_SOUTH[1]));
		int newxRadiusNeg = newshiftedPosition.getX() - Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_WEST[0], ContainerFortronFieldProjector.SLOT_WEST[1]));
		int newyRadiusNeg = Math.max(getLevel().getMinBuildHeight(), newshiftedPosition.getY() - countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_DOWN[0], ContainerFortronFieldProjector.SLOT_DOWN[1]));
		int newzRadiusNeg = newshiftedPosition.getZ() - Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_NORTH[0], ContainerFortronFieldProjector.SLOT_NORTH[1]));
		int newradius = Math.min(64, countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_MODULES) / 6);
		if (!newshiftedPosition.equals(getShiftedPos()) || xRadiusPos.get() != newxRadiusPos || yRadiusPos.get() != newyRadiusPos || zRadiusPos.get() != newzRadiusPos || xRadiusNeg.get() != newxRadiusNeg || yRadiusNeg.get() != newyRadiusNeg || zRadiusNeg.get() != newzRadiusNeg || radius.get() != newradius) {
			destroyField(false);
		}
		shiftedPosition.set(newshiftedPosition);
		moduleCount.set(count);
		xRadiusPos.set(newxRadiusPos);
		yRadiusPos.set(newyRadiusPos);
		zRadiusPos.set(newzRadiusPos);
		xRadiusNeg.set(newxRadiusNeg);
		yRadiusNeg.set(newyRadiusNeg);
		zRadiusNeg.set(newzRadiusNeg);
		radius.set(newradius);
		scaleEnergy = BASEENERGY * countModules(SubtypeModule.manipulationscale, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
		speedEnergy = 1 + BASEENERGY * countModules(SubtypeModule.upgradespeed, ContainerFortronFieldProjector.SLOT_UPGRADES);
	}

	public ProjectionType getProjectionType() {
		ComponentInventory inv = getComponent(ComponentType.Inventory);
		ItemStack stack = inv.getItem(ContainerFortronFieldProjector.SLOT_TYPE);
		ISubtype subtype = null;
		for (Entry<ISubtype, RegistryObject<Item>> en : ModularForcefieldsItems.SUBTYPEITEMREGISTER_MAPPINGS.entrySet()) {
			if (en.getValue().get() == stack.getItem()) {
				subtype = en.getKey();
			}
		}
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
		return FortronFieldColor.values()[fieldColorOrdinal.get()];
	}
}
