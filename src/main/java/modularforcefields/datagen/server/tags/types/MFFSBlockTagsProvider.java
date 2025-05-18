package modularforcefields.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import modularforcefields.ModularForcefields;
import modularforcefields.registers.ModularForcefieldsBlocks;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import voltaic.common.block.BlockMachine;

public class MFFSBlockTagsProvider extends BlockTagsProvider {

    public MFFSBlockTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModularForcefields.ID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getAllValuesArray(new BlockMachine[0]));

        tag(BlockTags.NEEDS_STONE_TOOL).add(ModularForcefieldsBlocks.BLOCKS_MFFSMACHINE.getAllValuesArray(new BlockMachine[0]));

    }

}
