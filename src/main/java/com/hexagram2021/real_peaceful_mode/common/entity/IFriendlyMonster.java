package com.hexagram2021.real_peaceful_mode.common.entity;

import net.minecraft.world.entity.LivingEntity;

public interface IFriendlyMonster {
	boolean preventAttack(LivingEntity target);
}
