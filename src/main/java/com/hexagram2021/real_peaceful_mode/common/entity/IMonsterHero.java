package com.hexagram2021.real_peaceful_mode.common.entity;

import net.minecraft.world.entity.EntityType;

public interface IMonsterHero {
	boolean isHero(EntityType<?> monsterType);

	void setHero(EntityType<?> monsterType);
}
