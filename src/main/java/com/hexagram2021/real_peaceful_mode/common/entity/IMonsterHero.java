package com.hexagram2021.real_peaceful_mode.common.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

public interface IMonsterHero {
	boolean isHero(EntityType<?> monsterType);

	void setHero(EntityType<?> monsterType);

	Map<ResourceLocation, Integer> getHelpedMonsters();
}
