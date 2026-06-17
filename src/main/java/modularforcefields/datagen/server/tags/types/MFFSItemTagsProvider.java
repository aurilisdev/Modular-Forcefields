package modularforcefields.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import modularforcefields.ModularForcefields;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MFFSItemTagsProvider extends ItemTagsProvider {

    public MFFSItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
	    BlockTagsProvider provider, ExistingFileHelper existingFileHelper) {
	super(output, lookupProvider, provider.contentsGetter(), ModularForcefields.ID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {

    }

}
