package modularforcefields.registers;

import modularforcefields.ModularForcefields;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModularForcefieldsFluidTypes {

	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ModularForcefields.ID);

	public static final DeferredHolder<FluidType, FluidType> FLUID_TYPE_FORTRON = FLUID_TYPES.register("fluidfortron", () -> ModularForcefieldsFluids.FLUID_FORTRON.get().getFluidType());

}
