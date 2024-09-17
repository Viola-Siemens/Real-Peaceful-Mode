package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.crafting.recipe_serializer.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMRecipeSerializers {
	public static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

	public static final DeferredHolder<RecipeSerializer<?>, CultureTableShadowRecipeSerializer> CULTURE_TABLE_SHADOW_SERIALIZER = REGISTER.register(
			"shadowed/culture_table", CultureTableShadowRecipeSerializer::new
	);
	public static final DeferredHolder<RecipeSerializer<?>, MonsterCollectionShadowRecipeSerializer> MONSTER_COLLECTION_SHADOW_SERIALIZER = REGISTER.register(
			"shadowed/monster_collection", MonsterCollectionShadowRecipeSerializer::new
	);

	private RPMRecipeSerializers() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
