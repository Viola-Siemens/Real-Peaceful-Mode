package com.hexagram2021.real_peaceful_mode.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin<T extends LivingEntity> {
	@Shadow @Final protected Class<T> targetType;

	@Shadow @Nullable protected LivingEntity target;

	@Inject(method = "findTarget", at = @At(value = "TAIL"))
	protected void ignoreHero(CallbackInfo ci) {
		if (this.targetType == Player.class || this.targetType == ServerPlayer.class) {
			if(this.target instanceof )
		}
	}
}
