package modularforcefields.registers;

import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.prefab.utils.MFFSTextUtils;
import net.minecraft.ChatFormatting;
import net.neoforged.bus.api.IEventBus;
import voltaic.common.blockitem.BlockItemDescriptable;
import voltaic.prefab.utilities.VoltaicTextUtils;

public class UnifiedModularForcefieldsRegister {

	public static void register(IEventBus bus) {
		ModularForcefieldsBlocks.BLOCKS.register(bus);
		ModularForcefieldsItems.ITEMS.register(bus);
		ModularForcefieldsDataComponentTypes.DATA_COMPONENT_TYPES.register(bus);
		ModularForcefieldsTiles.BLOCK_ENTITY_TYPES.register(bus);
		ModularForcefieldsMenuTypes.MENU_TYPES.register(bus);
		ModularForcefieldsFluids.FLUIDS.register(bus);
		ModularForcefieldsFluidTypes.FLUID_TYPES.register(bus);
		ModularForcefieldsSounds.SOUNDS.register(bus);
		ModularForcefieldsCreativeTabs.CREATIVE_TABS.register(bus);
	}

	static {

		BlockItemDescriptable.addDescription(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getHolder(SubtypeMFFSMachine.coercionderiver), VoltaicTextUtils.voltageTooltip(480));
		BlockItemDescriptable.addDescription(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getHolder(SubtypeMFFSMachine.coercionderiver), MFFSTextUtils.tooltip("coercionderiver").withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getHolder(SubtypeMFFSMachine.fortroncapacitor), MFFSTextUtils.tooltip("fortroncapacitor").withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getHolder(SubtypeMFFSMachine.fortronfieldprojector), MFFSTextUtils.tooltip("fortronfieldprojector").withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getHolder(SubtypeMFFSMachine.interdictionmatrix), MFFSTextUtils.tooltip("interdictionmatrix").withStyle(ChatFormatting.DARK_GRAY));
		BlockItemDescriptable.addDescription(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getHolder(SubtypeMFFSMachine.biometricidentifier), MFFSTextUtils.tooltip("biometricidentifier").withStyle(ChatFormatting.DARK_GRAY));

	}

}
