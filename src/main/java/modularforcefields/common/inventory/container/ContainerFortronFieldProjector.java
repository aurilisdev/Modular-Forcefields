package modularforcefields.common.inventory.container;

import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.prefab.inventory.container.slot.item.type.SlotModule;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerFortronFieldProjector extends GenericContainerBlockEntity<TileFortronFieldProjector> {

	public ContainerFortronFieldProjector(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(4), new SimpleContainerData(3));
	}

	public ContainerFortronFieldProjector(int id, Inventory pinv, Container inv, ContainerData data) {
		super(DeferredRegisters.CONTAINER_FORTRONFIELDPROJECTOR.get(), id, pinv, inv, data);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		playerInvOffset = 71;
		SubtypeModule[] valid = TileFortronFieldProjector.VALIDMODULES.toArray(new SubtypeModule[0]);
		for (int xSlot = 0; xSlot < 4; xSlot++) {
			for (int ySlot = 0; ySlot < 4; ySlot++) {
				if ((xSlot != 1 || ySlot != 1) && (xSlot != 2 || ySlot != 2) && (xSlot != 1 || ySlot != 2) && (xSlot != 2 || ySlot != 1)) {
					addSlot(new SlotModule(inv, nextIndex(), 91 + 18 * xSlot, 18 + 18 * ySlot, valid));
				}
			}
		}
		for (int xSlot = 0; xSlot < 3; xSlot++) {
			for (int ySlot = 0; ySlot < 2; ySlot++) {
				addSlot(new SlotModule(inv, nextIndex(), 8 + 18 * xSlot, 27 + 18 * ySlot, valid));
			}
		}
	}

}