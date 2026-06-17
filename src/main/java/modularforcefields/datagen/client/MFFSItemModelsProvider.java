package modularforcefields.datagen.client;

import modularforcefields.ModularForcefields;
import modularforcefields.registers.ModularForcefieldsItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import voltaic.datagen.utils.client.BaseItemModelsProvider;

public class MFFSItemModelsProvider extends BaseItemModelsProvider {

    public MFFSItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
	super(output, existingFileHelper, ModularForcefields.ID);
    }

    @Override
    protected void registerModels() {

	layeredItem(ModularForcefieldsItems.ITEM_IDENTIFICATIONCARD, Parent.GENERATED, itemLoc("identificationcard"));
	layeredItem(ModularForcefieldsItems.ITEM_FREQUENCYCARD, Parent.GENERATED, itemLoc("frequencycard"));
	layeredItem(ModularForcefieldsItems.ITEM_FOCUSMATRIX, Parent.GENERATED, itemLoc("focusmatrix"));

	for (Item item : ModularForcefieldsItems.ITEMS_MODULE.getAllValues()) {
	    layeredItem(item, Parent.GENERATED, itemLoc("module/" + name(item)));
	}

    }

}
