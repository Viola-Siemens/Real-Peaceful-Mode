package com.hexagram2021.real_peaceful_mode.client.models;

import com.hexagram2021.real_peaceful_mode.common.entity.boss.SkeletonKing;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;

public class SkeletonKingModel<T extends SkeletonKing> extends HumanoidModel<T> {
	public SkeletonKingModel(ModelPart root) {
		super(root);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.animateArms(entity.getTarget() != null, ageInTicks);
	}

	public static LayerDefinition createBodyLayer() {
		// TODO
		return LayerDefinition.create(createMesh(CubeDeformation.NONE, 0.0F), 64, 64);
	}

	private void animateArms(boolean isAttacking, float tick) {
		if(isAttacking) {
			float noise = Mth.sin(tick * Mth.PI / 40.0F) * 0.05F;
			this.leftArm.xRot = -Mth.HALF_PI * 1.35F;
			this.rightArm.xRot = -Mth.HALF_PI * 1.35F;
		}
	}
}
