package com.hexagram2021.real_peaceful_mode.common.spawner;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.CustomSpawner;

public abstract class AbstractEventSpawner<T extends LivingEntity> implements CustomSpawner {
	//Spawners for random events for player who is regarded as heroes of the monster.

	@Override
	public int tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
		//TODO @foliet
		return 0;
	}

	//player: should be the hero of getMonsterType()
	//return: true if spawn successfully, false if not.
	protected abstract boolean spawnEventNpc(ServerLevel level, ServerPlayer player);

	protected abstract EntityType<T> getMonsterType();
}
