package modularforcefields.registers;

import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.prefab.utils.MFFSTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraftforge.eventbus.api.IEventBus;
import voltaic.common.blockitem.BlockItemDescriptable;
import voltaic.prefab.utilities.VoltaicTextUtils;

public class UnifiedModularForcefieldsRegister {

	public static void register(IEventBus bus) {
		ModularForcefieldsBlocks.BLOCKS.register(bus);
		ModularForcefieldsItems.ITEMS.register(bus);
		ModularForcefieldsTiles.BLOCK_ENTITY_TYPES.register(bus);
		ModularForcefieldsMenuTypes.MENU_TYPES.register(bus);
		ModularForcefieldsFluids.FLUIDS.register(bus);
		ModularForcefieldsFluidTypes.FLUID_TYPES.register(bus);
		ModularForcefieldsSounds.SOUNDS.register(bus);
	}

	static {

		BlockItemDescriptable.addDescription(() -> ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.coercionderiver), VoltaicTextUtils.voltageTooltip(480));
		BlockItemDescriptable.addDescription(() -> ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.coercionderiver), MFFSTextUtils.tooltip("coercionderiver").withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(() -> ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortroncapacitor), MFFSTextUtils.tooltip("fortroncapacitor").withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(() -> ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortronfieldprojector), MFFSTextUtils.tooltip("fortronfieldprojector").withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(() -> ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.interdictionmatrix), MFFSTextUtils.tooltip("interdictionmatrix").withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(() -> ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.biometricidentifier), MFFSTextUtils.tooltip("biometricidentifier").withStyle(ChatFormatting.DARK_GRAY));

	}

}
