package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMJukeboxSongKeys {
	public static final ResourceKey<JukeboxSong> MUSIC_DISC_ZOMBIE = create("rpm.zombie");
	public static final ResourceKey<JukeboxSong> MUSIC_DISC_SKELETON = create("rpm.skeleton");
	public static final ResourceKey<JukeboxSong> MUSIC_DISC_CREEPER = create("rpm.creeper");

	private static ResourceKey<JukeboxSong> create(String name) {
		return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(MODID, name));
	}

	private RPMJukeboxSongKeys() {
	}
}
