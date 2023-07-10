package com.hexagram2021.real_peaceful_mode.common.entity;

import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public interface IFriendlyMonster {
	boolean preventAttack(@Nullable LivingEntity target);
}
