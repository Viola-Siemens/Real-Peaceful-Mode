package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMSounds {
	static final Map<ResourceLocation, SoundEvent> registeredEvents = new HashMap<>();
	public static final SoundEvent VILLAGER_WORK_SENIOR = registerSound("entity.villager.work_senior");
	public static final SoundEvent VILLAGER_WORK_BOTANIST = registerSound("entity.villager.work_botanist");
	public static final SoundEvent VILLAGER_WORK_PLUMBER = registerSound("entity.villager.work_plumber");
	public static final SoundEvent NOTE_BLOCK_IMITATE_DARK_ZOMBIE_KNIGHT = registerSound("block.note_block.imitate.dark_zombie_knight");
	public static final SoundEvent DARK_ZOMBIE_KNIGHT_AMBIENT = registerSound("entity.dark_zombie_knight.ambient");
	public static final SoundEvent DARK_ZOMBIE_KNIGHT_HURT = registerSound("entity.dark_zombie_knight.hurt");
	public static final SoundEvent DARK_ZOMBIE_KNIGHT_DEATH = registerSound("entity.dark_zombie_knight.death");
	public static final SoundEvent ZOMBIE_TYRANT_AMBIENT = registerSound("entity.zombie_tyrant.ambient");
	public static final SoundEvent ZOMBIE_TYRANT_DEATH = registerSound("entity.zombie_tyrant.death");
	public static final SoundEvent ZOMBIE_TYRANT_SPELL = registerSound("entity.zombie_tyrant.spell");
	public static final SoundEvent SKELETON_KING_AMBIENT = registerSound("entity.skeleton_king.ambient");
	public static final SoundEvent SKELETON_KING_DEATH = registerSound("entity.skeleton_king.death");
	public static final SoundEvent HUSK_PHARAOH_AMBIENT = registerSound("entity.husk_pharaoh.ambient");
	public static final SoundEvent HUSK_PHARAOH_DEATH = registerSound("entity.husk_pharaoh.death");

	public static final SoundEvent MUSIC_DISC_ZOMBIE = registerSound("music_disc.rpm.zombie");
	public static final SoundEvent MUSIC_DISC_SKELETON = registerSound("music_disc.rpm.skeleton");
	public static final SoundEvent MUSIC_DISC_CREEPER = registerSound("music_disc.rpm.creeper");

	private static SoundEvent registerSound(String name) {
		ResourceLocation location = new ResourceLocation(MODID, name);
		SoundEvent event = SoundEvent.createVariableRangeEvent(location);
		registeredEvents.put(location, event);
		return event;
	}

	public static void init(RegisterEvent event) {
		event.register(Registries.SOUND_EVENT, helper -> registeredEvents.forEach(helper::register));
	}
}
