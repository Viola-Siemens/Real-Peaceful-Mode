package com.hexagram2021.real_peaceful_mode.common.crafting.recipe;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.block.entity.CultureTableBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.register.RPMRecipeSerializers;
import com.hexagram2021.real_peaceful_mode.common.register.RPMRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public record CultureTableShadowRecipe(ResourceLocation id, Ingredient mix1, Ingredient mix2, Ingredient result) implements Recipe<CultureTableBlockEntity> {
	@Nullable
	private static List<CultureTableShadowRecipe> cachedList = null;

	public static List<CultureTableShadowRecipe> getCultureTableRecipes() {
		if(cachedList != null) {
			return cachedList;
		}

		List<CultureTableShadowRecipe> shadows = Lists.newArrayList();
		if(RPMCommonConfig.ENABLE_JEI_SHADOW_RECIPE.get()) {
			shadows.add(new CultureTableShadowRecipe(
					new ResourceLocation(MODID, "shadowed/culture_table/experiment_flower"),
					CultureTableBlockEntity.ACCEPTABLE_FLOWERS, CultureTableBlockEntity.ACCEPTABLE_FLOWERS,
					Ingredient.of(Items.GRASS, Items.BONE_MEAL, RPMItems.Materials.EXPERIMENT_FLOWER)
			));
			shadows.add(new CultureTableShadowRecipe(
					new ResourceLocation(MODID, "shadowed/culture_table/slime_colloid"),
					Ingredient.of(Items.WHEAT), Ingredient.of(Items.VINE),
					Ingredient.of(RPMItems.Materials.SLIME_COLLOID)
			));
		}

		return cachedList = shadows;
	}

	public static void setCultureTableRecipes(List<CultureTableShadowRecipe> shadows) {
		cachedList = shadows;
	}

	@Override
	public boolean matches(CultureTableBlockEntity container, Level level) {
		return this.mix1.test(container.getItem(CultureTableBlockEntity.SLOT_MIX1)) && this.mix2.test(container.getItem(CultureTableBlockEntity.SLOT_MIX2));
	}

	@Override
	public ItemStack assemble(CultureTableBlockEntity container, RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int wid, int hgt) {
		return false;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RPMRecipeSerializers.CULTURE_TABLE_SHADOW_SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return RPMRecipes.CULTURE_TABLE_SHADOW_TYPE.get();
	}
}
