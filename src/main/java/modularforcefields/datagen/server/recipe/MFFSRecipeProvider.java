package modularforcefields.datagen.server.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import modularforcefields.datagen.server.recipe.vanilla.MFFSCraftingTableRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import voltaic.datagen.utils.server.recipe.AbstractRecipeGenerator;

public class MFFSRecipeProvider extends RecipeProvider {

	public final List<AbstractRecipeGenerator> generators = new ArrayList<>();

	public MFFSRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
		addRecipes();
	}

	public void addRecipes() {
		generators.add(new MFFSCraftingTableRecipes());
	}

	@Override
	protected void buildRecipes(RecipeOutput output) {
		for (AbstractRecipeGenerator generator : generators) {
			generator.addRecipes(output);
		}
	}

}
