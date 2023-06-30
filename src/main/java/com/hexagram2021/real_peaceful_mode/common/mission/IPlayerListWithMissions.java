package com.hexagram2021.real_peaceful_mode.common.mission;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

public interface IPlayerListWithMissions {
	LevelResource PLAYER_MISSIONS_DIR = new LevelResource("rpm-missions");

	PlayerMissions getPlayerMissions(ServerPlayer player);
}
