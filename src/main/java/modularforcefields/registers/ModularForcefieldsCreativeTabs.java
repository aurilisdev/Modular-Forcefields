package modularforcefields.registers;

import modularforcefields.References;
import modularforcefields.prefab.utils.MFFSTextUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModularForcefieldsCreativeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, References.ID);

	public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder().title(MFFSTextUtils.creativeTab("main")).icon(() -> new ItemStack(ModularForcefieldsBlocks.blockCoercionDeriver)).build());

}
