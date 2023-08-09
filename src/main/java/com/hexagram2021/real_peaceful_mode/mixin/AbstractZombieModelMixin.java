package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractZombieModel.class)
public abstract class AbstractZombieModelMixin<T extends Monster> {
	@SuppressWarnings("unchecked")
	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/monster/Monster;FFFFF)V", at = @At(value = "TAIL"))
	public void setRPMDanceAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		if(entity instanceof IFriendlyMonster monster && monster.isDancing()) {
			AbstractZombieModel<T> current = (AbstractZombieModel<T>)(Object)this;
			float xRot = -0.4F;
			float noise = Mth.sin(ageInTicks * Mth.PI / 40.0F) * 0.05F;
			current.leftArm.xRot = -Mth.HALF_PI + xRot + noise;
			current.rightArm.xRot = -Mth.HALF_PI + xRot - noise;
		}
	}
}
