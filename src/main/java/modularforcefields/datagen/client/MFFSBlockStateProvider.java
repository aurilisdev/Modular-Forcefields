package modularforcefields.datagen.client;

import electrodynamics.datagen.client.ElectrodynamicsBlockStateProvider;
import modularforcefields.References;
import modularforcefields.common.block.SubtypeMFFSMachine;
import modularforcefields.registers.ModularForcefieldsBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MFFSBlockStateProvider extends ElectrodynamicsBlockStateProvider {

    private static final ResourceLocation STEEL_CASING = ResourceLocation.fromNamespaceAndPath(electrodynamics.api.References.ID, "block/steelcasing");

    public MFFSBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, exFileHelper, References.ID);
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
