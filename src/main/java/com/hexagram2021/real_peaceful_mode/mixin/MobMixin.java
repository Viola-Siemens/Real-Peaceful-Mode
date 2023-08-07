package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {
	@Inject(method = "handleEntityEvent", at = @At(value = "HEAD"))
	public void addFriendlyMonsterEventHandler(byte event, CallbackInfo ci) {
		if(this instanceof IFriendlyMonster friendlyMonster) {
			if(event == EntityEvent.VILLAGER_SWEAT) {
				friendlyMonster.addLessParticlesAroundSelf(ParticleTypes.SPLASH);
			}
		}
	}
}
