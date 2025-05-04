package modularforcefields.prefab.inventory.container.slot.item.type;

import java.util.ArrayList;
import java.util.List;

import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.IconType;
import voltaic.prefab.screen.component.types.ScreenComponentSlot.SlotType;

public class SlotModule extends SlotGeneric {

	private List<Item> items;

	public SlotModule(Container inventory, int index, int x, int y, SubtypeModule... valid) {
		super(SlotType.NORMAL, IconType.UPGRADE_DARK, inventory, index, x, y);

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

}
