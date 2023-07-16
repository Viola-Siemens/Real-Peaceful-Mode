package com.hexagram2021.real_peaceful_mode.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMModelLayers {
	public static final ModelLayerLocation DARK_ZOMBIE_KNIGHT = register("dark_zombie_knight");
	public static final ModelLayerLocation PINK_CREEPER = register("pink_creeper");
	public static final ModelLayerLocation DARK_ZOMBIE_KNIGHT_INNER_ARMOR = registerInnerArmor("dark_zombie_knight");
	public static final ModelLayerLocation DARK_ZOMBIE_KNIGHT_OUTER_ARMOR = registerOuterArmor("dark_zombie_knight");
	public static final ModelLayerLocation ZOMBIE_TYRANT = register("zombie_tyrant");
	public static final ModelLayerLocation SKELETON_KING = register("skeleton_king");
	public static final ModelLayerLocation DARK_ZOMBIE_KNIGHT_SKULL = register("dark_zombie_knight_skull");
	public static final ModelLayerLocation SKELETON_SKULL = register("skeleton_skull");

	private static ModelLayerLocation register(String name) {
		return register(new ResourceLocation(MODID, name), "main");
	}

	private static ModelLayerLocation registerInnerArmor(String name) {
		return register(new ResourceLocation(MODID, name), "inner_armor");
	}

	private static ModelLayerLocation registerOuterArmor(String name) {
		return register(new ResourceLocation(MODID, name), "outer_armor");
	}

	private static ModelLayerLocation register(ResourceLocation id, String description) {
		return new ModelLayerLocation(id, description);
	}
}
