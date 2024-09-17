package com.hexagram2021.real_peaceful_mode.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class GlueEffect extends MobEffect {
	public GlueEffect() {
		super(MobEffectCategory.HARMFUL, 0x68d058);
	}

	@Override
	public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
		livingEntity.setDeltaMovement(0.0D, 0.0D, 0.0D);
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}
