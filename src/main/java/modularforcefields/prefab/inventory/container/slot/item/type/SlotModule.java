package modularforcefields.prefab.inventory.container.slot.item.type;

import java.util.ArrayList;
import java.util.List;

import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.screen.component.ScreenComponentSlot.EnumSlotType;
import modularforcefields.DeferredRegisters;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SlotModule extends SlotGeneric {

	private List<Item> items;

	public SlotModule(Container inventory, int index, int x, int y, SubtypeModule... valid) {
		super(inventory, index, x, y);

		items = new ArrayList<>();
		for (SubtypeModule upg : valid) {
			items.add(DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(upg));
		}
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return items != null && items.contains(stack.getItem());
	}

	@Override
	public EnumSlotType getSlotType() {
		return EnumSlotType.SPEED;
	}

}
