package modularforcefields;

import java.util.HashMap;

import com.google.common.base.Supplier;

import electrodynamics.api.ISubtype;
import electrodynamics.api.References;
import modularforcefields.common.item.subtype.SubtypeModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

public class DeferredRegisters {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, References.ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, References.ID);
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, References.ID);
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, References.ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, References.ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, References.ID);
	public static final HashMap<ISubtype, RegistryObject<Item>> SUBTYPEITEMREGISTER_MAPPINGS = new HashMap<>();
	public static final HashMap<ISubtype, RegistryObject<Block>> SUBTYPEBLOCKREGISTER_MAPPINGS = new HashMap<>();
	public static final HashMap<ISubtype, Item> SUBTYPEITEM_MAPPINGS = new HashMap<>();
	public static final HashMap<Item, ISubtype> ITEMSUBTYPE_MAPPINGS = new HashMap<>();
	public static final HashMap<ISubtype, Block> SUBTYPEBLOCK_MAPPINGS = new HashMap<>();

	static {
		registerSubtypeItem(SubtypeModule.values());
	}

	private static void registerSubtypeItem(ISubtype[] array) {
		for (ISubtype subtype : array) {
			ITEMS.register(subtype.tag(), supplier(new Item(new Item.Properties().tab(References.CORETAB)), subtype));
		}
	}

	private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(T entry) {
		return () -> entry;
	}

	private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(T entry, ISubtype en) {
		if (entry instanceof Block block) {
			SUBTYPEBLOCK_MAPPINGS.put(en, block);
		} else if (entry instanceof Item item) {
			SUBTYPEITEM_MAPPINGS.put(en, item);
			ITEMSUBTYPE_MAPPINGS.put(item, en);
		}
		return supplier(entry);
	}
}
