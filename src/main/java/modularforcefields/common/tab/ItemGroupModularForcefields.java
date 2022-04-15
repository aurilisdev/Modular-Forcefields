package modularforcefields.common.tab;

import electrodynamics.common.block.subtype.SubtypeOre;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemGroupModularForcefields extends CreativeModeTab {

	public ItemGroupModularForcefields(String label) {
		super(label);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(electrodynamics.DeferredRegisters.SUBTYPEBLOCKREGISTER_MAPPINGS.get(SubtypeOre.molybdenum).get());
	}
}