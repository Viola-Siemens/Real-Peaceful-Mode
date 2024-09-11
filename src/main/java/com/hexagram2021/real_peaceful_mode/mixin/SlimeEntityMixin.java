package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slime.class)
public class SlimeEntityMixin {
	@Inject(method = "dealDamage", at = @At(value = "HEAD"), cancellable = true)
	protected void rpm$ignoreDealDamageToHero(LivingEntity target, CallbackInfo ci) {
		if(target instanceof ServerPlayer player) {
			if(((IMonsterHero)player).rpm$isHero(((LivingEntity)(Object)this).getType())) {
				ci.cancel();
			}
		}
	}
}
