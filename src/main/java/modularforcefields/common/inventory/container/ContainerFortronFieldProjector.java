package modularforcefields.common.inventory.container;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	public static final int[] SLOT_UPGRADES = new int[] { 12, 13, 14, 15, 16, 17 };
	public static final int[] SLOT_MODULES = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
	public static final Integer[] SLOT_NORTH = new Integer[] { 4, 6 };
	public static final Integer[] SLOT_SOUTH = new Integer[] { 5, 7 };
	public static final Integer[] SLOT_EAST = new Integer[] { 9, 10 };
	public static final Integer[] SLOT_WEST = new Integer[] { 1, 2 };
	public static final Integer[] SLOT_UP = new Integer[] { 0, 8 };
	public static final Integer[] SLOT_DOWN = new Integer[] { 3, 11 };
	public static final HashMap<List<Integer>, String> SLOT_MAP = new HashMap<>();
	static {
		SLOT_MAP.put(Arrays.asList(SLOT_NORTH), "North");
		SLOT_MAP.put(Arrays.asList(SLOT_SOUTH), "South");
		SLOT_MAP.put(Arrays.asList(SLOT_EAST), "East");
		SLOT_MAP.put(Arrays.asList(SLOT_WEST), "West");
		SLOT_MAP.put(Arrays.asList(SLOT_UP), "Up");
		SLOT_MAP.put(Arrays.asList(SLOT_DOWN), "Down");
	}

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