package com.hexagram2021.real_peaceful_mode.client.models;

import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DarkZombieKnightModel<T extends DarkZombieKnight> extends HumanoidModel<T> {
	public DarkZombieKnightModel(ModelPart root) {
		super(root);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, entity.isAggressive(), this.attackTime, ageInTicks);
	}

	public static LayerDefinition createBodyLayer() {
		return LayerDefinition.create(createMesh(CubeDeformation.NONE, 0.0F), 64, 64);
	}

	public static LayerDefinition createArmorLayer(float expand) {
		return LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(expand)), 64, 32);
	}
}
