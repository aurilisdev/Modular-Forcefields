package modularforcefields.common.fluid.types;

import electrodynamics.common.fluid.FluidNonPlaceable;
import electrodynamics.registers.ElectrodynamicsItems;
import modularforcefields.References;

public class FluidFortron extends FluidNonPlaceable {

	public static final String FORGE_TAG = "fortron";

	public FluidFortron() {
		super(() -> ElectrodynamicsItems.ITEM_CANISTERREINFORCED, References.ID, "fortron");
	}

}
