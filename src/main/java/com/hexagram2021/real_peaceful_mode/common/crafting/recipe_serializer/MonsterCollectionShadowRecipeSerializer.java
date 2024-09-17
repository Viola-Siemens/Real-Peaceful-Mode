package com.hexagram2021.real_peaceful_mode.common.crafting.recipe_serializer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.MonsterCollectionShadowRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public class MonsterCollectionShadowRecipeSerializer implements RecipeSerializer<MonsterCollectionShadowRecipe> {
	private final MapCodec<MonsterCollectionShadowRecipe> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity_type").forGetter(MonsterCollectionShadowRecipe::entityType),
			BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("results").forGetter(MonsterCollectionShadowRecipe::results)
	).apply(instance, MonsterCollectionShadowRecipe::new));
	private final StreamCodec<RegistryFriendlyByteBuf, MonsterCollectionShadowRecipe> streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);

	private static final StreamCodec<RegistryFriendlyByteBuf, Item> ITEM_STREAM_CODEC = ByteBufCodecs.registry(Registries.ITEM);

	private MonsterCollectionShadowRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
		ResourceLocation entityTypeId = ResourceLocation.parse(buf.readUtf());
		EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityTypeId);
		List<Item> results = buf.readCollection(Lists::newArrayListWithCapacity, buf1 -> ITEM_STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf1));
		return new MonsterCollectionShadowRecipe(entityType, ImmutableList.copyOf(results));
	}

	private void toNetwork(RegistryFriendlyByteBuf buf, MonsterCollectionShadowRecipe recipe) {
		buf.writeUtf(getRegistryName(recipe.entityType()).toString());
		buf.writeCollection(recipe.results(), (buf1, item) -> ITEM_STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf1, item));
	}

	@Override
	public MapCodec<MonsterCollectionShadowRecipe> codec() {
		return this.codec;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, MonsterCollectionShadowRecipe> streamCodec() {
		return this.streamCodec;
	}
}
