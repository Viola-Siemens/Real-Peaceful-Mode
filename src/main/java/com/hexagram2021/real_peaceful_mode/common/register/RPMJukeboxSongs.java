package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMJukeboxSongs {
	public static final DeferredRegister<JukeboxSong> REGISTER = DeferredRegister.create(Registries.JUKEBOX_SONG, MODID);

	public static final DeferredHolder<JukeboxSong, JukeboxSong> MUSIC_DISC_ZOMBIE = register(RPMJukeboxSongKeys.MUSIC_DISC_ZOMBIE, RPMSounds.MUSIC_DISC_ZOMBIE, 176, 1);
	public static final DeferredHolder<JukeboxSong, JukeboxSong> MUSIC_DISC_SKELETON = register(RPMJukeboxSongKeys.MUSIC_DISC_SKELETON, RPMSounds.MUSIC_DISC_SKELETON, 178, 2);
	public static final DeferredHolder<JukeboxSong, JukeboxSong> MUSIC_DISC_CREEPER = register(RPMJukeboxSongKeys.MUSIC_DISC_CREEPER, RPMSounds.MUSIC_DISC_CREEPER, 182, 3);

	private static DeferredHolder<JukeboxSong, JukeboxSong> register(ResourceKey<JukeboxSong> key, SoundEvent sound, float lengthInSeconds, int comparatorOutput) {
		return REGISTER.register(key.location().getPath(), () -> new JukeboxSong(Holder.direct(sound), Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), lengthInSeconds, comparatorOutput));
	}

	private RPMJukeboxSongs() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
