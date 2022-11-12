package modularforcefields.common.fluid.types;

import electrodynamics.common.fluid.FluidNonPlaceable;
import electrodynamics.common.fluid.types.SimpleWaterBasedFluidType;
import electrodynamics.registers.ElectrodynamicsItems;
import modularforcefields.References;
import net.minecraftforge.fluids.FluidType;

public class FluidFortron extends FluidNonPlaceable {

	public static final String FORGE_TAG = "fortron";

	private final FluidType type;

	public FluidFortron() {
		super(() -> ElectrodynamicsItems.ITEM_CANISTERREINFORCED);
		type = new SimpleWaterBasedFluidType(References.ID, "fortron");
	}

	@Override
	public FluidType getFluidType() {
		return type;
	}
}
