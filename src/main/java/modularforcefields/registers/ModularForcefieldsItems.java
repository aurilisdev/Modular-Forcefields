package modularforcefields.registers;

import java.util.ArrayList;
import java.util.List;

import modularforcefields.ModularForcefields;
import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.common.item.ItemFortronFrequencyCard;
import modularforcefields.common.item.ItemIdentificationCard;
import modularforcefields.common.item.ItemModule;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import voltaic.api.creativetab.CreativeTabSupplier;
import voltaic.api.registration.BulkRegistryObject;
import voltaic.common.blockitem.BlockItemDescriptable;
import voltaic.common.item.ItemVoltaic;

public class ModularForcefieldsItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
	    ModularForcefields.ID);

    public static final BulkRegistryObject<BlockItemDescriptable, SubtypeMFFSMachine> ITEMS_MFFSMACHINE = new BulkRegistryObject<>(
	    SubtypeMFFSMachine.values(),
	    subtype -> ITEMS.register(subtype.tag(),
		    () -> new BlockItemDescriptable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(subtype),
			    new Item.Properties(), ModularForcefieldsCreativeTabs.MAIN)));
    public static final BulkRegistryObject<ItemModule, SubtypeModule> ITEMS_MODULE = new BulkRegistryObject<>(
	    SubtypeModule.values(), subtype -> ITEMS.register(subtype.tag(),
		    () -> new ItemModule(subtype, new Item.Properties(), ModularForcefieldsCreativeTabs.MAIN)));
    public static final RegistryObject<Item> ITEM_FOCUSMATRIX = ITEMS.register("focusmatrix",
	    () -> new ItemVoltaic(new Item.Properties(), ModularForcefieldsCreativeTabs.MAIN));
    public static final RegistryObject<Item> ITEM_IDENTIFICATIONCARD = ITEMS.register("identificationcard",
	    () -> new ItemIdentificationCard(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ITEM_FREQUENCYCARD = ITEMS.register("frequencycard",
	    () -> new ItemFortronFrequencyCard(new Item.Properties().stacksTo(1)));

    @EventBusSubscriber(value = Dist.CLIENT, modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD)
    private static class MFFSCreativeRegistry {

	@SubscribeEvent
	public static void registerItems(BuildCreativeModeTabContentsEvent event) {

	    ITEMS.getEntries().forEach(reg -> {

		CreativeTabSupplier supplier = (CreativeTabSupplier) reg.get();

		if (supplier.hasCreativeTab() && supplier.isAllowedInCreativeTab(event.getTab())) {
		    List<ItemStack> toAdd = new ArrayList<>();
		    supplier.addCreativeModeItems(event.getTab(), toAdd);
		    event.acceptAll(toAdd);
		}

	    });

	}

    }
}
