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
	public static final SoundEvent NOTE_BLOCK_IMITATE_DARK_ZOMBIE_KNIGHT = registerSound("block.note_block.imitate.dark_zombie_knight");
	public static final SoundEvent DARK_ZOMBIE_KNIGHT_AMBIENT = registerSound("entity.dark_zombie_knight.ambient");
	public static final SoundEvent DARK_ZOMBIE_KNIGHT_HURT = registerSound("entity.dark_zombie_knight.hurt");
	public static final SoundEvent DARK_ZOMBIE_KNIGHT_DEATH = registerSound("entity.dark_zombie_knight.death");

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
