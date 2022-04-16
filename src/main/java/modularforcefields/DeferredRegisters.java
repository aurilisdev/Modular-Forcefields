package modularforcefields;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

import electrodynamics.api.ISubtype;
import electrodynamics.common.blockitem.BlockItemDescriptable;
import electrodynamics.prefab.block.GenericMachineBlock;
import modularforcefields.common.block.BlockFortronField;
import modularforcefields.common.fluid.types.FluidFortron;
import modularforcefields.common.inventory.container.ContainerCoercionDeriver;
import modularforcefields.common.inventory.container.ContainerFortronCapacitor;
import modularforcefields.common.inventory.container.ContainerFortronFieldProjector;
import modularforcefields.common.inventory.container.ContainerInterdictionMatrix;
import modularforcefields.common.item.subtype.SubtypeModule;
import modularforcefields.common.tile.TileBiometricIdentifier;
import modularforcefields.common.tile.TileCoercionDeriver;
import modularforcefields.common.tile.TileFortronCapacitor;
import modularforcefields.common.tile.TileFortronField;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.common.tile.TileInterdictionMatrix;
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
	public static final HashMap<Item, ISubtype> ITEMSUBTYPE_MAPPINGS = new HashMap<>();
	public static final HashMap<ISubtype, RegistryObject<Block>> SUBTYPEBLOCKREGISTER_MAPPINGS = new HashMap<>();
	public static GenericMachineBlock blockBiometricIdentifier;
	public static GenericMachineBlock blockCoercionDeriver;
	public static GenericMachineBlock blockFortronCapacitor;
	public static GenericMachineBlock blockFortronFieldProjector;
	public static GenericMachineBlock blockInterdictionMatrix;
	public static BlockFortronField blockFortronField;
	public static FluidFortron fluidFortron;

	static {
		BLOCKS.register("biometricidentifier", supplier(() -> blockBiometricIdentifier = new GenericMachineBlock(TileBiometricIdentifier::new)));
		BLOCKS.register("coercionderiver", supplier(() -> blockCoercionDeriver = new GenericMachineBlock(TileCoercionDeriver::new)));
		BLOCKS.register("fortroncapacitor", supplier(() -> blockFortronCapacitor = new GenericMachineBlock(TileFortronCapacitor::new)));
		BLOCKS.register("fortronfieldprojector", supplier(() -> blockFortronFieldProjector = new GenericMachineBlock(TileFortronFieldProjector::new)));
		BLOCKS.register("interdictionmatrix", supplier(() -> blockInterdictionMatrix = new GenericMachineBlock(TileInterdictionMatrix::new)));
		BLOCKS.register("fortronfield", supplier(() -> blockFortronField = new BlockFortronField()));
		ITEMS.register("biometricidentifier", supplier(() -> new BlockItemDescriptable(() -> blockBiometricIdentifier, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("coercionderiver", supplier(() -> new BlockItemDescriptable(() -> blockCoercionDeriver, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("fortroncapacitor", supplier(() -> new BlockItemDescriptable(() -> blockFortronCapacitor, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("fortronfieldprojector", supplier(() -> new BlockItemDescriptable(() -> blockFortronFieldProjector, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("interdictionmatrix", supplier(() -> new BlockItemDescriptable(() -> blockInterdictionMatrix, new Item.Properties().tab(References.MODULARTAB))));
		ITEMS.register("fortronfield", supplier(() -> new BlockItemDescriptable(() -> blockFortronField, new Item.Properties().tab(References.MODULARTAB))));
		registerSubtypeItem(SubtypeModule.values());
		FLUIDS.register("fluidfortron", supplier(() -> fluidFortron = new FluidFortron()));
	}
	public static final RegistryObject<Item> ITEM_FOCUSMATRIX = ITEMS.register("focusmatrix", supplier(() -> new Item(new Item.Properties().tab(References.MODULARTAB))));

	public static final RegistryObject<BlockEntityType<TileBiometricIdentifier>> TILE_BIOMETRICIDENTIFIER = TILES.register("biometricidentifier", () -> new BlockEntityType<>(TileBiometricIdentifier::new, Sets.newHashSet(blockBiometricIdentifier), null));
	public static final RegistryObject<BlockEntityType<TileCoercionDeriver>> TILE_COERCIONDERIVER = TILES.register("coercionderiver", () -> new BlockEntityType<>(TileCoercionDeriver::new, Sets.newHashSet(blockCoercionDeriver), null));
	public static final RegistryObject<BlockEntityType<TileFortronCapacitor>> TILE_FORTRONCAPACITOR = TILES.register("fortroncapacitor", () -> new BlockEntityType<>(TileFortronCapacitor::new, Sets.newHashSet(blockFortronCapacitor), null));
	public static final RegistryObject<BlockEntityType<TileFortronFieldProjector>> TILE_FORTRONFIELDPROJECTOR = TILES.register("fortronfieldprojector", () -> new BlockEntityType<>(TileFortronFieldProjector::new, Sets.newHashSet(blockFortronFieldProjector), null));
	public static final RegistryObject<BlockEntityType<TileInterdictionMatrix>> TILE_INTERDICTIONMATRIX = TILES.register("interdictionmatrix", () -> new BlockEntityType<>(TileInterdictionMatrix::new, Sets.newHashSet(blockInterdictionMatrix), null));
	public static final RegistryObject<BlockEntityType<TileFortronField>> TILE_FORTRONFIELD = TILES.register("fortronfield", () -> new BlockEntityType<>(TileFortronField::new, Sets.newHashSet(blockFortronField), null));

	public static final RegistryObject<MenuType<ContainerCoercionDeriver>> CONTAINER_COERCIONDERIVER = CONTAINERS.register("coercionderiver", () -> new MenuType<>(ContainerCoercionDeriver::new));
	public static final RegistryObject<MenuType<ContainerFortronCapacitor>> CONTAINER_FORTRONCAPACITOR = CONTAINERS.register("fortroncapacitor", () -> new MenuType<>(ContainerFortronCapacitor::new));
	public static final RegistryObject<MenuType<ContainerFortronFieldProjector>> CONTAINER_FORTRONFIELDPROJECTOR = CONTAINERS.register("fortronfieldprojector", () -> new MenuType<>(ContainerFortronFieldProjector::new));
	public static final RegistryObject<MenuType<ContainerInterdictionMatrix>> CONTAINER_INTERDICTIONMATRIX = CONTAINERS.register("interdictionmatrix", () -> new MenuType<>(ContainerInterdictionMatrix::new));

	private static void registerSubtypeItem(ISubtype[] array) {
		for (ISubtype subtype : array) {
			RegistryObject<Item> object = ITEMS.register(subtype.tag(), supplier(() -> new Item(new Item.Properties().tab(References.MODULARTAB)), subtype));
			SUBTYPEITEMREGISTER_MAPPINGS.put(subtype, object);
		}
	}

	private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(Supplier<T> entry) {
		return entry;
	}

	private static <T extends IForgeRegistryEntry<T>> Supplier<? extends T> supplier(Supplier<T> entry, ISubtype en) {
		return entry;
	}

	public static void initItemMapping() {
		for (Entry<ISubtype, RegistryObject<Item>> en : SUBTYPEITEMREGISTER_MAPPINGS.entrySet()) {
			ITEMSUBTYPE_MAPPINGS.put(en.getValue().get(), en.getKey());
		}
	}
}
