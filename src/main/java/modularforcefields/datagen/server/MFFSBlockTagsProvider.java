package modularforcefields.datagen.server;

import modularforcefields.ModularForcefields;
import modularforcefields.registers.ModularForcefieldsBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.common.block.BlockMachine;

public class MFFSBlockTagsProvider extends BlockTagsProvider {

    public MFFSBlockTagsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ModularForcefields.ID, existingFileHelper);
    }

    @Override
    protected void addTags() {

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getAllValuesArray(new BlockMachine[0]));

        tag(BlockTags.NEEDS_STONE_TOOL).add(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getAllValuesArray(new BlockMachine[0]));

    }

}
