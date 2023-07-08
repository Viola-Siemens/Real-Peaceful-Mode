package com.hexagram2021.real_peaceful_mode.client.models;

import com.hexagram2021.real_peaceful_mode.common.entity.boss.ZombieTyrant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieTyrantModel<T extends ZombieTyrant> extends HumanoidModel<T> {
	public ZombieTyrantModel(ModelPart root) {
		super(root);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.animateArms(entity.getSpelling(), ageInTicks);
	}

	public static LayerDefinition createBodyLayer() {
		// TODO
		return LayerDefinition.create(createMesh(CubeDeformation.NONE, 0.0F), 64, 64);
	}

	private void animateArms(int spelling, float tick) {
		float xRot = (Mth.cos(spelling * Mth.PI / 20.0F) - 1.0F) * 0.55F;
		float noise = Mth.sin(tick * Mth.PI / 40.0F) * 0.05F;
		this.leftArm.xRot = -Mth.HALF_PI + xRot + noise;
		this.rightArm.xRot = -Mth.HALF_PI + xRot - noise;
	}
}
