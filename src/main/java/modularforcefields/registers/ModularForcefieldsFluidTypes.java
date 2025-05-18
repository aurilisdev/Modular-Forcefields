package modularforcefields.registers;

import modularforcefields.ModularForcefields;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModularForcefieldsFluidTypes {

	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ModularForcefields.ID);

	public static final RegistryObject<FluidType> FLUID_TYPE_FORTRON = FLUID_TYPES.register("fluidfortron", () -> ModularForcefieldsFluids.FLUID_FORTRON.get().getFluidType());

}
