package com.hexagram2021.real_peaceful_mode.client.models;

import com.hexagram2021.real_peaceful_mode.common.entity.HuskWorkmanEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public class HuskWorkmanModel<T extends HuskWorkmanEntity> extends HumanoidModel<T> {
	public HuskWorkmanModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, false, this.attackTime, ageInTicks);
	}
}
