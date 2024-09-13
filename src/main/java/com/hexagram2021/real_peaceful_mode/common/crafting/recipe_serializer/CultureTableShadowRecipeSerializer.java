package com.hexagram2021.real_peaceful_mode.common.crafting.recipe_serializer;

import com.google.gson.JsonObject;
import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.CultureTableShadowRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CultureTableShadowRecipeSerializer implements RecipeSerializer<CultureTableShadowRecipe> {
	@Override
	public CultureTableShadowRecipe fromJson(ResourceLocation id, JsonObject json) {
		Ingredient mix1 = Ingredient.fromJson(GsonHelper.getNonNull(json, "mix1"));
		Ingredient mix2 = Ingredient.fromJson(GsonHelper.getNonNull(json, "mix2"));
		Ingredient result = Ingredient.fromJson(GsonHelper.getNonNull(json, "result"));
		return new CultureTableShadowRecipe(id, mix1, mix2, result);
	}

	@Override
	public CultureTableShadowRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		Ingredient mix1 = Ingredient.fromNetwork(buf);
		Ingredient mix2 = Ingredient.fromNetwork(buf);
		Ingredient result = Ingredient.fromNetwork(buf);
		return new CultureTableShadowRecipe(id, mix1, mix2, result);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, CultureTableShadowRecipe recipe) {
		recipe.mix1().toNetwork(buf);
		recipe.mix2().toNetwork(buf);
		recipe.result().toNetwork(buf);
	}
}
