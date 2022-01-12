package modularforcefields.common.fluid.types;

import electrodynamics.DeferredRegisters;
import electrodynamics.common.fluid.FluidNonPlaceable;
import modularforcefields.References;

public class FluidFortron extends FluidNonPlaceable {

	public static final String FORGE_TAG = "fortron";

	public FluidFortron() {
		super(() -> DeferredRegisters.ITEM_CANISTERREINFORCED, References.ID, "fortron", -428574419);
	}

}
