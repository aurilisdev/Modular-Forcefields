package modularforcefields.registers;

import static modularforcefields.registers.ModularForcefieldsBlocks.blockBiometricIdentifier;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockCoercionDeriver;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockFortronCapacitor;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockFortronField;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockFortronFieldProjector;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockInterdictionMatrix;

import java.util.HashMap;
import java.util.Map.Entry;

import electrodynamics.api.ISubtype;
import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.common.item.ItemElectrodynamics;
import modularforcefields.References;
import modularforcefields.common.item.ItemFortronFrequencyCard;
import modularforcefields.common.item.ItemIdentificationCard;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModularForcefieldsItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, References.ID);
	public static final HashMap<ISubtype, RegistryObject<Item>> SUBTYPEITEMREGISTER_MAPPINGS = new HashMap<>();
	public static final HashMap<Item, ISubtype> ITEMSUBTYPE_MAPPINGS = new HashMap<>();

	static {
		ITEMS.register("biometricidentifier", () -> new BlockItemDescriptable(() -> blockBiometricIdentifier, new Item.Properties(), () -> ModularForcefieldsCreativeTabs.MAIN.get()));
		ITEMS.register("coercionderiver", () -> new BlockItemDescriptable(() -> blockCoercionDeriver, new Item.Properties(), () -> ModularForcefieldsCreativeTabs.MAIN.get()));
		ITEMS.register("fortroncapacitor", () -> new BlockItemDescriptable(() -> blockFortronCapacitor, new Item.Properties(), () -> ModularForcefieldsCreativeTabs.MAIN.get()));
		ITEMS.register("fortronfieldprojector", () -> new BlockItemDescriptable(() -> blockFortronFieldProjector, new Item.Properties(), () -> ModularForcefieldsCreativeTabs.MAIN.get()));
		ITEMS.register("interdictionmatrix", () -> new BlockItemDescriptable(() -> blockInterdictionMatrix, new Item.Properties(), () -> ModularForcefieldsCreativeTabs.MAIN.get()));
		ITEMS.register("fortronfield", () -> new BlockItemDescriptable(() -> blockFortronField, new Item.Properties(), () -> ModularForcefieldsCreativeTabs.MAIN.get()));
		registerSubtypeItem(SubtypeModule.values());
	}
	public static final RegistryObject<Item> ITEM_FOCUSMATRIX = ITEMS.register("focusmatrix", () -> new ItemElectrodynamics(new Item.Properties(), () -> ModularForcefieldsCreativeTabs.MAIN.get()));
	public static final RegistryObject<Item> ITEM_IDENTIFICATIONCARD = ITEMS.register("identificationcard", () -> new ItemIdentificationCard(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ITEM_FREQUENCYCARD = ITEMS.register("frequencycard", () -> new ItemFortronFrequencyCard(new Item.Properties().stacksTo(1)));

	private static void registerSubtypeItem(ISubtype[] array) {
		for (ISubtype subtype : array) {
			RegistryObject<Item> object = ITEMS.register(subtype.tag(), () -> new ItemElectrodynamics(new Item.Properties(), () -> ModularForcefieldsCreativeTabs.MAIN.get()));
			SUBTYPEITEMREGISTER_MAPPINGS.put(subtype, object);
		}
	}

	public static void initItemMapping() {
		for (Entry<ISubtype, RegistryObject<Item>> en : SUBTYPEITEMREGISTER_MAPPINGS.entrySet()) {
			ITEMSUBTYPE_MAPPINGS.put(en.getValue().get(), en.getKey());
		}
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ElectroCreativeRegistry {

		@SubscribeEvent
		public static void registerItems(BuildCreativeModeTabContentsEvent event) {

			if (event.getTab() == ModularForcefieldsCreativeTabs.MAIN.get()) {
				ITEMS.getEntries().forEach(reg -> {
					event.accept(reg);
				});
			}

		}

	}
}
