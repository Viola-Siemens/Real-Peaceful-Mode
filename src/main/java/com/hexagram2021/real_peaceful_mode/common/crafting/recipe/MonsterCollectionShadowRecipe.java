package com.hexagram2021.real_peaceful_mode.common.crafting.recipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.AbstractChatMessage;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.MaterialCollectionEndChatMessage;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.hexagram2021.real_peaceful_mode.common.register.RPMRecipeSerializers;
import com.hexagram2021.real_peaceful_mode.common.register.RPMRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public record MonsterCollectionShadowRecipe(EntityType<?> entityType, List<Item> results) implements Recipe<CraftingInput> {
	@Nullable
	private static List<MonsterCollectionShadowRecipe> cachedList = null;

	private static final Map<EntityType<?>, Entity> LAZY_RENDER_ENTITIES = Maps.newHashMap();

	public static List<MonsterCollectionShadowRecipe> getMonsterCollectionRecipes() {
		if(cachedList != null) {
			return cachedList;
		}
		return List.of();
	}
	public static List<MonsterCollectionShadowRecipe> getMonsterCollectionRecipes(Function<ResourceKey<LootTable>, LootTable> lootTableGetter) {
		if(cachedList != null) {
			return cachedList;
		}

		List<MonsterCollectionShadowRecipe> shadows = Lists.newArrayList();
		if(RPMCommonConfig.ENABLE_JEI_SHADOW_RECIPE.get()) {
			ForgeEventHandler.getChatManager().getAllChats().stream()
					.flatMap(chat -> flatMapToLootTable(lootTableGetter, chat.entityType(), chat.message()))
					.forEach(shadows::add);
		}

		return cachedList = shadows;
	}

	private static Stream<MonsterCollectionShadowRecipe> flatMapToLootTable(Function<ResourceKey<LootTable>, LootTable> lootTableGetter, EntityType<?> entityType, @Nullable AbstractChatMessage chat) {
		if(chat == null) {
			return Stream.empty();
		}
		if(chat instanceof MaterialCollectionEndChatMessage materialCollection) {
			ResourceKey<LootTable> lootTableId = materialCollection.lootTable();
			ResourceLocation recipeId = lootTableId.location();
			return Stream.of(new MonsterCollectionShadowRecipe(
					entityType,
					getPossibleItemsFromLootTable(lootTableGetter.apply(lootTableId))
			));
		}
		Stream<MonsterCollectionShadowRecipe> ret = Stream.empty();
		List<ChatSelection> chatSelections = chat.getSelections();
		if(chatSelections != null) {
			ret = chatSelections.stream().flatMap(selection -> flatMapToLootTable(lootTableGetter, entityType, selection.getNext()));
		}

		return Stream.concat(flatMapToLootTable(lootTableGetter, entityType, chat.getNext()), ret);
	}
	private static List<Item> getPossibleItemsFromLootTable(LootTable lootTable) {
		ImmutableList.Builder<Item> builder = ImmutableList.builder();
		lootTable.pools.forEach(pool -> {
			for(LootPoolEntryContainer container: pool.entries) {
				if(container instanceof LootItem lootItem) {
					builder.add(lootItem.item.value());
				}
			}
		});

		return builder.build();
	}

	public static void setMonsterCollectionRecipes(List<MonsterCollectionShadowRecipe> shadows) {
		cachedList = shadows;
	}

	@Override
	public boolean matches(CraftingInput container, Level level) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int wid, int hgt) {
		return false;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RPMRecipeSerializers.MONSTER_COLLECTION_SHADOW_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RPMRecipes.MONSTER_COLLECTION_SHADOW_TYPE.get();
	}

	@OnlyIn(Dist.CLIENT)
	@Nullable
	public Entity getRenderEntity() {
		assert Minecraft.getInstance().level != null;
		return LAZY_RENDER_ENTITIES.computeIfAbsent(this.entityType, entityType1 -> this.entityType.create(Minecraft.getInstance().level));
	}
}
