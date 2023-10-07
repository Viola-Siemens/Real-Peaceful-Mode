package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.IRightArmDetachable;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonModel.class)
public abstract class SkeletonModelMixin<T extends Mob & RangedAttackMob> {
	@SuppressWarnings("unchecked")
	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/Mob;FFFFF)V", at = @At(value = "TAIL"))
	public void setRPMDanceAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		SkeletonModel<T> current = (SkeletonModel<T>) (Object) this;
		if(entity instanceof IFriendlyMonster monster && monster.isDancing()) {
			float xRot = -0.4F;
			float noise = Mth.sin(ageInTicks * Mth.PI / 40.0F) * 0.05F;
			current.leftArm.xRot = -Mth.HALF_PI + xRot + noise;
			current.rightArm.xRot = -Mth.HALF_PI + xRot - noise;
		}
		if(entity instanceof IRightArmDetachable rightArmDetachable) {
			current.rightArm.skipDraw = rightArmDetachable.isRightArmDetached();
		}
	}
}
