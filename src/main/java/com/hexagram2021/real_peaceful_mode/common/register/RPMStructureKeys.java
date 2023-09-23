package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("unused")
public class RPMStructureKeys {
	public static final ResourceKey<Structure> CRYSTAL_SKULL_ISLAND = createKey("crystal_skull_island");
	public static final ResourceKey<Structure> ABANDONED_MAGIC_POOL = createKey("abandoned_magic_pool");
	public static final ResourceKey<Structure> PINK_CREEPER = createKey("pink_creeper");
	public static final ResourceKey<Structure> ZOMBIE_FORT = createKey("zombie_fort");
	public static final ResourceKey<Structure> SKELETON_PALACE = createKey("skeleton_palace");
	public static final ResourceKey<Structure> CREEPER_TOWN = createKey("creeper_town");
	public static final ResourceKey<Structure> PHARAOH_ALTAR = createKey("pharaoh_altar");

	private static ResourceKey<Structure> createKey(String name) {
		return ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(MODID, name));
	}

	public static void init() {
	}
}
