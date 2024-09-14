package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.crafting.recipe_serializer.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMRecipeSerializers {
	public static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

	public static final RegistryObject<CultureTableShadowRecipeSerializer> CULTURE_TABLE_SHADOW_SERIALIZER = REGISTER.register(
			"shadowed/culture_table", CultureTableShadowRecipeSerializer::new
	);
	public static final RegistryObject<MonsterCollectionShadowRecipeSerializer> MONSTER_COLLECTION_SHADOW_SERIALIZER = REGISTER.register(
			"shadowed/monster_collection", MonsterCollectionShadowRecipeSerializer::new
	);

	private RPMRecipeSerializers() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
