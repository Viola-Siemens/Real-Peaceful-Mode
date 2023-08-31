package com.hexagram2021.real_peaceful_mode.client.models;

import com.hexagram2021.real_peaceful_mode.common.entity.boss.ZombieTyrant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
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

	public static MeshDefinition createMesh(CubeDeformation expand) {
		MeshDefinition mesh = HumanoidModel.createMesh(expand, 0.0F);
		PartDefinition root = mesh.getRoot();
		root.getChild("hat").addOrReplaceChild("crown", CubeListBuilder.create().texOffs(32, 32).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, expand.extend(1.0F)), PartPose.ZERO);
		root.getChild("body").addOrReplaceChild(
				"body_armor", CubeListBuilder.create()
						.texOffs(16, 25).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.5F))
						.texOffs(32, 45).addBox(-5.0F, -10.0F, -2.5F, 10.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(0, 42).addBox(-4.0F, 2.5F, -2.5F, 8.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.ZERO
		);
		root.getChild("right_arm").addOrReplaceChild(
				"right_shoulder", CubeListBuilder.create()
						.texOffs(0, 32).addBox(-3.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.ZERO
		);
		root.getChild("left_arm").addOrReplaceChild(
				"left_shoulder", CubeListBuilder.create()
						.texOffs(0, 32).mirror().addBox(-1.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.ZERO
		);

		return mesh;
	}

	public static LayerDefinition createBodyLayer() {
		return LayerDefinition.create(createMesh(CubeDeformation.NONE), 64, 64);
	}

	private void animateArms(int spelling, float tick) {
		float xRot = (Mth.cos(spelling * Mth.PI / 20.0F) - 1.0F) * 0.55F;
		float noise = Mth.sin(tick * Mth.PI / 40.0F) * 0.05F;
		this.leftArm.xRot = -Mth.HALF_PI + xRot + noise;
		this.rightArm.xRot = -Mth.HALF_PI + xRot - noise;
	}
}
