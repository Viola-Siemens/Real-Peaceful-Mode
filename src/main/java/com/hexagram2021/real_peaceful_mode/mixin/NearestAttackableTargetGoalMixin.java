package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal {
	@Shadow @Final protected Class<T> targetType;

	@Shadow @Nullable protected LivingEntity target;

	public NearestAttackableTargetGoalMixin(Mob mob, boolean mustSee) {
		super(mob, mustSee);
	}

	public NearestAttackableTargetGoalMixin(Mob mob, boolean mustSee, boolean mustReach) {
		super(mob, mustSee, mustReach);
	}

	@Inject(method = "findTarget", at = @At(value = "TAIL"))
	protected void ignoreHero(CallbackInfo ci) {
		if (this.targetType == Player.class || this.targetType == ServerPlayer.class) {
			if(this.target instanceof IMonsterHero hero) {
				if(hero.isHero(this.mob.getType())) {
					this.target = null;
				}
			}
		}
		if(this.mob instanceof IFriendlyMonster friendlyMonster && friendlyMonster.preventAttack(this.target)) {
			this.target = null;
		}
		//TODO 猜测特性：如果一个帮助过怪物的英雄玩家站得比另一位没帮助过的玩家近，那么怪物也不会索敌该玩家，除非英雄离开。
	}
}
