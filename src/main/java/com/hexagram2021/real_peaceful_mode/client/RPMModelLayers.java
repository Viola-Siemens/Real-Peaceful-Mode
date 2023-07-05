package com.hexagram2021.real_peaceful_mode.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMModelLayers {
	public static final ModelLayerLocation DARK_ZOMBIE_KNIGHT_SKULL = register("dark_zombie_knight_skull");

	private static ModelLayerLocation register(String name) {
		return register(new ResourceLocation(MODID, name), "main");
	}

	@SuppressWarnings("SameParameterValue")
	private static ModelLayerLocation register(ResourceLocation id, String description) {
		return new ModelLayerLocation(id, description);
	}
}
