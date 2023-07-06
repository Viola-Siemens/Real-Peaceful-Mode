package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMFluidTags {
	public static final TagKey<Fluid> MAGIC_POOL_WATER = create("magic_pool_water");
	public static final TagKey<Fluid> DARK_MAGIC_POOL_WATER = create("dark_magic_pool_water");

	private RPMFluidTags() {
	}

	private static TagKey<Fluid> create(String name) {
		return TagKey.create(Registries.FLUID, new ResourceLocation(MODID, name));
	}
}
