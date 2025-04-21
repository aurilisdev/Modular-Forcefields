package modularforcefields.registers;

import com.google.common.collect.Sets;

import modularforcefields.ModularForcefields;
import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.common.tile.TileBiometricIdentifier;
import modularforcefields.common.tile.TileCoercionDeriver;
import modularforcefields.common.tile.TileFortronCapacitor;
import modularforcefields.common.tile.TileFortronField;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.common.tile.TileInterdictionMatrix;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModularForcefieldsTiles {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModularForcefields.ID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileBiometricIdentifier>> TILE_BIOMETRICIDENTIFIER = BLOCK_ENTITY_TYPES.register("biometricidentifier", () -> new BlockEntityType<>(TileBiometricIdentifier::new, Sets.newHashSet(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.biometricidentifier)), null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileCoercionDeriver>> TILE_COERCIONDERIVER = BLOCK_ENTITY_TYPES.register("coercionderiver", () -> new BlockEntityType<>(TileCoercionDeriver::new, Sets.newHashSet(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.coercionderiver)), null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileFortronCapacitor>> TILE_FORTRONCAPACITOR = BLOCK_ENTITY_TYPES.register("fortroncapacitor", () -> new BlockEntityType<>(TileFortronCapacitor::new, Sets.newHashSet(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortroncapacitor)), null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileFortronFieldProjector>> TILE_FORTRONFIELDPROJECTOR = BLOCK_ENTITY_TYPES.register("fortronfieldprojector", () -> new BlockEntityType<>(TileFortronFieldProjector::new, Sets.newHashSet(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortronfieldprojector)), null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileInterdictionMatrix>> TILE_INTERDICTIONMATRIX = BLOCK_ENTITY_TYPES.register("interdictionmatrix", () -> new BlockEntityType<>(TileInterdictionMatrix::new, Sets.newHashSet(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.interdictionmatrix)), null));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileFortronField>> TILE_FORTRONFIELD = BLOCK_ENTITY_TYPES.register("fortronfield", () -> new BlockEntityType<>(TileFortronField::new, Sets.newHashSet(ModularForcefieldsBlocks.BLOCK_FORTRONFIELD.get()), null));

}
