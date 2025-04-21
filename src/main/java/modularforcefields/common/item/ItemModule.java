package modularforcefields.common.item;

import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTab;
import voltaic.common.item.ItemVoltaic;

public class ItemModule extends ItemVoltaic {

    public final SubtypeModule subtype;

    public ItemModule(SubtypeModule subtype, Properties properties, Holder<CreativeModeTab> creativeTab) {
        super(properties, creativeTab);
        this.subtype = subtype;
    }
}
