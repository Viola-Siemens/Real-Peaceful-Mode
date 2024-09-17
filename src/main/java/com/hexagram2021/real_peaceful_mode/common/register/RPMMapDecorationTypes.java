package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("NotNullFieldNotInitialized")
public final class RPMMapDecorationTypes {
	private static final DeferredRegister<MapDecorationType> REGISTER = DeferredRegister.create(Registries.MAP_DECORATION_TYPE, MODID);

	public static final DeferredHolder<MapDecorationType, MapDecorationType> SLIME_MAZE = register("slime_maze", true, 0x8eb683, false, true);
	public static final DeferredHolder<MapDecorationType, MapDecorationType> CRYSTAL_SKULL_ISLAND = register("crystal_skull_island", true, 0x80bbc9, false, true);

	public static Set<MapDecorationType> DECORATION_TYPES;

	@SuppressWarnings("SameParameterValue")
	private static DeferredHolder<MapDecorationType, MapDecorationType> register(String name, boolean showOnItemFrame, int mapColor, boolean explorationMapElement, boolean trackCount) {
		return REGISTER.register(name, () -> new MapDecorationType(ResourceLocation.fromNamespaceAndPath(MODID, name), showOnItemFrame, mapColor, explorationMapElement, trackCount));
	}

	private RPMMapDecorationTypes() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
