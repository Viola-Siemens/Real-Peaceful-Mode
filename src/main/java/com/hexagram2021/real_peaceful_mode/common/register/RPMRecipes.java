package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.CultureTableShadowRecipe;
import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.MonsterCollectionShadowRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMRecipes {
	private static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

	public static final DeferredHolder<RecipeType<?>, RecipeType<CultureTableShadowRecipe>> CULTURE_TABLE_SHADOW_TYPE = register("shadow/culture_table");
	public static final DeferredHolder<RecipeType<?>, RecipeType<MonsterCollectionShadowRecipe>> MONSTER_COLLECTION_SHADOW_TYPE = register("shadow/monster_collection");

	private RPMRecipes() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	@SuppressWarnings("SameParameterValue")
	private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> register(String name) {
		return REGISTER.register(name, () -> new RecipeType<>() {
			@Override
			public String toString() {
				return ResourceLocation.fromNamespaceAndPath(MODID, name).toString();
			}
		});
	}
}
