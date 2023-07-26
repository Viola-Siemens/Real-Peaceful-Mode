package com.hexagram2021.real_peaceful_mode.common.spawner;

import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public abstract class AbstractEventSpawner<T extends LivingEntity> implements CustomSpawner {
	private int tickDelay;
	private int possibility;

	@Override
	public int tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
		if(--this.tickDelay > 0) {
			return 0;
		}
		this.tickDelay = RPMCommonConfig.RANDOM_EVENT_CHECKER_INTERVAL.get();
		if(RPMCommonConfig.DISABLE_EVENTS.get().contains(getRegistryName(this.getMonsterType()).toString())) {
			return 0;
		}
		level.players().forEach(player -> {
			if(player instanceof IMonsterHero hero && IMonsterHero.underMission(hero.getPlayerMissions(), this.getMissionId())) {
				hero.getPlayerMissions().removeMission(this.getMissionId());
			}
		});
		if(level.getRandom().nextInt(100) < this.possibility) {
			List<ServerPlayer> availablePlayers = level.players().stream()
					.filter(player -> player instanceof IMonsterHero hero && hero.isHero(this.getMonsterType()) && this.checkSpawnConditions(level, player))
					.toList();
			if(availablePlayers.isEmpty()) {
				return 0;
			}
			ServerPlayer luckyDog = availablePlayers.get(level.getRandom().nextInt(availablePlayers.size()));
			if(this.spawnEventNpc(level, luckyDog)) {
				this.possibility = RPMCommonConfig.RANDOM_EVENT_POSSIBILITY.get();
				return 1;
			}
		} else {
			this.possibility = Math.min(this.possibility + RPMCommonConfig.RANDOM_EVENT_POSSIBILITY_ADDER.get(), RPMCommonConfig.RANDOM_EVENT_POSSIBILITY_MAX.get());
		}

		return 0;
	}

	//player: should be the hero of getMonsterType()
	//return: true if spawn successfully, false if not.
	protected abstract boolean spawnEventNpc(ServerLevel level, ServerPlayer player);

	protected abstract EntityType<T> getMonsterType();

	public abstract ResourceKey<Level> dimension();

	protected abstract boolean checkSpawnConditions(ServerLevel level, ServerPlayer player);

	protected abstract ResourceLocation getMissionId();
}
