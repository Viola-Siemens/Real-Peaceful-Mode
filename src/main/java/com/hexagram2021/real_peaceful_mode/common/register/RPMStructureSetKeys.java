package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMStructureSetKeys {
	public static final ResourceKey<StructureSet> CRYSTAL_SKULL_ISLANDS = createKey("crystal_skull_islands");
	public static final ResourceKey<StructureSet> ZOMBIE_FORTS = createKey("zombie_forts");

	private static ResourceKey<StructureSet> createKey(String name) {
		return ResourceKey.create(Registries.STRUCTURE_SET, new ResourceLocation(MODID, name));
	}

	public static void init() {
	}
}
