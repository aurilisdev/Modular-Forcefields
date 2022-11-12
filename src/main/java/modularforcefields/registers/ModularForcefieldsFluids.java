package modularforcefields.registers;

import static electrodynamics.registers.UnifiedElectrodynamicsRegister.supplier;

import modularforcefields.References;
import modularforcefields.common.fluid.types.FluidFortron;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModularForcefieldsFluids {
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, References.ID);
	public static FluidFortron fluidFortron;
	static {
		FLUIDS.register("fluidfortron", supplier(() -> fluidFortron = new FluidFortron()));
	}

}
