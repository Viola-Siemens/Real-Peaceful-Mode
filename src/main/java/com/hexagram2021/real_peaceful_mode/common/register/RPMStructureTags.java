package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMStructureTags {
	public static final TagKey<Structure> ON_VOID_EXPLORER_MAPS = create("on_void_explorer_maps");
	public static final TagKey<Structure> ON_SLIME_EXPLORER_MAPS = create("on_slime_explorer_maps");

	public static final TagKey<Structure> PINK_CREEPERS_TOWN = create("pink_creepers_town");

	@SuppressWarnings("SameParameterValue")
	private static TagKey<Structure> create(String name) {
		return TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
	}

	public static void init() {
	}
}
