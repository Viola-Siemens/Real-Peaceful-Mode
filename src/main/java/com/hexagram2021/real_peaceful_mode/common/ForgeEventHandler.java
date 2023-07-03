package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeEventHandler {
	private MissionManager missionManager;

	@SubscribeEvent
	public void onResourceReload(AddReloadListenerEvent event) {
		this.missionManager = new MissionManager();
		event.addListener(this.missionManager);
	}

	public MissionManager getMissionManager() {
		return this.missionManager;
	}
}
