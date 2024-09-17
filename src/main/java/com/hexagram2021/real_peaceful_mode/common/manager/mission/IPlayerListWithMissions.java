package com.hexagram2021.real_peaceful_mode.common.manager.mission;

import net.minecraft.server.level.ServerPlayer;

public interface IPlayerListWithMissions {
	PlayerMissions rpm$getPlayerMissions(ServerPlayer player);
}
