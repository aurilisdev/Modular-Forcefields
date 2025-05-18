package modularforcefields.datagen.server;

import modularforcefields.ModularForcefields;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MFFSItemTagsProvider extends ItemTagsProvider {

	public MFFSItemTagsProvider(DataGenerator generator, BlockTagsProvider provider, ExistingFileHelper existingFileHelper) {
        super(generator, provider, ModularForcefields.ID, existingFileHelper);
    }

    @Override
    protected void addTags() {

    }

}
