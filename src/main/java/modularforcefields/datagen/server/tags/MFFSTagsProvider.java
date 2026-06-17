package modularforcefields.datagen.server.tags;

import java.util.concurrent.CompletableFuture;

import modularforcefields.datagen.server.tags.types.MFFSBlockTagsProvider;
import modularforcefields.datagen.server.tags.types.MFFSItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MFFSTagsProvider {

    public static void addTagProviders(DataGenerator generator, PackOutput output,
	    CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper) {
	MFFSBlockTagsProvider blockProvider = new MFFSBlockTagsProvider(output, lookupProvider, helper);
	generator.addProvider(true, blockProvider);
	generator.addProvider(true, new MFFSItemTagsProvider(output, lookupProvider, blockProvider, helper));
    }

}
