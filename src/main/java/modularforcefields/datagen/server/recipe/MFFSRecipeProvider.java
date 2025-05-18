package modularforcefields.datagen.server.recipe;

import modularforcefields.datagen.server.recipe.vanilla.MFFSCraftingTableRecipes;
import net.minecraft.data.DataGenerator;
import voltaic.datagen.utils.server.recipe.BaseRecipeProvider;

public class MFFSRecipeProvider extends BaseRecipeProvider {

	public MFFSRecipeProvider(DataGenerator gen) {
		super(gen);
	}

	public void addRecipes() {
		generators.add(new MFFSCraftingTableRecipes());
	}

}
