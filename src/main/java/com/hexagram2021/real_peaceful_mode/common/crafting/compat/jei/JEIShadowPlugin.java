package com.hexagram2021.real_peaceful_mode.common.crafting.compat.jei;

import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.CultureTableShadowRecipe;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@JeiPlugin
public class JEIShadowPlugin implements IModPlugin {
	public interface RPMJEIRecipeTypes {
		RecipeType<CultureTableShadowRecipe> CULTURE_TABLE = new RecipeType<>(CultureTableShadowRecipeCategory.UID, CultureTableShadowRecipe.class);
	}

	private static final ResourceLocation UID = new ResourceLocation(MODID, "shadow");

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		//Recipes
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(
				new CultureTableShadowRecipeCategory(guiHelper)
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RPMLogger.info("Adding RPM recipes to JEI!!");
		registration.addRecipes(RPMJEIRecipeTypes.CULTURE_TABLE, CultureTableShadowRecipe.getCultureTableRecipes());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(RPMBlocks.WorkStation.CULTURE_TABLE), RPMJEIRecipeTypes.CULTURE_TABLE);
	}
}
