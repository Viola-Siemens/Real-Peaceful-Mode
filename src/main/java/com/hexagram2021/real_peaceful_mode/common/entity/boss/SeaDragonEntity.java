package com.hexagram2021.real_peaceful_mode.common.entity.boss;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;

public class SeaDragonEntity extends PathfinderMob implements RangedAttackMob {
	protected SeaDragonEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void performRangedAttack(LivingEntity target, float power) {

	}
}
