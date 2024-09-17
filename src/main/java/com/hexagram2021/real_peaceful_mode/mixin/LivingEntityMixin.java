package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.crafting.menu.IMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@SuppressWarnings("DataFlowIssue")
	@Inject(method = "handleEntityEvent", at = @At(value = "HEAD"), cancellable = true)
	public void rpm$addFriendlyMonsterEventHandler(byte event, CallbackInfo ci) {
		if(this instanceof IFriendlyMonster) {
			if(event == EntityEvent.VILLAGER_SWEAT) {
				IFriendlyMonster.addLessParticlesAroundSelf((LivingEntity)(Object)this, ParticleTypes.SPLASH);
				IFriendlyMonster.addLessParticlesAroundSelf((LivingEntity)(Object)this, ParticleTypes.ANGRY_VILLAGER);
				ci.cancel();
			}
		}
	}

	@Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At(value = "HEAD"), cancellable = true)
	public void rpm$ignoreHeroOrPlayerWithMenu(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
		if(this instanceof IFriendlyMonster friendlyMonster) {
			if(friendlyMonster.rpm$preventAttack(livingEntity)) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
		if(livingEntity instanceof ServerPlayer player) {
			if(((IMonsterHero)player).rpm$isHero(((LivingEntity)(Object)this).getType())) {
				cir.setReturnValue(false);
				cir.cancel();
			}
			if(player.containerMenu instanceof IMessageMenu) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
	}

	@SuppressWarnings("ConstantValue")
	@Inject(method = "tick", at = @At(value = "HEAD"))
	public void rpm$tickExtra(CallbackInfo ci) {
		if ((Object)this instanceof Mob mob && (Object)this instanceof IFriendlyMonster monster) {
			Consumer<Mob> tickAction = monster.rpm$getNpcExtraTickAction();
			if(tickAction != null) {
				tickAction.accept(mob);
			}
		}
	}
}
