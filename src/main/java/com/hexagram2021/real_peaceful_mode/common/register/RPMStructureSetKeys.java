package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMStructureSetKeys {
	public static final ResourceKey<StructureSet> CRYSTAL_SKULL_ISLANDS = createKey("crystal_skull_islands");
	public static final ResourceKey<StructureSet> ABANDONED_MAGIC_POOLS = createKey("abandoned_magic_pools");
	public static final ResourceKey<StructureSet> PINK_CREEPERS = createKey("pink_creepers");
	public static final ResourceKey<StructureSet> ZOMBIE_FORTS = createKey("zombie_forts");
	public static final ResourceKey<StructureSet> SKELETON_PALACES = createKey("skeleton_palaces");
	public static final ResourceKey<StructureSet> CREEPER_TOWNS = createKey("creeper_towns");

	private static ResourceKey<StructureSet> createKey(String name) {
		return ResourceKey.create(Registries.STRUCTURE_SET, new ResourceLocation(MODID, name));
	}

	public static void init() {
	}
}
