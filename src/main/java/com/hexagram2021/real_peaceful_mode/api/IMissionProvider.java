package com.hexagram2021.real_peaceful_mode.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;


//I don't think a event for you to cancel a mission trigger is a good choice. But I still provide you an injection point.
public interface IMissionProvider {
	/**
	 * A player-free decision tree to get which mission is available to trigger.
	 * @return		an available mission to trigger.
	 */
	@Nullable
	TriggerableMission<? extends IMissionProvider> getTriggerableMission();

	interface TriggerableMission<T extends IMissionProvider> extends StringRepresentable {
		/**
		 * If this mission is available to trigger, then trigger it.
		 * @param serverLevel	current level where IMissionProvider is at.
		 * @param outer			mission provider that will trigger this mission.
		 * @return				true is available, false otherwise.
		 */
		@SuppressWarnings("UnusedReturnValue")
		boolean tryTrigger(ServerLevel serverLevel, T outer);
	}
}
