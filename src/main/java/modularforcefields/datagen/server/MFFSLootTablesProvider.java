package modularforcefields.datagen.server;

import modularforcefields.ModularForcefields;
import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.registers.ModularForcefieldsBlocks;
import modularforcefields.registers.ModularForcefieldsTiles;
import net.minecraft.data.DataGenerator;
import voltaic.datagen.utils.server.loottable.BaseLootTablesProvider;

public class MFFSLootTablesProvider extends BaseLootTablesProvider {

	public MFFSLootTablesProvider(DataGenerator gen) {
		super(gen, ModularForcefields.ID);
	}

	@Override
	protected void addTables() {

		addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.biometricidentifier), ModularForcefieldsTiles.TILE_BIOMETRICIDENTIFIER, true, false, false, true, true);
		addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.coercionderiver), ModularForcefieldsTiles.TILE_COERCIONDERIVER, true, false, false, true, true);
		addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortroncapacitor), ModularForcefieldsTiles.TILE_FORTRONCAPACITOR, true, false, false, true, true);
		addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortronfieldprojector), ModularForcefieldsTiles.TILE_FORTRONFIELDPROJECTOR, true, false, false, true, true);
		addMachineTable(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.interdictionmatrix), ModularForcefieldsTiles.TILE_INTERDICTIONMATRIX, true, false, false, true, true);

	}

}
