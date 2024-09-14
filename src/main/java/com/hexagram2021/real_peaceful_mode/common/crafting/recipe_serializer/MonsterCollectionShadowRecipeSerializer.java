package com.hexagram2021.real_peaceful_mode.common.crafting.recipe_serializer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.MonsterCollectionShadowRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

@SuppressWarnings("deprecation")
public class MonsterCollectionShadowRecipeSerializer implements RecipeSerializer<MonsterCollectionShadowRecipe> {
	@Override
	public MonsterCollectionShadowRecipe fromJson(ResourceLocation id, JsonObject json) {
		ResourceLocation entityTypeId = new ResourceLocation(GsonHelper.getAsString(json, "entity_type"));
		EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityTypeId);
		if(entityType == null) {
			throw new IllegalArgumentException("No entity type found for \"" + entityTypeId + "\" from recipe " + id);
		}
		ImmutableList.Builder<Item> builder = ImmutableList.builder();
		JsonArray array = GsonHelper.getAsJsonArray(json, "results");
		for(JsonElement element: array) {
			builder.add(GsonHelper.convertToItem(element, "result"));
		}
		return new MonsterCollectionShadowRecipe(id, entityType, builder.build());
	}

	@Override
	public MonsterCollectionShadowRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		ResourceLocation entityTypeId = new ResourceLocation(buf.readUtf());
		EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityTypeId);
		if(entityType == null) {
			throw new IllegalArgumentException("No entity type found for \"" + entityTypeId + "\" from recipe " + id);
		}
		List<Item> results = buf.readCollection(Lists::newArrayListWithCapacity, buf1 -> buf1.readById(BuiltInRegistries.ITEM));
		return new MonsterCollectionShadowRecipe(id, entityType, ImmutableList.copyOf(results));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, MonsterCollectionShadowRecipe recipe) {
		buf.writeUtf(getRegistryName(recipe.entityType()).toString());
		buf.writeCollection(recipe.results(), (buf1, item) -> buf1.writeId(BuiltInRegistries.ITEM, item));
	}
}
