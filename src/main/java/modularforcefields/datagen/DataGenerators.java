package modularforcefields.datagen;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import modularforcefields.ModularForcefields;
import modularforcefields.datagen.client.MFFSBlockStateProvider;
import modularforcefields.datagen.client.MFFSItemModelsProvider;
import modularforcefields.datagen.client.MFFSLangKeyProvider;
import modularforcefields.datagen.client.MFFSSoundProvider;
import modularforcefields.datagen.server.MFFSLootTablesProvider;
import modularforcefields.datagen.server.recipe.MFFSRecipeProvider;
import modularforcefields.datagen.server.tags.MFFSTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import voltaic.datagen.utils.client.BaseLangKeyProvider;

@EventBusSubscriber(modid = ModularForcefields.ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {

		DataGenerator generator = event.getGenerator();

		PackOutput output = generator.getPackOutput();

		ExistingFileHelper helper = event.getExistingFileHelper();

		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		if (event.includeServer()) {

			generator.addProvider(true, new LootTableProvider(output, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(MFFSLootTablesProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
			generator.addProvider(true, new MFFSRecipeProvider(output, lookupProvider));
			MFFSTagsProvider.addTagProviders(generator, output, lookupProvider, helper);

		}
		if (event.includeClient()) {
			generator.addProvider(true, new MFFSBlockStateProvider(output, helper));
			generator.addProvider(true, new MFFSItemModelsProvider(output, helper));
			generator.addProvider(true, new MFFSLangKeyProvider(output, BaseLangKeyProvider.Locale.EN_US));
			generator.addProvider(true, new MFFSSoundProvider(output, helper));
		}
	}

}
