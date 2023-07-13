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
import java.util.function.Consumer;
import java.util.function.Predicate;

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

	@ApiStatus.Internal
	public static void triggerMissionForPlayers(MissionManager.Mission mission, SummonBlockEntity.SummonMissionType summonMissionType,
												List<ServerPlayer> players, IPlayerListWithMissions playerList, @Nullable LivingEntity npc, Consumer<ServerPlayer> additionWork) {
		players.forEach(player -> {
			additionWork.accept(player);
			if (player instanceof IMonsterHero hero && !player.getAbilities().instabuild && SummonBlockEntity.checkMission(hero, summonMissionType, mission)) {
				PlayerMissions playerMissions = playerList.getPlayerMissions(player);
				switch (summonMissionType) {
					case RECEIVE -> playerMissions.receiveNewMission(mission, npc);
					case FINISH -> playerMissions.finishMission(mission, npc);
				}
			}
		});
	}
}
