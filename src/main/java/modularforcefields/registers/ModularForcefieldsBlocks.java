package modularforcefields.registers;

import java.util.HashMap;

import electrodynamics.api.ISubtype;
import electrodynamics.prefab.block.GenericMachineBlock;
import modularforcefields.References;
import modularforcefields.common.block.BlockFortronField;
import modularforcefields.common.tile.TileBiometricIdentifier;
import modularforcefields.common.tile.TileCoercionDeriver;
import modularforcefields.common.tile.TileFortronCapacitor;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.common.tile.TileInterdictionMatrix;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModularForcefieldsBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, References.ID);
	public static final HashMap<ISubtype, RegistryObject<Block>> SUBTYPEBLOCKREGISTER_MAPPINGS = new HashMap<>();
	public static GenericMachineBlock blockBiometricIdentifier;
	public static GenericMachineBlock blockCoercionDeriver;
	public static GenericMachineBlock blockFortronCapacitor;
	public static GenericMachineBlock blockFortronFieldProjector;
	public static GenericMachineBlock blockInterdictionMatrix;
	public static BlockFortronField blockFortronField;

	static {
		BLOCKS.register("biometricidentifier", () -> blockBiometricIdentifier = new GenericMachineBlock(TileBiometricIdentifier::new));
		BLOCKS.register("coercionderiver", () -> blockCoercionDeriver = new GenericMachineBlock(TileCoercionDeriver::new));
		BLOCKS.register("fortroncapacitor", () -> blockFortronCapacitor = new GenericMachineBlock(TileFortronCapacitor::new));
		BLOCKS.register("fortronfieldprojector", () -> blockFortronFieldProjector = new GenericMachineBlock(TileFortronFieldProjector::new));
		BLOCKS.register("interdictionmatrix", () -> blockInterdictionMatrix = new GenericMachineBlock(TileInterdictionMatrix::new));
		BLOCKS.register("fortronfield", () -> blockFortronField = new BlockFortronField());
	}
}
