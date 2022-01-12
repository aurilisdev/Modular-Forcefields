package modularforcefields.common.inventory.container;

import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.tile.TileCoercionDeriver;
import modularforcefields.prefab.inventory.container.slot.item.type.SlotModule;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerCoercionDeriver extends GenericContainerBlockEntity<TileCoercionDeriver> {

	public ContainerCoercionDeriver(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(4), new SimpleContainerData(3));
	}

	public ContainerCoercionDeriver(int id, Inventory pinv, Container inv, ContainerData data) {
		super(DeferredRegisters.CONTAINER_COERCIONDERIVER.get(), id, pinv, inv, data);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		playerInvOffset = 40;
		SubtypeModule[] valid = TileCoercionDeriver.VALIDMODULES.toArray(new SubtypeModule[0]);
		addSlot(new SlotModule(inv, nextIndex(), 9, 87, valid));
		addSlot(new SlotModule(inv, nextIndex(), 154, 47, valid));
		addSlot(new SlotModule(inv, nextIndex(), 154, 67, valid));
		addSlot(new SlotModule(inv, nextIndex(), 154, 87, valid));
	}

}