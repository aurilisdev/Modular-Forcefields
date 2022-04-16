package modularforcefields.common.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.compress.utils.Sets;

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
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@EventBusSubscriber(bus = Bus.MOD, modid = References.ID)
public class TileInterdictionMatrix extends TileFortronConnective {
	public static final int BASEENERGY = 100;
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.values());
	public int fortronCapacity;
	public int fortron;
	public int radius;
	public int frequency;
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

	@Override
	protected void tickServer(ComponentTickable tickable) {
		super.tickServer(tickable);
		if (tickable.getTicks() % 1000 == 1) {
			onChanged(getComponent(ComponentType.Inventory));
		}
		if (tickable.getTicks() % 10 == 0) {
			int use = getFortronUse();
			if (fortron >= use) {
				fortron -= use;
				List<LivingEntity> entities = level.getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(worldPosition).inflate(radius), l -> l.isAlive());
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
			if (list.contains(SubtypeModule.upgradeantispawn)) {
				// TODO: Implement some global list with aabbs and matrices that events can check
			}
			if (list.contains(SubtypeModule.upgradeblockaccess)) {
				// TODO: Implement some global list with aabbs and matrices that events can check
			}
			if (list.contains(SubtypeModule.upgradeblockalter)) {
				// TODO: Implement some global list with aabbs and matrices that events can check
			}
		}

	}

	public int getMaxFortron() {
		return getFortronUse() * 40 + BASEENERGY;
	}

	public int getFortronUse() {
		return scaleEnergy * -1;
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
		radius = countModules(SubtypeModule.manipulationscale, 0, 11);
		strength = countModules(SubtypeModule.upgradestrength, 0, 11);
		scaleEnergy = BASEENERGY * radius * radius * radius;
	}
}
