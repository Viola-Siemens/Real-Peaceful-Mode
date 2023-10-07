package com.hexagram2021.real_peaceful_mode.api;

import com.google.common.collect.Sets;
import com.hexagram2021.real_peaceful_mode.common.spawner.AbstractEventSpawner;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

public class RandomEventSpawnerHelper {
	public static final Set<AbstractEventSpawner<?>> spawners = Sets.newIdentityHashSet();

	/**
	 * Subscribe ServerStartingEvent and call this in its handler function.
	 * @param s		the new spawner to be added.
	 */
	public static void registerRandomEventSpawner(AbstractEventSpawner<?> s) {
		spawners.add(s);
	}

	//You do NOT need to call this.
	@ApiStatus.Internal
	public static void clearRandomEventSpawners() {
		spawners.clear();
	}
}
