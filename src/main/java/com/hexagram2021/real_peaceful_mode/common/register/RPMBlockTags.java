package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMBlockTags {
	public static final TagKey<Block> CRACKABLE = create("crackable");

	private static TagKey<Block> create(String name) {
		return TagKey.create(Registries.BLOCK, new ResourceLocation(MODID, name));
	}

	private RPMBlockTags() {
	}

	public static void init() {
	}
}
