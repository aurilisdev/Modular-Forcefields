package modularforcefields.datagen.server;

import java.util.List;

import modularforcefields.ModularForcefields;
import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.registers.ModularForcefieldsBlocks;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import voltaic.datagen.utils.server.loottable.BaseLootTablesProvider;

public class MFFSLootTablesProvider extends BaseLootTablesProvider {

    public MFFSLootTablesProvider(HolderLookup.Provider provider) {
	super(ModularForcefields.ID, provider);
    }

    @Override
    protected void generate() {

	addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.biometricidentifier),
		ModularForcefieldsTiles.TILE_BIOMETRICIDENTIFIER, true, false, false, true, true);
	addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.coercionderiver),
		ModularForcefieldsTiles.TILE_COERCIONDERIVER, true, false, false, true, true);
	addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortroncapacitor),
		ModularForcefieldsTiles.TILE_FORTRONCAPACITOR, true, false, false, true, true);
	addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortronfieldprojector),
		ModularForcefieldsTiles.TILE_FORTRONFIELDPROJECTOR, true, false, false, true, true);
	addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.interdictionmatrix),
		ModularForcefieldsTiles.TILE_INTERDICTIONMATRIX, true, false, false, true, true);

    }

    @Override
    public List<Block> getExcludedBlocks() {
	return List.of(ModularForcefieldsBlocks.BLOCK_FORTRONFIELD.get());
    }

}
