package modularforcefields.registers;

import net.minecraftforge.eventbus.api.IEventBus;

public class UnifiedModularForcefieldsRegister {

	public static void register(IEventBus bus) {
		ModularForcefieldsBlocks.BLOCKS.register(bus);
		ModularForcefieldsItems.ITEMS.register(bus);
		ModularForcefieldsBlockTypes.BLOCK_ENTITY_TYPES.register(bus);
		ModularForcefieldsMenuTypes.MENU_TYPES.register(bus);
		ModularForcefieldsFluids.FLUIDS.register(bus);
		ModularForcefieldsFluidTypes.FLUID_TYPES.register(bus);
		ModularForcefieldsSounds.SOUNDS.register(bus);
	}

}
