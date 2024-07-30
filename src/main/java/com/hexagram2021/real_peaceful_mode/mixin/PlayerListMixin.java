package com.hexagram2021.real_peaceful_mode.mixin;

import com.google.common.collect.Maps;
import com.hexagram2021.real_peaceful_mode.common.mission.IPlayerListWithMissions;
import com.hexagram2021.real_peaceful_mode.common.mission.PlayerMissions;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.UUID;

@Mixin(PlayerList.class)
public class PlayerListMixin implements IPlayerListWithMissions {
	@Unique
	private final Map<UUID, PlayerMissions> rpm$missions = Maps.newHashMap();

	@Override
	public PlayerMissions rpm$getPlayerMissions(ServerPlayer player) {
		UUID uuid = player.getUUID();
		PlayerMissions playerMissions = this.rpm$missions.get(uuid);
		if (playerMissions == null) {
			RPMLogger.debug("Add new PlayerMissions for UUID: %s".formatted(uuid.toString()));
			playerMissions = new PlayerMissions(player);
			this.rpm$missions.put(uuid, playerMissions);
		} else {
			playerMissions.setPlayer(player);
		}

		return playerMissions;
	}
}
