package com.hexagram2021.real_peaceful_mode.mixin;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMobEffects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin {
	@Shadow @Final
	protected PathfinderMob mob;

	@WrapOperation(method = "canUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;createPath(Lnet/minecraft/world/entity/Entity;I)Lnet/minecraft/world/level/pathfinder/Path;"))
	@Nullable
	public Path rpm$createTrancePath(PathNavigation instance, Entity entity, int reachRange, Operation<Path> original) {
		if(this.mob.hasEffect(RPMMobEffects.TRANCE.get())) {
			float diffX = 12.0F * Mth.sin((float) (this.mob.tickCount * Math.PI / 101.0D)) + 5.0F * Mth.sin((float) (this.mob.tickCount * Math.PI / 89.0D + 13.0D * Math.PI));
			float diffZ = 12.0F * Mth.sin((float) (this.mob.tickCount * Math.PI / 97.0D)) + 5.0F * Mth.sin((float) (this.mob.tickCount * Math.PI / 83.0D + 11.0D * Math.PI));
			return instance.createPath(ImmutableSet.of(entity.blockPosition().offset((int) diffX, 0, (int) diffZ)), 16, true, reachRange);
		}
		return original.call(instance, entity, reachRange);
	}
}
