package modularforcefields.common.tags;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import voltaic.Voltaic;

public class MFFSTags {

	public static List<TagKey<Fluid>> FLUID_TAGS = new ArrayList<>();

	public static void init() {
		Fluids.init();
	}

	public static List<TagKey<Fluid>> getFluidTags() {
		return FLUID_TAGS;
	}

	// Only the Tag objects should ever be visible from this class!
	public static class Fluids {

		public static final TagKey<Fluid> FORTRON = forgeTag("fortron");

		private static void init() {

		}

		private static TagKey<Fluid> forgeTag(String name) {
			return FluidTags.create(Voltaic.forgerl(name));
		}

	}

}
