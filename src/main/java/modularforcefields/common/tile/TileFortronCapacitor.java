package modularforcefields.common.tile;

import java.util.HashSet;
import java.util.Map.Entry;

import com.google.common.collect.Sets;

import electrodynamics.api.ISubtype;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.registers.ModularForcefieldsBlockTypes;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class TileFortronCapacitor extends TileFortronConnective {
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.upgradespeed, SubtypeModule.upgradecapacity);
	public static final int BASEENERGY = 100;
	public Property<Integer> fortron = property(new Property<>(PropertyType.Integer, "fortron", 0));
	public Property<Integer> fortronCapacity = property(new Property<>(PropertyType.Integer, "fortronCapacity", 0));

	public TileFortronCapacitor(BlockPos pos, BlockState state) {
		super(ModularForcefieldsBlockTypes.TILE_FORTRONCAPACITOR.get(), pos, state);
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentInventory(this, InventoryBuilder.newInv().forceSize(4)).valid((index, stack, inv) -> {
			for (Entry<ISubtype, RegistryObject<Item>> en : ModularForcefieldsItems.SUBTYPEITEMREGISTER_MAPPINGS.entrySet()) {
				if (VALIDMODULES.contains(en.getKey())) {
					if (en.getValue().get() == stack.getItem()) {
						return true;
					}
				}
			}
			return false;

		}));
		addComponent(new ComponentContainerProvider("container.fortroncapacitor", this).createMenu((id, player) -> new ContainerFortronCapacitor(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
	}

	@Override
	protected void tickServer(ComponentTickable tickable) {
		super.tickServer(tickable);
		if (tickable.getTicks() % 20 == 0) {
			onInventoryChange(getComponent(IComponentType.Inventory), 0);
		}
		fortron.set(fortron.get() - sendFortronTo(Math.min(fortron.get(), getTransfer()), this::canSendTo));
	}

	protected boolean canSendTo(BlockEntity entity) {
		if (entity instanceof TileCoercionDeriver) {
			return false;
		}
		if (entity instanceof TileFortronCapacitor capacitor) {
			for (TileFortronConnective connective : connections) {
				if (connective instanceof TileFortronFieldProjector projector) {
					if (projector.activeFields.isEmpty()) {
						continue;
					}
					if (capacitor.connections.contains(projector)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void onInventoryChange(ComponentInventory inv, int slot) {
		super.onInventoryChange(inv, slot);
		int max = getMaxStored();
		fortron.set(Mth.clamp(fortron.get(), 0, max));
		fortronCapacity.set(max);
	}

	private int getMaxStored() {
		return (int) (getTransfer() * 20 + BASEENERGY * countModules(SubtypeModule.upgradecapacity) * 2.0);
	}

	public int getTransfer() {
		return BASEENERGY * 30 + BASEENERGY * countModules(SubtypeModule.upgradespeed);
	}

	@Override
	protected boolean canRecieveFortron(TileFortronConnective tile) {
		return true;
	}

	@Override
	protected int recieveFortron(int amount) {
		int received = Math.max(0, Math.min(amount, fortronCapacity.get() - fortron.get()));
		fortron.set(fortron.get() + received);
		return received;
	}
}