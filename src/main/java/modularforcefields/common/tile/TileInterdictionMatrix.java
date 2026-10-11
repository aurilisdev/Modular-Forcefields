package modularforcefields.common.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Sets;

import modularforcefields.ModularForcefields;
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import modularforcefields.common.item.ItemModule;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.registers.ModularForcefieldsDataComponentTypes;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.IItemHandler;
import voltaic.common.block.states.VoltaicBlockStates;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentTickable;

@EventBusSubscriber(modid = ModularForcefields.ID)
public class TileInterdictionMatrix extends TileFortronConnective {
    public static HashMap<TileInterdictionMatrix, AABB> matrices = new HashMap<>();
    public static final int BASEENERGY = 20;
    public static final int MAX_RADIUS = 64;
    public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.values());
    public SingleProperty<Integer> fortron = property(
	    new SingleProperty<>(getPropertyManager(), PropertyTypes.INTEGER, "fortron", 0));
    public SingleProperty<Integer> scaleEnergy = property(
	    new SingleProperty<>(getPropertyManager(), PropertyTypes.INTEGER, "scaleEnergy", 0));
    public SingleProperty<Integer> fortronCapacity = property(
	    new SingleProperty<>(getPropertyManager(), PropertyTypes.INTEGER, "fortronCapacity", 0));
    public int radius;
    public boolean running;
    public boolean antispawn;
    public boolean blockaccess;
    public boolean blockalter;
    private int strength;

    public TileInterdictionMatrix(BlockPos pos, BlockState state) {
	super(ModularForcefieldsTiles.TILE_INTERDICTIONMATRIX.get(), pos, state);
	addComponent(new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().forceSize(18))
		.valid((index, stack, inv) -> true).onChanged(this::onChanged));
	addComponent(new ComponentContainerProvider("interdictionmatrix", this)
		.createMenu((id, player) -> new ContainerInterdictionMatrix(id, player,
			requireComponent(IComponentType.Inventory), getCoordsArray())));
    }

    @Override
    protected void tickCommon(Level level, ComponentTickable tickable) {
	super.tickCommon(level, tickable);
	if (tickable.getTicks() % 20 == 0) {

	}
    }

    private final HashSet<UUID> validPlayers = new HashSet<>();
    private boolean wasRunning;

    @Override
    protected void tickServer(Level level, ComponentTickable tickable) {
	super.tickServer(level, tickable);
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
	    onChanged(requireComponent(IComponentType.Inventory), -1);
	}
	int use = getFortronUse();
	running = false;
	if (fortron.getValue() >= use && isPoweredByRedstone()) {
	    fortron.setValue(fortron.getValue() - use);
	    running = true;
	}
	boolean justActivated = running && !wasRunning;
	wasRunning = running;
	if (justActivated || tickable.getTicks() % 10 == 0) {
	    if (running) {
		validPlayers.clear();
		for (Direction direction : Direction.values()) {
		    BlockEntity entity = level.getBlockEntity(worldPosition.offset(direction.getNormal()));
		    if (entity instanceof TileBiometricIdentifier identifier) {
			for (ItemStack stack : identifier.<ComponentInventory>requireComponent(IComponentType.Inventory)
				.getItems()) {
			    if (stack.has(ModularForcefieldsDataComponentTypes.UUID)) {
				validPlayers.add(stack.get(ModularForcefieldsDataComponentTypes.UUID));
			    }
			}
		    }
		}
		AABB aabb = new AABB(worldPosition).inflate(radius);
		List<LivingEntity> entities = level.getEntities(EntityTypeTest.forClass(LivingEntity.class), aabb,
			LivingEntity::isAlive);
		matrices.put(this, aabb);
		List<SubtypeModule> list = new ArrayList<>();
		for (ItemStack stack : this.<ComponentInventory>requireComponent(IComponentType.Inventory).getItems()) {
		    if (stack.getItem() instanceof ItemModule module) {
			list.add(module.subtype);
		    }
		}
		applyModules(level, list, entities);
	    }
	}
    }

    private void applyModules(Level level, List<SubtypeModule> list, List<LivingEntity> entities) {
	for (LivingEntity entity : entities) {
	    if (entity instanceof Player player) {
		if (validPlayers.contains(player.getUUID()) || player.isCreative()) {
		    continue;
		}
	    }
	    if (list.contains(SubtypeModule.upgradeantifriendly)) {
		if (entity instanceof Animal animal) {
		    animal.hurt(animal.damageSources().magic(), 2 + strength);
		}
	    }
	    if (list.contains(SubtypeModule.upgradeantihostile)) {
		if (entity instanceof Monster monster) {
		    monster.hurt(monster.damageSources().magic(), 2 + strength);
		}
	    }
	    if (list.contains(SubtypeModule.upgradeconfiscate)) {
		if (entity instanceof Player player) {
		    confiscateItems(level, player);
		}
	    }
	    if (list.contains(SubtypeModule.upgradeantipersonnel)) {
		if (entity instanceof Player player) {
		    player.hurt(player.damageSources().magic(), 2 + strength);
		}
	    }
	}
    }

    @Override
    protected int recieveFortron(int amount) {
	int received = Math.max(0, Math.min(amount, fortronCapacity.getValue() - fortron.getValue()));
	fortron.setValue(fortron.getValue() + received);
	return received;
    }

    private void confiscateItems(Level level, Player player) {
	BlockEntity above = level.getBlockEntity(worldPosition.above());
	if (above == null)
	    return;

	IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, above.getBlockPos(),
		above.getBlockState(), above, Direction.DOWN);
	if (handler == null)
	    return;

	List<ItemStack> stacks = player.getInventory().items;
	for (int index = 0; index < stacks.size(); index++) {
	    stacks.set(index, addItemToItemHandler(stacks.get(index), handler));
	}
	stacks = player.getInventory().armor;
	for (int index = 0; index < stacks.size(); index++) {
	    stacks.set(index, addItemToItemHandler(stacks.get(index), handler));
	}
	stacks = player.getInventory().offhand;
	for (int index = 0; index < stacks.size(); index++) {
	    stacks.set(index, addItemToItemHandler(stacks.get(index), handler));
	}
	player.getInventory().setChanged();
    }

    private static ItemStack addItemToItemHandler(ItemStack item, IItemHandler handler) {
	ItemStack remainder = item.copy();
	for (int slot = 0; slot < handler.getSlots() && !remainder.isEmpty(); slot++) {
	    remainder = handler.insertItem(slot, remainder, false);
	}
	return remainder;
    }

    private boolean isActiveIn(LevelAccessor other) {
	if (!running || isRemoved() || level == null) {
	    return false;
	}
	return other == level || other instanceof ServerLevelAccessor server && server.getLevel() == level;
    }

    @SubscribeEvent
    public static void spawnLiving(MobSpawnEvent.SpawnPlacementCheck event) {
	for (Entry<TileInterdictionMatrix, AABB> en : matrices.entrySet()) {
	    if (event.getLevel().isClientSide() || !en.getKey().isActiveIn(event.getLevel()) || !en.getKey().antispawn) {
		continue;
	    }
	    if (en.getValue().intersects(event.getEntityType().getSpawnAABB(event.getPos().getX(),
		    event.getPos().getY(), event.getPos().getZ()))) {
		event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
		return;
	    }
	}
    }

    @SubscribeEvent
    public static void antiAccessBlockRight(PlayerInteractEvent.RightClickBlock event) {
	for (Entry<TileInterdictionMatrix, AABB> en : matrices.entrySet()) {
	    if (event.getLevel().isClientSide() || !en.getKey().isActiveIn(event.getLevel()) || !en.getKey().blockaccess) {
		continue;
	    }
	    if (!en.getValue().contains(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ())) {
		continue;
	    }
	    Player player = event.getEntity();
	    if (en.getKey().validPlayers.contains(player.getUUID()) || player.isCreative()) {
		continue;
	    }
	    event.setCanceled(true);
	    event.setCancellationResult(InteractionResult.FAIL);
	    return;
	}
    }

    @SubscribeEvent
    public static void antiAccessBlockLeft(PlayerInteractEvent.LeftClickBlock event) {
	for (Entry<TileInterdictionMatrix, AABB> en : matrices.entrySet()) {
	    if (event.getLevel().isClientSide() || !en.getKey().isActiveIn(event.getLevel()) || !en.getKey().blockaccess) {
		continue;
	    }
	    if (!en.getValue().contains(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ())) {
		continue;
	    }
	    Player player = event.getEntity();
	    if (en.getKey().validPlayers.contains(player.getUUID()) || player.isCreative()) {
		continue;
	    }
	    event.setCanceled(true);
	    return;
	}
    }

    @SubscribeEvent
    public static void antiAccess(BlockEvent.BreakEvent event) {
	for (Entry<TileInterdictionMatrix, AABB> en : matrices.entrySet()) {
	    if (event.getLevel().isClientSide() || !en.getKey().isActiveIn(event.getLevel()) || !en.getKey().blockalter) {
		continue;
	    }
	    if (!en.getValue().contains(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ())) {
		continue;
	    }
	    Player player = event.getPlayer();
	    if (en.getKey().validPlayers.contains(player.getUUID()) || player.isCreative()) {
		continue;
	    }
	    event.setCanceled(true);
	    return;
	}
    }

    @SubscribeEvent
    public static void antiAccess(BlockEvent.EntityPlaceEvent event) {
	for (Entry<TileInterdictionMatrix, AABB> en : matrices.entrySet()) {
	    if (event.getLevel().isClientSide() || !en.getKey().isActiveIn(event.getLevel()) || !en.getKey().blockalter) {
		continue;
	    }
	    if (!en.getValue().contains(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ())) {
		continue;
	    }
	    if (event.getEntity() instanceof Player player) {
		if (en.getKey().validPlayers.contains(player.getUUID()) || player.isCreative()) {
		    continue;
		}
	    }
	    event.setCanceled(true);
	    return;
	}
    }

    public int getMaxFortron() {
	return (int) Math.min(Integer.MAX_VALUE, (long) getFortronUse() * 40L + BASEENERGY * 100L);
    }

    public int getFortronUse() {
	return scaleEnergy.getValue();
    }

    @Override
    protected boolean canRecieveFortron(TileFortronConnective tile) {
	return tile instanceof TileFortronCapacitor;
    }

    private void onChanged(ComponentInventory inv, int index) {
	radius = Mth.clamp(countModules(SubtypeModule.manipulationscale), 0, MAX_RADIUS);
	strength = Math.max(0, countModules(SubtypeModule.upgradestrength));
	long use = (BASEENERGY + (long) strength) * radius * radius * radius;
	scaleEnergy.setValue((int) Math.min(use, Integer.MAX_VALUE / 41L));
	antispawn = hasModule(SubtypeModule.upgradeantispawn);
	blockaccess = hasModule(SubtypeModule.upgradeblockaccess);
	blockalter = hasModule(SubtypeModule.upgradeblockalter);
	if (matrices.containsKey(this)) {
	    matrices.put(this, new AABB(worldPosition).inflate(radius));
	}
    }

    @Override
    public void setRemoved() {
	super.setRemoved();
	matrices.remove(this);
    }

    @Override
    public void onChunkUnloaded() {
	super.onChunkUnloaded();
	matrices.remove(this);
    }
}
