package modularforcefields.prefab.inventory.container.slot.item.type;

import java.util.ArrayList;
import java.util.List;

import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import voltaic.prefab.inventory.container.slot.item.SlotGeneric;
import voltaic.prefab.screen.component.types.ScreenComponentSlot;

public class SlotModule extends SlotGeneric {

    private final List<Item> items = new ArrayList<>();

    public SlotModule(Container inventory, int index, int x, int y, SubtypeModule... valid) {
        super(ScreenComponentSlot.SlotType.NORMAL, ScreenComponentSlot.IconType.UPGRADE_DARK, inventory, index, x, y);

        items.addAll(ModularForcefieldsItems.ITEMS_MODULE.getAllValues());
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return items.contains(stack.getItem());
    }

}
