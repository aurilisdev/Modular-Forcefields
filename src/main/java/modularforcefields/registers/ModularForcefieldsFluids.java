package modularforcefields.registers;

import modularforcefields.ModularForcefields;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import voltaic.common.fluid.FluidNonPlaceable;
import voltaic.common.fluid.SimpleWaterBasedFluidType;
import voltaic.prefab.utilities.math.Color;

public class ModularForcefieldsFluids {
	
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ModularForcefields.ID);

	public static final RegistryObject<FluidNonPlaceable> FLUID_FORTRON = FLUIDS.register("fluidfortron", () -> new FluidNonPlaceable(() -> Items.AIR, new SimpleWaterBasedFluidType(ModularForcefields.ID, "fluidfortron", "fortron", Color.WHITE)));


}
