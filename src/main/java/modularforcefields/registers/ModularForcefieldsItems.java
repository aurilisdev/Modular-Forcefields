package modularforcefields.registers;

import static electrodynamics.registers.UnifiedElectrodynamicsRegister.supplier;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockBiometricIdentifier;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockCoercionDeriver;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockFortronCapacitor;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockFortronField;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockFortronFieldProjector;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockInterdictionMatrix;

import java.util.HashMap;
import java.util.Map.Entry;

import electrodynamics.api.ISubtype;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import modularforcefields.References;
import modularforcefields.common.item.ItemFortronFrequencyCard;
import modularforcefields.common.item.ItemIdentificationCard;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModularForcefieldsItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, References.ID);
	public static final HashMap<ISubtype, RegistryObject<Item>> SUBTYPEITEMREGISTER_MAPPINGS = new HashMap<>();
	public static final HashMap<Item, ISubtype> ITEMSUBTYPE_MAPPINGS = new HashMap<>();

	static {
		ITEMS.register("biometricidentifier", supplier(() -> new BlockItemDescriptable(() -> blockBiometricIdentifier, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("coercionderiver", supplier(() -> new BlockItemDescriptable(() -> blockCoercionDeriver, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("fortroncapacitor", supplier(() -> new BlockItemDescriptable(() -> blockFortronCapacitor, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("fortronfieldprojector", supplier(() -> new BlockItemDescriptable(() -> blockFortronFieldProjector, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("interdictionmatrix", supplier(() -> new BlockItemDescriptable(() -> blockInterdictionMatrix, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("fortronfield", supplier(() -> new BlockItemDescriptable(() -> blockFortronField, new Item.Properties().tab(References.MODULARTAB))));
		registerSubtypeItem(SubtypeModule.values());
	}
	public static final RegistryObject<Item> ITEM_FOCUSMATRIX = ITEMS.register("focusmatrix", supplier(() -> new Item(new Item.Properties().tab(References.MODULARTAB))));
	public static final RegistryObject<Item> ITEM_IDENTIFICATIONCARD = ITEMS.register("identificationcard", supplier(() -> new ItemIdentificationCard(new Item.Properties().tab(References.MODULARTAB).stacksTo(1))));
	public static final RegistryObject<Item> ITEM_FREQUENCYCARD = ITEMS.register("frequencycard", supplier(() -> new ItemFortronFrequencyCard(new Item.Properties().tab(References.MODULARTAB).stacksTo(1))));

	private static void registerSubtypeItem(ISubtype[] array) {
		for (ISubtype subtype : array) {
			RegistryObject<Item> object = ITEMS.register(subtype.tag(), supplier(() -> new Item(new Item.Properties().tab(References.MODULARTAB)), subtype));
			SUBTYPEITEMREGISTER_MAPPINGS.put(subtype, object);
		}
	}

	public static void initItemMapping() {
		for (Entry<ISubtype, RegistryObject<Item>> en : SUBTYPEITEMREGISTER_MAPPINGS.entrySet()) {
			ITEMSUBTYPE_MAPPINGS.put(en.getValue().get(), en.getKey());
		}
	}

}
