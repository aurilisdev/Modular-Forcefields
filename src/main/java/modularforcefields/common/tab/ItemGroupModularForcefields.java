package modularforcefields.common.tab;

import electrodynamics.common.block.subtype.SubtypeOre;
import electrodynamics.registers.ElectrodynamicsBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemGroupModularForcefields extends CreativeModeTab {

	public ItemGroupModularForcefields(String label) {
		super(label);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(ElectrodynamicsBlocks.SUBTYPEBLOCKREGISTER_MAPPINGS.get(SubtypeOre.molybdenum).get());
	}
}