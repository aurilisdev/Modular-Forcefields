package modularforcefields.registers;

import static modularforcefields.registers.ModularForcefieldsFluids.fluidFortron;

import modularforcefields.References;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModularForcefieldsFluidTypes {
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, References.ID);

	static {
		FLUID_TYPES.register("fluidfortron", () -> fluidFortron.getFluidType());
	}
}
