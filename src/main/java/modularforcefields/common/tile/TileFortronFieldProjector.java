package modularforcefields.common.tile;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Sets;

import modularforcefields.common.block.BlockFortronField;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.item.ItemModule;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.settings.MFFSConstants;
import modularforcefields.common.tile.projection.ProjectionType;
import modularforcefields.common.tile.projection.ThreadProjectorCalculationThread;
import modularforcefields.common.world.FortronFieldData;
import modularforcefields.registers.ModularForcefieldsBlocks;
import modularforcefields.registers.ModularForcefieldsItems;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.prefab.utilities.object.Location;

public class TileFortronFieldProjector extends TileFortronConnective {
    public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.values());
    public static final int BASEENERGY = 100;
    private ThreadProjectorCalculationThread calculationThread;
    public Set<BlockPos> calculatedFieldPoints = Collections.synchronizedSet(new HashSet<>());
    public final SingleProperty<Integer> typeOrdinal = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "type", ProjectionType.NONE.ordinal()));
    public final SingleProperty<Integer> fieldColorOrdinal = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "fieldColorOrdinal", DyeColor.LIGHT_BLUE.ordinal()));
    public final SingleProperty<Integer> moduleCount = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "moduleCount", 0));
    public final SingleProperty<Integer> fortronCapacity = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "fortronCapacity", 0));
    public final SingleProperty<Double> health = property(
	    new SingleProperty<>(PropertyTypes.DOUBLE, "health", 1.0).setNoUpdateServer());
    public final SingleProperty<Integer> fortron = property(new SingleProperty<>(PropertyTypes.INTEGER, "fortron", 0));
    public final SingleProperty<Integer> fortronUse = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "fortronUse", 0));
    private final SingleProperty<Integer> statusInteger = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "statusInteger", FortronFieldStatus.PROJECTING.ordinal()));

    public FortronFieldStatus getStatus() {
	return FortronFieldStatus.values()[statusInteger.getValue()];
    }

    public void setStatus(FortronFieldStatus status) {
	statusInteger.setValue(status.ordinal());
    }

    public final SingleProperty<Integer> xRadiusPos = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "xRadiusPos", 0));
    public final SingleProperty<Integer> yRadiusPos = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "yRadiusPos", 0));
    public final SingleProperty<Integer> zRadiusPos = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "zRadiusPos", 0));
    public final SingleProperty<Integer> xRadiusNeg = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "xRadiusNeg", 0));
    public final SingleProperty<Integer> yRadiusNeg = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "yRadiusNeg", 0));
    public final SingleProperty<Integer> zRadiusNeg = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "zRadiusNeg", 0));
    public final SingleProperty<Integer> radius = property(new SingleProperty<>(PropertyTypes.INTEGER, "radius", 0));
    public int scaleEnergy;
    public int speedEnergy;
    public boolean shouldSponge = false;
    public boolean shouldDisintegrate = false;
    public boolean shouldStabilize = false;
    public boolean shouldColor = false;
    public boolean hasCollectionModule = false;
    public boolean isInterior = false;
    public float totalGeneratedPerTick = 0;
    public float tickGenerationProgress = 0;
    public int ticksUntilProjection;
    public final SingleProperty<BlockPos> shiftedPosition = property(
	    new SingleProperty<>(PropertyTypes.BLOCK_POS, "shiftedPosition", BlockPos.ZERO));
    public final SingleProperty<Integer> calculatedSize = property(
	    new SingleProperty<>(PropertyTypes.INTEGER, "calculatedSize", 0));
    public final SingleProperty<Long> rebuildAtGameTime = property(
	    new SingleProperty<>(PropertyTypes.LONG, "rebuildAtGameTime", 0L));
    public final SingleProperty<Long> projectorId = property(
	    new SingleProperty<>(PropertyTypes.LONG, "projectorId", 0L).setNoUpdateServer());

    @Override
    protected int recieveFortron(int amount) {
	int received = Math.max(0, Math.min(amount, fortronCapacity.getValue() - fortron.getValue()));
	fortron.setValue(fortron.getValue() + received);
	return received;
    }

    @Override
    protected Predicate<BlockEntity> getConnectionTest() {
	return TileFortronCapacitor.class::isInstance;
    }

    public TileFortronFieldProjector(BlockPos pos, BlockState state) {
	super(ModularForcefieldsTiles.TILE_FORTRONFIELDPROJECTOR.get(), pos, state);
	addComponent(new ComponentPacketHandler(this));
	addComponent(new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().forceSize(21))
		.valid((index, stack, inv) -> true).onChanged(this::onChanged));
	addComponent(new ComponentContainerProvider("fortronfieldprojector", this)
		.createMenu((id, player) -> new ContainerFortronFieldProjector(id, player,
			getComponent(IComponentType.Inventory), getCoordsArray())));
    }

    @Override
    protected void tickServer(ComponentTickable tickable) {
	super.tickServer(tickable);
	if (tickable.getTicks() % 20 == 0) {
	    fortronCapacity.setValue(getMaxFortron());
	    fortron.setValue(Mth.clamp(fortron.getValue(), 0, fortronCapacity.getValue()));
	    boolean isLit = getBlockState().getValue(VoltaicBlockStates.LIT);
	    boolean shouldLit = fortron.getValue() > 0;
	    if (isLit != shouldLit) {
		level.setBlockAndUpdate(worldPosition, getBlockState().setValue(VoltaicBlockStates.LIT, shouldLit));
	    }
	}
	if (tickable.getTicks() % 1000 == 1) {
	    onChanged(getComponent(IComponentType.Inventory), -1);
	}
	if (getStatus() == FortronFieldStatus.PROJECTED && level instanceof ServerLevel serverLevel) {

	    if (FortronFieldData.get(serverLevel).getFieldCount(projectorId.getValue()) >= calculatedSize.getValue()) {

		setStatus(FortronFieldStatus.PROJECTED_SEALED);
	    }
	}
	ProjectionType projectedType = getProjectionType();
	if (typeOrdinal.getValue() != projectedType.ordinal()) {
	    destroyField();
	    typeOrdinal.setValue(projectedType.ordinal());
	}
	if (getStatus() == FortronFieldStatus.DESTROYING && level instanceof ServerLevel serverLevel) {
	    health.setValue(0.0);
	    if (!FortronFieldData.get(serverLevel).hasFields(projectorId.getValue())) {
		setStatus(FortronFieldStatus.PREPARE);
		/*
		 * A normally disabled field gets the usual 40-tick delay.
		 *
		 * A broken field already has an absolute rebuild deadline, so it doesn't need
		 * an additional 40 ticks afterward.
		 */
		ticksUntilProjection = rebuildAtGameTime.getValue() > 0L ? 0 : 40;
	    }
	}
	if (tickable.getTicks() > 5) {
	    int use = getFortronUse();
	    if (isPoweredByRedstone() && typeOrdinal.getValue() != ProjectionType.NONE.ordinal()
		    && fortron.getValue() >= use) {
		fortron.setValue(fortron.getValue() - use);
		if (getStatus() != FortronFieldStatus.DESTROYING) {
		    if (getStatus() == FortronFieldStatus.PREPARE && calculatedFieldPoints.isEmpty()) {
			long rebuildAt = rebuildAtGameTime.getValue();
			/*
			 * Field was destroyed through health depletion. Wait until the persistent
			 * world-time deadline.
			 */
			if (rebuildAt > 0L && level.getGameTime() < rebuildAt) {
			    // Still broken. Do nothing.
			} else {
			    /*
			     * Deadline has passed.
			     */
			    if (rebuildAt > 0L) {
				rebuildAtGameTime.setValue(0L);
			    }
			    if (ticksUntilProjection > 0) {
				if (fortron.getValue() > use) {
				    ticksUntilProjection--;
				}
			    } else {
				ticksUntilProjection = 40;
				setStatus(FortronFieldStatus.CALCULATING);
				calculationThread = new ThreadProjectorCalculationThread(this);
				calculationThread.start();
				Logger.getGlobal().log(Level.INFO,
					"Started forcefield calculation thread at: " + new Location(worldPosition));
			    }
			}
		    } else if (getStatus() != FortronFieldStatus.CALCULATING && !calculatedFieldPoints.isEmpty()) {
			projectField();
		    } else if (getStatus() == FortronFieldStatus.PROJECTING) {
			setStatus(FortronFieldStatus.PROJECTED);
		    }
		}
	    } else if (getStatus() != FortronFieldStatus.PREPARE) {
		if (fortron.getValue() < use) {
		    ticksUntilProjection = 100;
		}
		destroyField();
	    }
	}
	if (getStatus() == FortronFieldStatus.PROJECTED || getStatus() == FortronFieldStatus.PROJECTED_SEALED) {
	    double currentHealth = health.getValue();
	    if (currentHealth < 0.0) {
		health.setValue(0.0);
		rebuildAtGameTime.setValue(level.getGameTime() + MFFSConstants.BROKEN_FIELD_REBUILD_DELAY);
		destroyField();
		return;
	    }
	    double maxHealth = MFFSConstants.FORTRONFIELD_MAXHEALTH;

	    if (calculatedSize.getValue() <= 0 || totalGeneratedPerTick <= 0.0F) {
		return;
	    }

	    double buildTicks = calculatedSize.getValue() / totalGeneratedPerTick;
	    double healthPerTick = maxHealth / buildTicks;

	    if (currentHealth < maxHealth) {
		health.setValue(Math.min(currentHealth + healthPerTick, maxHealth));
	    }
	}
    }

    private void projectField() {
	setStatus(FortronFieldStatus.PROJECTING);
	Set<BlockPos> finishedQueueItems = new HashSet<>();
	int currentlyGenerated = 0;
	tickGenerationProgress += totalGeneratedPerTick;
	if (tickGenerationProgress >= 1) {
	    for (BlockPos fieldPoint : calculatedFieldPoints) {
		if (currentlyGenerated >= (int) tickGenerationProgress) {
		    break;
		}
		finishedQueueItems.add(fieldPoint);
		BlockState state = level.getBlockState(fieldPoint);
		Block block = state.getBlock();
		if (state.is(ModularForcefieldsBlocks.BLOCK_FORTRONFIELD.get())) {
		    if (integrateExistingFieldPoint(fieldPoint)) {
			continue;
		    }
		}
		if (shouldSponge) {
		    // TODO: IMPLEMENT SPONGE MODULE
		}
		if (shouldDisintegrate) {
		    state = disintegrate(fieldPoint, state);
		}
		if (state.canBeReplaced(new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND,
			new ItemStack(block), new BlockHitResult(Vec3.ZERO, Direction.DOWN, fieldPoint, false)))) {
		    if (shouldStabilize) {
			stabilizeFieldPoint(fieldPoint);
		    } else {
			currentlyGenerated = createNewFieldPoint(currentlyGenerated, fieldPoint);
		    }
		} else if (state.getDestroySpeed(level, fieldPoint) == -1) {
		    calculatedSize.setValue(calculatedSize.getValue() - 1);
		}
	    }
	    calculatedFieldPoints.removeAll(finishedQueueItems);
	    tickGenerationProgress -= currentlyGenerated;
	}

    }

    private boolean integrateExistingFieldPoint(BlockPos fieldPoint) {

	BlockState state = level.getBlockState(fieldPoint);

	if (state.getValue(BlockFortronField.COLOR) != getFieldColor()) {

	    level.setBlockAndUpdate(fieldPoint, state.setValue(BlockFortronField.COLOR, getFieldColor()));
	}

	if (!(level instanceof ServerLevel serverLevel)) {
	    return false;
	}

	FortronFieldData.get(serverLevel).claim(projectorId.getValue(), worldPosition, fieldPoint);

	return true;
    }

    private int createNewFieldPoint(int currentlyGenerated, BlockPos fieldPoint) {

	level.setBlockAndUpdate(fieldPoint, ModularForcefieldsBlocks.BLOCK_FORTRONFIELD.get().defaultBlockState()
		.setValue(BlockFortronField.COLOR, getFieldColor()));

	if (level instanceof ServerLevel serverLevel) {

	    FortronFieldData.get(serverLevel).claim(projectorId.getValue(), worldPosition, fieldPoint);
	}

	return currentlyGenerated + 1;
    }

    private void stabilizeFieldPoint(BlockPos fieldPoint) {
	boolean broken = false;
	for (Direction dir : Direction.values()) { // TODO: Optimize this so it doesnt check all inventories around
	    // every placement.
	    if (broken) {
		break;
	    }
	    BlockEntity entity = level.getBlockEntity(worldPosition.offset(dir.getNormal()));

	    if (entity == null) {
		continue;
	    }

	    IItemHandler handler = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, dir)
		    .orElse(CapabilityUtils.EMPTY_ITEM_HANDLER);

	    if (handler == CapabilityUtils.EMPTY_ITEM_HANDLER) {
		continue;
	    }

	    for (int i = 0; i < handler.getSlots(); i++) {

		ItemStack stack = handler.getStackInSlot(i);

		if (stack.getItem() instanceof BlockItem bi) {

		    Block b = bi.getBlock();

		    if (b.defaultBlockState().canSurvive(level, fieldPoint)) {

			level.setBlockAndUpdate(fieldPoint, b.defaultBlockState());

			stack.shrink(1);

			broken = true;

			break;
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
		if (entity == null) {
		    continue;
		}

		IItemHandler handler = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, dir)
			.orElse(CapabilityUtils.EMPTY_ITEM_HANDLER);

		if (handler == CapabilityUtils.EMPTY_ITEM_HANDLER) {
		    continue;
		}

		for (ItemStack item : items) {

		    for (int targetIndex = 0; targetIndex < handler.getSlots(); targetIndex++) {

			ItemStack remainder = handler.insertItem(targetIndex, item.copy(), false);

			int taken = item.getCount() - remainder.getCount();

			if (taken <= 0) {

			    continue;

			}

			item = remainder;
			item.shrink(taken);

			if (item.isEmpty()) {
			    break;
			}

		    }

		}
	    }
	}
    }

    public void destroyField() {

	setStatus(FortronFieldStatus.DESTROYING);

	calculatedSize.setValue(0);
	tickGenerationProgress = 0.0F;

	if (calculationThread != null) {
	    calculationThread.interrupt();
	    calculationThread = null;
	}

	calculatedFieldPoints.clear();

	if (level instanceof ServerLevel serverLevel) {
	    FortronFieldData.get(serverLevel).releaseAllFields(serverLevel, projectorId.getValue());
	}
    }

    public int getMaxFortron() {
	return getFortronUse() * 200 + BASEENERGY;
    }

    public int getFortronUse() {
	if (!level.isClientSide) {
	    fortronUse.setValue(scaleEnergy + speedEnergy + (shouldDisintegrate || shouldStabilize ? 5000 : 0));
	}
	return fortronUse.getValue();
    }

    public BlockPos getShiftedPos() {
	if (shiftedPosition == null) {
	    shiftedPosition.setValue(worldPosition);
	}
	return shiftedPosition.getValue();
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
	shouldColor = hasModule(SubtypeModule.upgradecolorchange);
	shouldStabilize = hasModule(SubtypeModule.upgradestabilize);
	hasCollectionModule = hasModule(SubtypeModule.upgradecollection);
	totalGeneratedPerTick = 1
		+ 2 * countModules(SubtypeModule.upgradespeed) / ((shouldSponge ? 2 : 5) / (shouldStabilize ? 2 : 1));
	if (shouldSponge) {
	    totalGeneratedPerTick /= 2.0f;
	} else if (shouldDisintegrate) {
	    totalGeneratedPerTick /= hasCollectionModule ? 5.0f : 4.0f;
	} else if (shouldStabilize) {
	    totalGeneratedPerTick /= 3.0f;
	}
    }

    private void onChanged(ComponentInventory inv, int index) {
	int count = 0;
	updateFieldTerms();
	for (int i = 0; i < inv.getContainerSize(); i++) {
	    if (i != ContainerFortronFieldProjector.SLOT_TYPE) {
		ItemStack stack = inv.getItem(i);
		if (stack.isEmpty()) {
		    continue;
		}
		if (ModularForcefieldsItems.ITEMS_MODULE.getAllValues().contains(stack.getItem())) {
		    count += stack.getCount();
		}
	    }
	}
	BlockPos newshiftedPosition = worldPosition.offset(
		countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_EAST[0],
			ContainerFortronFieldProjector.SLOT_EAST[1])
			- countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_WEST[0],
				ContainerFortronFieldProjector.SLOT_WEST[1]),
		countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_UP[0],
			ContainerFortronFieldProjector.SLOT_UP[1])
			- countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_DOWN[0],
				ContainerFortronFieldProjector.SLOT_DOWN[1]),
		countModules(SubtypeModule.manipulationtranslate, ContainerFortronFieldProjector.SLOT_SOUTH[0],
			ContainerFortronFieldProjector.SLOT_SOUTH[1])
			- countModules(SubtypeModule.manipulationtranslate,
				ContainerFortronFieldProjector.SLOT_NORTH[0],
				ContainerFortronFieldProjector.SLOT_NORTH[1]));
	int newxRadiusPos = newshiftedPosition.getX() + Math.min(64, countModules(SubtypeModule.manipulationscale,
		ContainerFortronFieldProjector.SLOT_EAST[0], ContainerFortronFieldProjector.SLOT_EAST[1]));
	int newyRadiusPos = Math.min(getLevel().getMaxBuildHeight(),
		Math.max(getLevel().getMinBuildHeight(),
			newshiftedPosition.getY() + countModules(SubtypeModule.manipulationscale,
				ContainerFortronFieldProjector.SLOT_UP[0], ContainerFortronFieldProjector.SLOT_UP[1])));
	int newzRadiusPos = newshiftedPosition.getZ() + Math.min(64, countModules(SubtypeModule.manipulationscale,
		ContainerFortronFieldProjector.SLOT_SOUTH[0], ContainerFortronFieldProjector.SLOT_SOUTH[1]));
	int newxRadiusNeg = newshiftedPosition.getX() - Math.min(64, countModules(SubtypeModule.manipulationscale,
		ContainerFortronFieldProjector.SLOT_WEST[0], ContainerFortronFieldProjector.SLOT_WEST[1]));
	int newyRadiusNeg = Math.max(getLevel().getMinBuildHeight(),
		newshiftedPosition.getY() - countModules(SubtypeModule.manipulationscale,
			ContainerFortronFieldProjector.SLOT_DOWN[0], ContainerFortronFieldProjector.SLOT_DOWN[1]));
	int newzRadiusNeg = newshiftedPosition.getZ() - Math.min(64, countModules(SubtypeModule.manipulationscale,
		ContainerFortronFieldProjector.SLOT_NORTH[0], ContainerFortronFieldProjector.SLOT_NORTH[1]));
	int newradius = Math.min(64,
		countModules(SubtypeModule.manipulationscale, ContainerFortronFieldProjector.SLOT_MODULES) / 6);
	if (!newshiftedPosition.equals(getShiftedPos()) || xRadiusPos.getValue() != newxRadiusPos
		|| yRadiusPos.getValue() != newyRadiusPos || zRadiusPos.getValue() != newzRadiusPos
		|| xRadiusNeg.getValue() != newxRadiusNeg || yRadiusNeg.getValue() != newyRadiusNeg
		|| zRadiusNeg.getValue() != newzRadiusNeg || radius.getValue() != newradius) {
	    destroyField();
	}
	shiftedPosition.setValue(newshiftedPosition);
	moduleCount.setValue(count);
	xRadiusPos.setValue(newxRadiusPos);
	yRadiusPos.setValue(newyRadiusPos);
	zRadiusPos.setValue(newzRadiusPos);
	xRadiusNeg.setValue(newxRadiusNeg);
	yRadiusNeg.setValue(newyRadiusNeg);
	zRadiusNeg.setValue(newzRadiusNeg);
	radius.setValue(newradius);
	scaleEnergy = BASEENERGY * countModules(SubtypeModule.manipulationscale, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
	speedEnergy = 1
		+ BASEENERGY * countModules(SubtypeModule.upgradespeed, ContainerFortronFieldProjector.SLOT_UPGRADES);
    }

    public ProjectionType getProjectionType() {
	ComponentInventory inv = getComponent(IComponentType.Inventory);
	ItemStack stack = inv.getItem(ContainerFortronFieldProjector.SLOT_TYPE);

	if (stack.isEmpty()) {
	    return ProjectionType.NONE;
	}

	if (stack.getItem() instanceof ItemModule module) {
	    switch (module.subtype) {
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

    public DyeColor getFieldColor() {
	return DyeColor.values()[fieldColorOrdinal.getValue()];
    }

    @Override
    public void onBlockDestroyed() {

	if (!(level instanceof ServerLevel serverLevel)) {
	    return;
	}

	if (calculationThread != null) {

	    calculationThread.interrupt();
	    calculationThread = null;
	}

	calculatedFieldPoints.clear();

	FortronFieldData.get(serverLevel).removeProjector(serverLevel, projectorId.getValue());
    }

    @Override
    public void onLoad() {
	super.onLoad();

	if (!(level instanceof ServerLevel serverLevel)) {
	    return;
	}

	long currentId = projectorId.getValue();

	long registeredId = FortronFieldData.get(serverLevel).registerProjector(currentId, worldPosition);

	if (registeredId != currentId) {
	    projectorId.setValue(registeredId);
	}

	/*
	 * These states rely on runtime-only calculation data, so restart the
	 * calculation after an unload.
	 */
	if (getStatus() == FortronFieldStatus.CALCULATING || getStatus() == FortronFieldStatus.PROJECTING) {

	    calculationThread = null;
	    calculatedFieldPoints.clear();

	    setStatus(FortronFieldStatus.PREPARE);
	    ticksUntilProjection = 0;
	}
    }

    public long getProjectorId() {
	return projectorId.getValue();
    }

    public boolean hasFieldBlocks() {
	return level instanceof ServerLevel serverLevel
		&& FortronFieldData.get(serverLevel).hasFields(projectorId.getValue());
    }
}
