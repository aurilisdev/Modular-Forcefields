package modularforcefields.common.tags;

import java.util.ArrayList;
import java.util.List;

import electrodynamics.common.item.gear.tools.ItemCanister;
import modularforcefields.common.fluid.types.FluidFortron;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;

public class MFFTags {
	
	public static List<Tags.IOptionalNamedTag<Fluid>> FLUID_TAGS = new ArrayList<>();

	public static void init() {
		Fluids.init();
	}

	public static List<Tags.IOptionalNamedTag<Fluid>> getFluidTags() {
		return FLUID_TAGS;
	}

	// Only the Tag objects should ever be visible from this class!
	public static class Fluids {
		
		public static final Tags.IOptionalNamedTag<Fluid> FORTRON = forgeTag(FluidFortron.FORGE_TAG);
		
		private static void init() {
			FLUID_TAGS.add(FORTRON);
			
			ItemCanister.addTag(FORTRON);
		}
		
		private static Tags.IOptionalNamedTag<Fluid> forgeTag(String name) {
			return FluidTags.createOptional(new ResourceLocation("forge", name));
		}
		
	}

}
