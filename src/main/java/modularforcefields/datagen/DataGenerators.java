package modularforcefields.datagen;

import modularforcefields.ModularForcefields;
import modularforcefields.datagen.client.MFFSBlockStateProvider;
import modularforcefields.datagen.client.MFFSItemModelsProvider;
import modularforcefields.datagen.client.MFFSLangKeyProvider;
import modularforcefields.datagen.client.MFFSSoundProvider;
import modularforcefields.datagen.server.MFFSBlockTagsProvider;
import modularforcefields.datagen.server.MFFSItemTagsProvider;
import modularforcefields.datagen.server.MFFSLootTablesProvider;
import modularforcefields.datagen.server.recipe.MFFSRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.datagen.utils.client.BaseLangKeyProvider;

@EventBusSubscriber(modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {

		DataGenerator generator = event.getGenerator();

		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {

			generator.addProvider(true, new MFFSLootTablesProvider(generator));
			generator.addProvider(true, new MFFSRecipeProvider(generator));
			MFFSBlockTagsProvider blocktags = new MFFSBlockTagsProvider(generator, helper);
			generator.addProvider(true, blocktags);
			generator.addProvider(true, new MFFSItemTagsProvider(generator, blocktags, helper));

		}
		if (event.includeClient()) {
			generator.addProvider(true, new MFFSBlockStateProvider(generator, helper));
			generator.addProvider(true, new MFFSItemModelsProvider(generator, helper));
			generator.addProvider(true, new MFFSLangKeyProvider(generator, BaseLangKeyProvider.Locale.EN_US));
			generator.addProvider(true, new MFFSSoundProvider(generator, helper));
		}
	}

}
