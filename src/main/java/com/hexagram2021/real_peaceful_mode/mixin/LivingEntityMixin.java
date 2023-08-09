package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.crafting.menu.MissionMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "handleEntityEvent", at = @At(value = "HEAD"), cancellable = true)
	public void addFriendlyMonsterEventHandler(byte event, CallbackInfo ci) {
		if(this instanceof IFriendlyMonster) {
			if(event == EntityEvent.VILLAGER_SWEAT) {
				IFriendlyMonster.addLessParticlesAroundSelf((LivingEntity)(Object)this, ParticleTypes.SPLASH);
				IFriendlyMonster.addLessParticlesAroundSelf((LivingEntity)(Object)this, ParticleTypes.ANGRY_VILLAGER);
				ci.cancel();
			}
		}
	}

	@Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "HEAD"), cancellable = true)
	public void ignoreHeroOrPlayerWithMenu(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
		if(this instanceof IFriendlyMonster friendlyMonster) {
			if(friendlyMonster.preventAttack(livingEntity)) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
		if(livingEntity instanceof ServerPlayer player) {
			if(((IMonsterHero)player).isHero(((LivingEntity)(Object)this).getType())) {
				cir.setReturnValue(false);
				cir.cancel();
			}
			if(player.containerMenu instanceof MissionMessageMenu) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
	}
}
