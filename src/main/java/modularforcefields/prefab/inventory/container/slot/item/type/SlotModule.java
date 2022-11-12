package modularforcefields.prefab.inventory.container.slot.item.type;

import java.util.ArrayList;
import java.util.List;

import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.screen.component.ScreenComponentSlot.EnumSlotType;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

public class SlotModule extends SlotGeneric {

	private List<Item> items;

	public SlotModule(Container inventory, int index, int x, int y, SubtypeModule... valid) {
		super(inventory, index, x, y);

		items = new ArrayList<>();
		for (SubtypeModule upg : valid) {
			RegistryObject<Item> object = ModularForcefieldsItems.SUBTYPEITEMREGISTER_MAPPINGS.get(upg);
			if (object != null) {
				items.add(object.get());
			}
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
