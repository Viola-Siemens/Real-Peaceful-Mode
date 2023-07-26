package com.hexagram2021.real_peaceful_mode.api;

import com.google.common.collect.Sets;
import com.hexagram2021.real_peaceful_mode.common.spawner.AbstractEventSpawner;

import java.util.Set;

public class RandomEventSpawnerHelper {
	public static final Set<AbstractEventSpawner<?>> spawners = Sets.newIdentityHashSet();

	public static void registerRandomEventSpawner(AbstractEventSpawner<?> s) {
		spawners.add(s);
	}
}
