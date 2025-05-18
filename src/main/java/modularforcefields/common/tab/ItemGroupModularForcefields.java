package modularforcefields.common.tab;

import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.registers.ModularForcefieldsBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ItemGroupModularForcefields extends CreativeModeTab {

	public ItemGroupModularForcefields(String label) {
		super(label);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.coercionderiver));
	}
}