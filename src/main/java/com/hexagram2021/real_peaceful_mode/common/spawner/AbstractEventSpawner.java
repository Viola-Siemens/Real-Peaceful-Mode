package com.hexagram2021.real_peaceful_mode.common.spawner;

import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.mojang.datafixers.util.Function3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public abstract class AbstractEventSpawner<T extends LivingEntity> implements CustomSpawner {
	private int tickDelay;
	private int possibility;

	protected static final Consumer<Mob> MOB_SWEAT = mob -> {
		if(!mob.level().isClientSide && mob.getRandom().nextInt(64) == 0) {
			mob.level().broadcastEntityEvent(mob, EntityEvent.VILLAGER_SWEAT);
		}
	};

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
			this.possibility = Math.max(RPMCommonConfig.RANDOM_EVENT_POSSIBILITY.get(), Math.min(this.possibility + RPMCommonConfig.RANDOM_EVENT_POSSIBILITY_ADDER.get(), RPMCommonConfig.RANDOM_EVENT_POSSIBILITY_MAX.get()));
		}

		return 0;
	}

	/**
	 * @see 			AbstractEventSpawner#spawnEventFor(ServerLevel, ServerPlayer, Function3, Consumer)
	 * @param level		dimension of the random event.
	 * @param player	the player chosen to trigger the random event.
	 * @return			true if event is successfully triggered, false if not.
	 */
	protected abstract boolean spawnEventNpc(ServerLevel level, ServerPlayer player);

	public abstract EntityType<T> getMonsterType();

	public abstract ResourceKey<Level> dimension();

	protected abstract boolean checkSpawnConditions(ServerLevel level, ServerPlayer player);

	protected abstract ResourceLocation getMissionId();

	public abstract boolean isInteractItem(Holder<Item> item);

	/**
	 * API for spawn random event by command. Override it if needed.
	 * @param level		dimension of the random event.
	 * @param player	the player chosen to trigger the random event.
	 * @param missionId	the id of mission
	 * @return			true if event is successfully triggered, false if not.
	 */
	public boolean spawnEventFor(ServerLevel level, ServerPlayer player, ResourceLocation missionId) {
		if(missionId.equals(this.getMissionId())) {
			return this.spawnEventNpc(level, player);
		}
		return false;
	}

	public boolean spawnEventFor(ServerLevel level, ServerPlayer player, Function3<ServerLevel, BlockPos, Float, Boolean> missionContent, Consumer<BlockPos> toDo) {
		float angle = player.getYRot() * Mth.PI / 180.0F;
		Vec3 pos = player.position().subtract(-Mth.sin(angle) * 16, 0, Mth.cos(angle) * 16);
		BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y - 5, (int) pos.z);
		if(level.getBlockState(blockPos).isAir()) {
			return false;
		}
		for(int y = 1; y < 10; ++y) {
			BlockPos tryPos = blockPos.above(y);
			if(level.getBlockState(tryPos).isAir()) {
				boolean ret = missionContent.apply(level, tryPos, player.getYRot());
				toDo.accept(tryPos);
				return ret;
			}
		}
		return false;
	}
}
