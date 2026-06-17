package modularforcefields.datagen.server.recipe;

import java.util.concurrent.CompletableFuture;

import modularforcefields.datagen.server.recipe.vanilla.MFFSCraftingTableRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import voltaic.datagen.utils.server.recipe.BaseRecipeProvider;

public class MFFSRecipeProvider extends BaseRecipeProvider {

    public MFFSRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
	super(output);
    }

    @Override
    public void addRecipes() {
	generators.add(new MFFSCraftingTableRecipes());
    }

}
