package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMBiomeTags {
	public static final TagKey<Biome> HAS_CRYSTAL_SKULL_ISLAND = create("has_structure/crystal_skull_island");

	private static TagKey<Biome> create(String name) {
		return TagKey.create(Registries.BIOME, new ResourceLocation(MODID, name));
	}

	private RPMBiomeTags() {
	}

	public static void init() {
	}
}
