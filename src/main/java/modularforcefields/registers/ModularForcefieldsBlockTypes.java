package modularforcefields.registers;

import static modularforcefields.registers.ModularForcefieldsBlocks.blockBiometricIdentifier;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockCoercionDeriver;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockFortronCapacitor;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockFortronField;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockFortronFieldProjector;
import static modularforcefields.registers.ModularForcefieldsBlocks.blockInterdictionMatrix;

import com.google.common.collect.Sets;

import modularforcefields.References;
import modularforcefields.common.tile.TileBiometricIdentifier;
import modularforcefields.common.tile.TileCoercionDeriver;
import modularforcefields.common.tile.TileFortronCapacitor;
import modularforcefields.common.tile.TileFortronField;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.common.tile.TileInterdictionMatrix;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModularForcefieldsBlockTypes {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, References.ID);

	public static final RegistryObject<BlockEntityType<TileBiometricIdentifier>> TILE_BIOMETRICIDENTIFIER = BLOCK_ENTITY_TYPES.register("biometricidentifier", () -> new BlockEntityType<>(TileBiometricIdentifier::new, Sets.newHashSet(blockBiometricIdentifier), null));
	public static final RegistryObject<BlockEntityType<TileCoercionDeriver>> TILE_COERCIONDERIVER = BLOCK_ENTITY_TYPES.register("coercionderiver", () -> new BlockEntityType<>(TileCoercionDeriver::new, Sets.newHashSet(blockCoercionDeriver), null));
	public static final RegistryObject<BlockEntityType<TileFortronCapacitor>> TILE_FORTRONCAPACITOR = BLOCK_ENTITY_TYPES.register("fortroncapacitor", () -> new BlockEntityType<>(TileFortronCapacitor::new, Sets.newHashSet(blockFortronCapacitor), null));
	public static final RegistryObject<BlockEntityType<TileFortronFieldProjector>> TILE_FORTRONFIELDPROJECTOR = BLOCK_ENTITY_TYPES.register("fortronfieldprojector", () -> new BlockEntityType<>(TileFortronFieldProjector::new, Sets.newHashSet(blockFortronFieldProjector), null));
	public static final RegistryObject<BlockEntityType<TileInterdictionMatrix>> TILE_INTERDICTIONMATRIX = BLOCK_ENTITY_TYPES.register("interdictionmatrix", () -> new BlockEntityType<>(TileInterdictionMatrix::new, Sets.newHashSet(blockInterdictionMatrix), null));
	public static final RegistryObject<BlockEntityType<TileFortronField>> TILE_FORTRONFIELD = BLOCK_ENTITY_TYPES.register("fortronfield", () -> new BlockEntityType<>(TileFortronField::new, Sets.newHashSet(blockFortronField), null));

}
