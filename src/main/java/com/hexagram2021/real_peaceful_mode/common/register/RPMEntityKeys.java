package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("unused")
public final class RPMEntityKeys {
	public static final ResourceKey<EntityType<?>> DARK_ZOMBIE_KNIGHT = createKey("dark_zombie_knight");
	public static final ResourceKey<EntityType<?>> PINK_CREEPER = createKey("pink_creeper");
	public static final ResourceKey<EntityType<?>> HUSK_WORKMAN = createKey("husk_workman");
	public static final ResourceKey<EntityType<?>> ZOMBIE_TYRANT = createKey("zombie_tyrant");
	public static final ResourceKey<EntityType<?>> SKELETON_KING = createKey("skeleton_king");
	public static final ResourceKey<EntityType<?>> HUSK_PHARAOH = createKey("husk_pharaoh");
	public static final ResourceKey<EntityType<?>> GUARD_SLIME = createKey("guard_slime");
	public static final ResourceKey<EntityType<?>> SKELETON_SKULL = createKey("skeleton_skull");
	public static final ResourceKey<EntityType<?>> TINY_FIREBALL = createKey("tiny_fireball");
	public static final ResourceKey<EntityType<?>> FLAME_CRYSTAL = createKey("flame_crystal");

	private RPMEntityKeys() {
	}

	private static ResourceKey<EntityType<?>> createKey(String name) {
		return ResourceKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MODID, name));
	}

	public static void init() {
	}
}
