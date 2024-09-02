package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.PlayerMissions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public interface IMonsterHero {
	String HELPED_MONSTERS = "helpedMonsters";
	String MATERIAL_COLLECTIONS = "materialCollections";

	boolean rpm$isHero(EntityType<?> monsterType);
	void rpm$setHero(EntityType<?> monsterType);

	boolean rpm$canCollectMaterial(ResourceLocation lootTable);
	boolean rpm$materialCollected(ResourceLocation lootTable, LivingEntity livingEntity);
	void rpm$startCollectingMaterial(ResourceLocation lootTable, LivingEntity livingEntity, long duration);
	boolean rpm$endCollectingMaterial(ResourceLocation lootTable);

	Map<ResourceLocation, Integer> rpm$getHelpedMonsters();

	PlayerMissions rpm$getPlayerMissions();

	Map<ResourceLocation, MaterialCollection> rpm$getMaterialCollections();

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
	static boolean chatDisabledFor(ResourceLocation entityType) {
		return RPMCommonConfig.DISABLE_CHATS.get().contains(entityType.toString());
	}
}
