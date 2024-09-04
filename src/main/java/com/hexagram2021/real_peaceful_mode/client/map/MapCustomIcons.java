package com.hexagram2021.real_peaceful_mode.client.map;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapDecoration;

import java.util.Map;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;
import static com.hexagram2021.real_peaceful_mode.common.register.RPMMapDecorationTypes.*;

public final class MapCustomIcons {
	private static final RenderType MAP_ICONS = RenderType.text(new ResourceLocation(MODID, "textures/map/map_icons.png"));

	public static final Map<MapDecoration.Type, RenderType> RENDER_TYPES;
	public static final Map<MapDecoration.Type, Byte> ORDINARIES;
	private static byte ordinaryCount = 0;

	private MapCustomIcons() {
	}

	private static void register(MapDecoration.Type type, ImmutableMap.Builder<MapDecoration.Type, RenderType> typesBuilder, ImmutableMap.Builder<MapDecoration.Type, Byte> ordinariesBuilder) {
		typesBuilder.put(type, MAP_ICONS);
		ordinariesBuilder.put(type, ordinaryCount);
		ordinaryCount += 1;
	}

	static {
		ImmutableMap.Builder<MapDecoration.Type, RenderType> typesBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<MapDecoration.Type, Byte> ordinariesBuilder = ImmutableMap.builder();

		register(SLIME_MAZE, typesBuilder, ordinariesBuilder);
		register(CRYSTAL_SKULL_ISLAND, typesBuilder, ordinariesBuilder);

		RENDER_TYPES = typesBuilder.build();
		ORDINARIES = ordinariesBuilder.build();
	}
}
