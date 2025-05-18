package modularforcefields.datagen.client;

import modularforcefields.ModularForcefields;
import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.registers.ModularForcefieldsBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.datagen.utils.client.BaseBlockstateProvider;

public class MFFSBlockStateProvider extends BaseBlockstateProvider {


    public MFFSBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, exFileHelper, ModularForcefields.ID);
    }

    @Override
    protected void registerStatesAndModels() {

        simpleBlock(ModularForcefieldsBlocks.BLOCK_FORTRONFIELD.get(), existingBlock(ModularForcefieldsBlocks.BLOCK_FORTRONFIELD), false);

        horrRotatedBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.biometricidentifier), existingBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.biometricidentifier)), true);
        horrRotatedLitBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortroncapacitor), existingBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortroncapacitor)), existingBlock(blockLoc("fortroncapacitoron")), true);
        horrRotatedLitBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.coercionderiver), existingBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.coercionderiver)), existingBlock(blockLoc("coercionderiveron")), 90, 0, true);
        horrRotatedLitBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortronfieldprojector), existingBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.fortronfieldprojector)), existingBlock(blockLoc("fortronfieldprojector")), true);
        horrRotatedLitBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.interdictionmatrix), existingBlock(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getValue(SubtypeMFFSMachine.interdictionmatrix)), existingBlock(blockLoc("interdictionmatrixon")), true);

    }



}
