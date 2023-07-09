package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMStructureKeys {
	public static final ResourceKey<Structure> CRYSTAL_SKULL_ISLAND = createKey("crystal_skull_island");

	private static ResourceKey<Structure> createKey(String name) {
		return ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(MODID, name));
	}

	public static void init() {
	}
}
