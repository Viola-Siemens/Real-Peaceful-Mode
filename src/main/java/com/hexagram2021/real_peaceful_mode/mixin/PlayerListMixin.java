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

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerList.class)
public class PlayerListMixin implements IPlayerListWithMissions {
	@Shadow @Final
	private MinecraftServer server;

	private final Map<UUID, PlayerMissions> missions = Maps.newHashMap();

	@Override
	public PlayerMissions getPlayerMissions(ServerPlayer player) {
		UUID uuid = player.getUUID();
		PlayerMissions playerMissions = this.missions.get(uuid);
		if (playerMissions == null) {
			RPMLogger.debug("Add new PlayerMissions for UUID: %s".formatted(uuid.toString()));
			Path path = this.server.getWorldPath(PLAYER_MISSIONS_DIR).resolve(uuid + ".json");
			playerMissions = new PlayerMissions(path, player);
			this.missions.put(uuid, playerMissions);
		}

		return playerMissions;
	}
}
