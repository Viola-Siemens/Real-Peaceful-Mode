package com.hexagram2021.real_peaceful_mode.api.event;

import com.hexagram2021.real_peaceful_mode.api.RandomEventSpawnerHelper;
import com.hexagram2021.real_peaceful_mode.common.spawner.AbstractEventSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public class RegisterRandomEventSpawnerEvent extends Event {
	private final Dist side;

	@ApiStatus.Internal
	public RegisterRandomEventSpawnerEvent(Dist side) {
		this.side = side;
	}


	/**
	 * Register a random event spawner.
	 * @param s		the new spawner to be added.
	 * @see         com.hexagram2021.real_peaceful_mode.common.spawner.AbstractEventSpawner
	 */
	public void register(AbstractEventSpawner<?> s) {
		RandomEventSpawnerHelper.registerRandomEventSpawner(s);
	}

	public boolean isClient() {
		return this.side.isClient();
	}
}
