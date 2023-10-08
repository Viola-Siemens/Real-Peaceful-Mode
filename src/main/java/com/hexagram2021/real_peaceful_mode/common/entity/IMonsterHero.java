package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.mission.PlayerMissions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

public interface IMonsterHero {
	boolean isHero(EntityType<?> monsterType);

	void setHero(EntityType<?> monsterType);

	Map<ResourceLocation, Integer> getHelpedMonsters();

	PlayerMissions getPlayerMissions();

	static boolean isAtMissionsBetween(PlayerMissions playerMissions, ResourceLocation finished, ResourceLocation toFinish) {
		return completeMission(playerMissions, finished) && !completeMission(playerMissions, toFinish) && !underMission(playerMissions, toFinish);
	}

	static boolean underMission(PlayerMissions playerMissions, ResourceLocation toFinish) {
		return playerMissions.getActiveMissions().contains(toFinish);
	}

	static boolean completeMission(PlayerMissions playerMissions, ResourceLocation finish) {
		return playerMissions.getFinishedMissions().contains(finish);
	}

	static boolean missionDisabled(ResourceLocation mission) {
		return RPMCommonConfig.DISABLE_MISSIONS.get().contains(mission.toString());
	}

	static boolean eventDisabledFor(ResourceLocation entityType) {
		return RPMCommonConfig.DISABLE_EVENTS.get().contains(entityType.toString());
	}
}
