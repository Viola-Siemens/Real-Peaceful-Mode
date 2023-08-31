package com.hexagram2021.real_peaceful_mode.api;

import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.mission.IPlayerListWithMissions;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import com.hexagram2021.real_peaceful_mode.common.mission.PlayerMissions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 	There's two approaches to trigger missions. One is to use MissionHelper.triggerMissionForPlayers, the other one is
 * to use Summon Block. For datapack developers, the second choice seems to be the only choice.
 *
 * @see SummonBlockEntity#load
 */
public class MissionHelper {
	/**
	 * API for custom mods to trigger mission on/off.
	 *
	 * @see com.hexagram2021.real_peaceful_mode.common.register.RPMFluids#DARK_MAGIC_POOL_WATER_FLUID
	 * @see com.hexagram2021.real_peaceful_mode.common.entity.boss.ZombieTyrant#die
	 * @see com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity.SummonMissionType
	 *
	 * @param missionId				Mission ID (for example, "real_peaceful_mode:zombie3") of the triggered mission.
	 * @param summonMissionType		RECEIVE for receiving a new mission, FINISH for finishing a mission.
	 * @param serverLevel			Server Level (Dimension) to trigger the mission.
	 * @param predicate				What kind of players can trigger the mission?
	 *                          	No need to check PlayerMissions.activeMissions or PlayerMissions.finishedMissions
	 *                          because they will be checked internally.
	 *                          	For example, player -> player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)
	 * @param npc					The NPC that will be rendered in GUI. This field could be null if the messages of
	 *                          the mission are monologue of player.
	 * @param additionWork			Additional works to do with each related players.
	 */
	public static void triggerMissionForPlayers(ResourceLocation missionId, SummonBlockEntity.SummonMissionType summonMissionType,
												ServerLevel serverLevel, Predicate<ServerPlayer> predicate, @Nullable LivingEntity npc, Consumer<ServerPlayer> additionWork) {
		ForgeEventHandler.getMissionManager().getMission(missionId).ifPresent(mission -> triggerMissionForPlayers(
				mission, summonMissionType, serverLevel.getPlayers(predicate),
				(IPlayerListWithMissions) serverLevel.getServer().getPlayerList(),
				npc, additionWork
		));
	}

	/**
	 * API for custom mods to trigger mission on/off for a single player.
	 *
	 * @see com.hexagram2021.real_peaceful_mode.common.entity.PinkCreeperEntity#tick
	 *
	 * @param missionId				Mission ID (for example, "real_peaceful_mode:zombie3") of the triggered mission.
	 * @param summonMissionType		RECEIVE for receiving a new mission, FINISH for finishing a mission.
	 * @param player				The player who triggers the mission.
	 * @param npc                    The NPC that will be rendered in GUI. This field could be null if the messages of
	 *                          the mission are monologue of player.
	 * @param additionWork			Additional works to do with the player when trigger the mission.
	 */
	public static void triggerMissionForPlayer(ResourceLocation missionId, SummonBlockEntity.SummonMissionType summonMissionType,
											   ServerPlayer player, @Nullable LivingEntity npc, Consumer<ServerPlayer> additionWork) {
		ForgeEventHandler.getMissionManager().getMission(missionId).ifPresent(mission -> triggerMissionForPlayer(
				mission, summonMissionType, player,
				(IPlayerListWithMissions) Objects.requireNonNull(player.level().getServer()).getPlayerList(),
				npc, additionWork
		));
	}

	//You don't need to call this api lol.
	@ApiStatus.Internal
	public static void triggerMissionForPlayers(MissionManager.Mission mission, SummonBlockEntity.SummonMissionType summonMissionType,
												List<ServerPlayer> players, IPlayerListWithMissions playerList, @Nullable LivingEntity npc, Consumer<ServerPlayer> additionWork) {
		players.forEach(player -> triggerMissionForPlayer(mission, summonMissionType, player, playerList, npc, additionWork));
	}

	@ApiStatus.Internal
	public static void triggerMissionForPlayer(MissionManager.Mission mission, SummonBlockEntity.SummonMissionType summonMissionType,
											   ServerPlayer player, IPlayerListWithMissions playerList, @Nullable LivingEntity npc, Consumer<ServerPlayer> additionWork) {
		if (player instanceof IMonsterHero hero && !player.getAbilities().instabuild && checkMission(hero, summonMissionType, mission)) {
			additionWork.accept(player);
			PlayerMissions playerMissions = playerList.getPlayerMissions(player);
			switch (summonMissionType) {
				case RECEIVE -> playerMissions.receiveNewMission(mission, npc);
				case FINISH -> playerMissions.finishMission(mission, npc);
			}
		}
	}

	@ApiStatus.Internal
	public static boolean checkMission(IMonsterHero hero, SummonBlockEntity.SummonMissionType type, @Nullable MissionManager.Mission mission) {
		PlayerMissions playerMissions = hero.getPlayerMissions();
		if(mission == null) {
			return true;
		}
		ResourceLocation missionId = mission.id();
		if(IMonsterHero.missionDisabled(missionId)) {
			return false;
		}
		if (type == SummonBlockEntity.SummonMissionType.RECEIVE) {
			if(IMonsterHero.underMission(playerMissions, missionId) || IMonsterHero.completeMission(playerMissions, missionId)) {
				return false;
			}
			for(ResourceLocation former: mission.formers()) {
				if(!IMonsterHero.missionDisabled(former) && !IMonsterHero.completeMission(playerMissions, former)) {
					return false;
				}
			}
			return true;
		}
		return IMonsterHero.underMission(playerMissions, missionId);
	}
}
