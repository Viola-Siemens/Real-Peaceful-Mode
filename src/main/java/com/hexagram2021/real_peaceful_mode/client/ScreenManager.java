package com.hexagram2021.real_peaceful_mode.client;

import com.hexagram2021.real_peaceful_mode.client.screens.MissionListScreen;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import net.minecraft.client.Minecraft;

import java.util.List;

public final class ScreenManager {
	public static void openMissionListScreen(List<MissionManager.Mission> activeMissions, List<MissionManager.Mission> finishedMissions) {
		Minecraft.getInstance().setScreen(new MissionListScreen(activeMissions, finishedMissions));
	}
}
