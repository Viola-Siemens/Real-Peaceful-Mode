package com.hexagram2021.real_peaceful_mode.client.models;

import com.hexagram2021.real_peaceful_mode.common.entity.boss.HuskPharaoh;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class HuskPharaohModel<T extends HuskPharaoh> extends HumanoidModel<T> {
	public HuskPharaohModel(ModelPart root) {
		super(root);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		if(entity.isStone()) {
			this.leftArm.xRot = -Mth.HALF_PI * 1.05F;
			this.leftArm.yRot = 0.0F;
			this.leftArm.zRot = 0.0F;
			this.rightArm.xRot = -Mth.HALF_PI * 1.45F;
			this.rightArm.yRot = 0.0F;
			this.rightArm.zRot = 0.0F;
		} else {
			this.animateArms(entity.isAggressive(), ageInTicks);
		}
	}

	private void animateArms(boolean isAttacking, float tick) {
		if(isAttacking) {
			float noise = Mth.sin(tick * Mth.PI / 40.0F) * 0.05F;
			this.leftArm.xRot = -Mth.HALF_PI * 1.35F + noise;
			this.rightArm.xRot = -Mth.HALF_PI * 1.35F - noise;
		} else {
			AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, false, this.attackTime, tick);
		}
	}
}
