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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import voltaic.datagen.utils.client.BaseLangKeyProvider;

@EventBusSubscriber(modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {

		DataGenerator generator = event.getGenerator();

		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {

			generator.addProvider(new MFFSLootTablesProvider(generator));
			generator.addProvider(new MFFSRecipeProvider(generator));
			MFFSBlockTagsProvider blocktags = new MFFSBlockTagsProvider(generator, helper);
			generator.addProvider(blocktags);
			generator.addProvider(new MFFSItemTagsProvider(generator, blocktags, helper));

		}
		if (event.includeClient()) {
			generator.addProvider(new MFFSBlockStateProvider(generator, helper));
			generator.addProvider(new MFFSItemModelsProvider(generator, helper));
			generator.addProvider(new MFFSLangKeyProvider(generator, BaseLangKeyProvider.Locale.EN_US));
			generator.addProvider(new MFFSSoundProvider(generator, helper));
		}
	}

}
