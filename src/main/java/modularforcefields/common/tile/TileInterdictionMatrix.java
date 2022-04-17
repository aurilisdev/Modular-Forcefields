package modularforcefields.common.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.Sets;

import electrodynamics.api.ISubtype;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.InventoryUtils;
import modularforcefields.DeferredRegisters;
import modularforcefields.References;
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@EventBusSubscriber(bus = Bus.FORGE, modid = References.ID)
public class TileInterdictionMatrix extends TileFortronConnective {
	public static HashMap<TileInterdictionMatrix, AABB> matrices = new HashMap<>();
	public static final int BASEENERGY = 100;
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.values());
	public int fortronCapacity;
	public int fortron;
	public int radius;
	public int frequency;
	public boolean running;
	public boolean antispawn;
	public boolean blockaccess;
	public boolean blockalter;
	private int scaleEnergy;
	private int strength;

	public TileInterdictionMatrix(BlockPos pos, BlockState state) {
		super(DeferredRegisters.TILE_INTERDICTIONMATRIX.get(), pos, state);
		addComponent(new ComponentDirection());
		addComponent(new ComponentPacketHandler().guiPacketWriter(this::saveAdditional).guiPacketReader(this::load));
		addComponent(new ComponentInventory(this).size(18).shouldSendInfo().valid((index, stack, inv) -> true).onChanged(this::onChanged));
		addComponent(new ComponentContainerProvider("container.interdictionmatrix").createMenu((id, player) -> new ContainerInterdictionMatrix(id, player, getComponent(ComponentType.Inventory), getCoordsArray())));
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
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	@Override
	public int getFrequency() {
		return frequency;
	}

	private HashSet<UUID> validPlayers = new HashSet<>();

	@Override
	protected void tickServer(ComponentTickable tickable) {
		super.tickServer(tickable);
		if (tickable.getTicks() % 1000 == 1) {
			onChanged(getComponent(ComponentType.Inventory));
		}
		int use = getFortronUse();
		running = false;
		if (fortron >= use) {
			fortron -= use;
			running = true;
		}
		running = true;
		validPlayers.clear();
		if (tickable.getTicks() % 10 == 0) {
			if (running) {
				for (Direction direction : Direction.values()) {
					BlockEntity entity = level.getBlockEntity(worldPosition.offset(direction.getNormal()));
					if (entity instanceof TileBiometricIdentifier identifier) {
						for (ItemStack stack : identifier.<ComponentInventory>getComponent(ComponentType.Inventory).getItems()) {
							if (stack.hasTag()) {
								UUID id = stack.getTag().getUUID("player");
								if (id != null) {
									validPlayers.add(id);
								}
							}
						}
					}
				}
				AABB aabb = new AABB(worldPosition).inflate(radius);
				List<LivingEntity> entities = level.getEntities(EntityTypeTest.forClass(LivingEntity.class), aabb, l -> l.isAlive());
				matrices.put(this, aabb);
				List<SubtypeModule> list = new ArrayList<>();
				for (ItemStack stack : this.<ComponentInventory>getComponent(ComponentType.Inventory).getItems()) {
					ISubtype subtype = DeferredRegisters.ITEMSUBTYPE_MAPPINGS.get(stack.getItem());
					if (subtype instanceof SubtypeModule module) {
						list.add(module);
					}
				}
				applyModules(list, entities);
			}
		}
	}

	private void applyModules(List<SubtypeModule> list, List<LivingEntity> entities) {
		for (LivingEntity entity : entities) {
			if (entity instanceof Player player) {
				if (validPlayers.contains(player.getUUID()) || player.isCreative()) {
					continue;
				}
			}
			if (list.contains(SubtypeModule.upgradeantifriendly)) {
				if (entity instanceof Animal animal) {
					animal.hurt(DamageSource.MAGIC, 5 + strength);
				}
			}
			if (list.contains(SubtypeModule.upgradeantihostile)) {
				if (entity instanceof Monster monster) {
					monster.hurt(DamageSource.MAGIC, 5 + strength);
				}
			}
			if (list.contains(SubtypeModule.upgradeconfiscate)) {
				if (entity instanceof Player player) {
					BlockEntity above = level.getBlockEntity(worldPosition.above());
					LazyOptional<IItemHandler> cap = above.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN);
					if (cap.isPresent()) {
						List<ItemStack> stacks = player.getInventory().items;
						IItemHandler handler = cap.resolve().get();
						for (int index = 0; index < stacks.size(); index++) {
							ItemStack back = InventoryUtils.addItemToItemHandler(handler, stacks.get(index), 0, handler.getSlots());
							player.getInventory().items.set(index, back);
						}
						stacks = player.getInventory().items;
						for (int index = 0; index < stacks.size(); index++) {
							ItemStack back = InventoryUtils.addItemToItemHandler(handler, stacks.get(index), 0, handler.getSlots());
							player.getInventory().items.set(index, back);
						}
						stacks = player.getInventory().offhand;
						for (int index = 0; index < stacks.size(); index++) {
							ItemStack back = InventoryUtils.addItemToItemHandler(handler, stacks.get(index), 0, handler.getSlots());
							player.getInventory().items.set(index, back);
						}

					}
				}
			}
			if (list.contains(SubtypeModule.upgradeantipersonnel)) {
				if (entity instanceof Player player) {
					player.hurt(DamageSource.MAGIC, 100);
				}
			}
			antispawn = list.contains(SubtypeModule.upgradeantispawn);
			blockaccess = list.contains(SubtypeModule.upgradeblockaccess);
			blockalter = list.contains(SubtypeModule.upgradeblockalter);
		}
	}

	@SubscribeEvent
	public static void spawnLiving(LivingSpawnEvent event) {
		for (Entry<TileInterdictionMatrix, AABB> en : matrices.entrySet()) {
			if (en.getKey().running && !en.getKey().isRemoved() && en.getKey().antispawn) {
				if (en.getValue().intersects(event.getEntity().getBoundingBox())) {
					event.setCanceled(true);
					event.setResult(Result.DENY);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void antiAccess(PlayerInteractEvent event) {
		for (Entry<TileInterdictionMatrix, AABB> en : matrices.entrySet()) {
			if (en.getKey().running && !en.getKey().isRemoved() && en.getKey().blockaccess) {
				if (en.getValue().contains(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ())) {
					Player player = event.getPlayer();
					if (en.getKey().validPlayers.contains(player.getUUID()) || player.isCreative()) {
						continue;
					}
					event.setCanceled(true);
					event.setResult(Result.DENY);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void antiAccess(BreakEvent event) {
		for (Entry<TileInterdictionMatrix, AABB> en : matrices.entrySet()) {
			if (en.getKey().running && !en.getKey().isRemoved() && en.getKey().blockalter) {
				if (en.getValue().contains(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ())) {
					Player player = event.getPlayer();
					if (en.getKey().validPlayers.contains(player.getUUID()) || player.isCreative()) {
						continue;
					}
					event.setCanceled(true);
					event.setResult(Result.DENY);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public static void antiAccess(EntityPlaceEvent event) {
		for (Entry<TileInterdictionMatrix, AABB> en : matrices.entrySet()) {
			if (en.getKey().running && !en.getKey().isRemoved() && en.getKey().blockalter) {
				if (en.getValue().contains(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ())) {
					if (event.getEntity() instanceof Player player) {
						if (en.getKey().validPlayers.contains(player.getUUID()) || player.isCreative()) {
							continue;
						}
					}
					event.setCanceled(true);
					event.setResult(Result.DENY);
					return;
				}
			}

		}
	}

	public int getMaxFortron() {
		return getFortronUse() * 40 + BASEENERGY;
	}

	public int getFortronUse() {
		return scaleEnergy;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("frequency", frequency);
		tag.putInt("fortronCapacity", fortronCapacity);
		tag.putInt("fortron", fortron);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		frequency = tag.getInt("frequency");
		fortronCapacity = tag.getInt("fortronCapacity");
		fortron = tag.getInt("fortron");
	}

	@Override
	protected boolean canRecieveFortron(TileFortronConnective tile) {
		return tile instanceof TileFortronCapacitor;
	}

	private void onChanged(ComponentInventory inv) {
		radius = countModules(SubtypeModule.manipulationscale);
		strength = countModules(SubtypeModule.upgradestrength);
		scaleEnergy = (BASEENERGY + strength) * radius * radius * radius;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		matrices.remove(this);
	}
}
