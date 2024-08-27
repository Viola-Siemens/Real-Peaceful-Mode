package com.hexagram2021.real_peaceful_mode.client;

import com.hexagram2021.real_peaceful_mode.client.screens.MissionListScreen;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedChat;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedMission;
import com.hexagram2021.real_peaceful_mode.common.crafting.menu.ChatMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.crafting.menu.MissionMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.Mission;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public final class ScreenManager {
	public static void openMissionListScreen(List<Mission> activeMissions, List<Mission> finishedMissions) {
		Minecraft.getInstance().setScreen(new MissionListScreen(activeMissions, finishedMissions));
	}

	public static void openMissionMessageScreen(MessagedMission mission, int containerId) {
		LocalPlayer player = Minecraft.getInstance().player;
		//com.hexagram2021.real_peaceful_mode.common.util.RPMLogger.debug(mission.createTag());
		if(player != null) {
			AbstractContainerMenu menu = player.containerMenu;
			if(menu.containerId == containerId && menu instanceof MissionMessageMenu missionMessageMenu) {
				missionMessageMenu.setMission(mission);
			}
		}
	}
	public static void openChatMessageScreen(MessagedChat chat, int containerId) {
		LocalPlayer player = Minecraft.getInstance().player;
		//com.hexagram2021.real_peaceful_mode.common.util.RPMLogger.debug(chat.createTag());
		if(player != null) {
			AbstractContainerMenu menu = player.containerMenu;
			if(menu.containerId == containerId && menu instanceof ChatMessageMenu chatMessageMenu) {
				chatMessageMenu.setChat(chat);
			}
		}
	}
	public static void updateChatSelections(@Nullable List<ChatSelection> chatSelections) {
		LocalPlayer player = Minecraft.getInstance().player;
		//if (chatSelections != null) {
		//	chatSelections.forEach(chatSelection -> com.hexagram2021.real_peaceful_mode.common.util.RPMLogger.debug(ChatSelection.CODEC.encode(chatSelection, com.mojang.serialization.JsonOps.INSTANCE, new com.google.gson.JsonObject()).getOrThrow(false, com.hexagram2021.real_peaceful_mode.common.util.RPMLogger::error)));
		//}
		if(player != null && player.containerMenu instanceof ChatMessageMenu chatMessageMenu) {
			chatMessageMenu.setCachedSelections(chatSelections);
		}
	}

	public static Player getLocalPlayer() {
		return Objects.requireNonNull(Minecraft.getInstance().player);
	}
}
