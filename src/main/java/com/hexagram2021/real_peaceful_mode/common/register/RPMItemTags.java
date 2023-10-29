package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMItemTags {
	public static final TagKey<Item> CONVERTIBLE = create("beads/convertible");

	private static TagKey<Item> create(String name) {
		return TagKey.create(Registries.ITEM, new ResourceLocation(MODID, name));
	}

	private RPMItemTags() {
	}

	public static void init() {
	}
}
