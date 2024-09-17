package com.hexagram2021.real_peaceful_mode.common.crafting.recipe_serializer;

import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.CultureTableShadowRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CultureTableShadowRecipeSerializer implements RecipeSerializer<CultureTableShadowRecipe> {
	private final MapCodec<CultureTableShadowRecipe> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC.fieldOf("mix1").forGetter(CultureTableShadowRecipe::mix1),
			Ingredient.CODEC.fieldOf("mix2").forGetter(CultureTableShadowRecipe::mix2),
			Ingredient.CODEC.fieldOf("result").forGetter(CultureTableShadowRecipe::result)
	).apply(instance, CultureTableShadowRecipe::new));
	private final StreamCodec<RegistryFriendlyByteBuf, CultureTableShadowRecipe> streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);

	private CultureTableShadowRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
		Ingredient mix1 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
		Ingredient mix2 = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
		Ingredient result = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
		return new CultureTableShadowRecipe(mix1, mix2, result);
	}

	private void toNetwork(RegistryFriendlyByteBuf buf, CultureTableShadowRecipe recipe) {
		Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.mix1());
		Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.mix2());
		Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.result());
	}

	@Override
	public MapCodec<CultureTableShadowRecipe> codec() {
		return this.codec;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, CultureTableShadowRecipe> streamCodec() {
		return this.streamCodec;
	}
}
