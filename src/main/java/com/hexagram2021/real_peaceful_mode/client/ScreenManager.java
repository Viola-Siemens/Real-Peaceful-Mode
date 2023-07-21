package com.hexagram2021.real_peaceful_mode.client;

import com.hexagram2021.real_peaceful_mode.client.screens.MissionListScreen;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedMission;
import com.hexagram2021.real_peaceful_mode.common.crafting.menu.MissionMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;
import java.util.Objects;

public final class ScreenManager {
	public static void openMissionListScreen(List<MissionManager.Mission> activeMissions, List<MissionManager.Mission> finishedMissions) {
		Minecraft.getInstance().setScreen(new MissionListScreen(activeMissions, finishedMissions));
	}

	public static void openMissionMessageScreen(MessagedMission mission, int containerId) {
		LocalPlayer player = Minecraft.getInstance().player;
		RPMLogger.debug(mission.createTag());
		if(player != null) {
			AbstractContainerMenu menu = player.containerMenu;
			if(menu.containerId == containerId && menu instanceof MissionMessageMenu missionMessageMenu) {
				missionMessageMenu.setMission(mission);
			}
		}
	}

	public static Player getLocalPlayer() {
		return Objects.requireNonNull(Minecraft.getInstance().player);
	}
}
