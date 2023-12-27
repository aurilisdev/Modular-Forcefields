package modularforcefields.common.tile;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.google.common.collect.Sets;

import electrodynamics.api.ISubtype;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.object.TransferPack;
import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.settings.Constants;
import modularforcefields.registers.ModularForcefieldsBlockTypes;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public class TileCoercionDeriver extends TileFortronConnective {
	public static final HashSet<SubtypeModule> VALIDMODULES = Sets.newHashSet(SubtypeModule.upgradespeed, SubtypeModule.upgradecapacity);
	public static final int BASEENERGY = 50;
	public Property<Integer> fortron = property(new Property<>(PropertyType.Integer, "fortron", 0));
	public Property<Integer> fortronCapacity = property(new Property<>(PropertyType.Integer, "fortronCapacity", 0));

	public TileCoercionDeriver(BlockPos pos, BlockState state) {
		super(ModularForcefieldsBlockTypes.TILE_COERCIONDERIVER.get(), pos, state);
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentElectrodynamic(this, false, true).voltage(Constants.COERCIONDERIVER_VOLTAGE).setInputDirections(Direction.DOWN));
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
		addComponent(new ComponentContainerProvider("container.coercionderiver", this).createMenu((id, player) -> new ContainerCoercionDeriver(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
	}

	@Override
	protected void tickServer(ComponentTickable tickable) {
		super.tickServer(tickable);
		if (tickable.getTicks() % 20 == 0) {
			onInventoryChange(getComponent(IComponentType.Inventory), 0);
		}
		ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);
		fortron.set((int) (fortron.get() + electro.extractPower(TransferPack.joulesVoltage(Math.min(getTransfer(), fortronCapacity.get() - fortron.get()), electro.getVoltage()), false).getJoules()));
		fortron.set(fortron.get() - sendFortronTo(Math.min(fortron.get(), getTransfer()), getConnectionTest()));
	}

	@Override
	public void onInventoryChange(ComponentInventory inv, int slot) {
		super.onInventoryChange(inv, slot);
		int max = getMaxStored();
		ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);
		electro.maxJoules(max);
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
	protected Predicate<BlockEntity> getConnectionTest() {
		return b -> b instanceof TileFortronCapacitor;
	}
}