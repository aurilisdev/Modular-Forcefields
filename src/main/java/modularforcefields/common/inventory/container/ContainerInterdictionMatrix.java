package modularforcefields.common.inventory.container;

import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.tile.TileInterdictionMatrix;
import modularforcefields.prefab.inventory.container.slot.item.type.SlotModule;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ContainerInterdictionMatrix extends GenericContainerBlockEntity<TileInterdictionMatrix> {

	public ContainerInterdictionMatrix(int id, Inventory playerinv) {
		this(id, playerinv, new SimpleContainer(18), new SimpleContainerData(3));
	}

	public ContainerInterdictionMatrix(int id, Inventory pinv, Container inv, ContainerData data) {
		super(DeferredRegisters.CONTAINER_INTERDICTIONMATRIX.get(), id, pinv, inv, data);
	}

	@Override
	public void addInventorySlots(Container inv, Inventory playerinv) {
		playerInvOffset = 51;
		SubtypeModule[] valid = TileInterdictionMatrix.VALIDMODULES.toArray(new SubtypeModule[0]);
		addSlot(new SlotModule(inv, nextIndex(), 152, 100, valid));
		for (int xSlot = 0; xSlot < 2; xSlot++) {
			for (int ySlot = 0; ySlot < 4; ySlot++) {
				addSlot(new SlotModule(inv, nextIndex(), 98 + ySlot * 18, 31 + 18 + xSlot * 18, valid));
			}
		}
		for (int var4 = 0; var4 < 9; var4++) {
			addSlot(new SlotModule(inv, nextIndex(), 8 + var4 * 18, 31));
		}

	}

}