package modularforcefields.registers;

import modularforcefields.ModularForcefields;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voltaic.common.fluid.FluidNonPlaceable;
import voltaic.common.fluid.SimpleWaterBasedFluidType;
import voltaic.prefab.utilities.math.Color;

public class ModularForcefieldsFluids {
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, ModularForcefields.ID);

	public static final DeferredHolder<Fluid, FluidNonPlaceable> FLUID_FORTRON = FLUIDS.register("fluidfortron", () -> new FluidNonPlaceable(Items.AIR.builtInRegistryHolder(), new SimpleWaterBasedFluidType(ModularForcefields.ID, "fluidfortron", "fortron", Color.WHITE)));


}
