package com.hexagram2021.real_peaceful_mode.common.spawner.zombie;

import com.hexagram2021.real_peaceful_mode.common.spawner.AbstractEventSpawner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieHelmetEventSpawner extends AbstractEventSpawner<Zombie> {

	@Override
	protected boolean spawnEventNpc(ServerLevel level, ServerPlayer player) {
		return false;
	}

	@Override
	protected EntityType<Zombie> getMonsterType() {
		return EntityType.ZOMBIE;
	}
}
