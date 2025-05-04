package modularforcefields.common.fluid.types;

import modularforcefields.References;
import net.minecraft.world.item.Items;
import voltaic.common.fluid.FluidNonPlaceable;
import voltaic.common.fluid.SimpleWaterBasedFluidType;

public class FluidFortron extends FluidNonPlaceable {

    public static final String FORGE_TAG = "fortron";

    public FluidFortron() {
	super(() -> Items.AIR, new SimpleWaterBasedFluidType(References.ID, "fortron", "fortron"));
    }

}
