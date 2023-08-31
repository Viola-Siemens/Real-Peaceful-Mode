package com.hexagram2021.real_peaceful_mode.client.models;

import com.hexagram2021.real_peaceful_mode.common.entity.boss.SkeletonKing;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class SkeletonKingModel<T extends SkeletonKing> extends HumanoidModel<T> {
	public SkeletonKingModel(ModelPart root) {
		super(root);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.animateArms(entity.isAggressive(), ageInTicks);
	}

	public static MeshDefinition createMesh(CubeDeformation expand) {
		MeshDefinition mesh = HumanoidModel.createMesh(expand, 0.0F);
		PartDefinition root = mesh.getRoot();
		root.getChild("hat").addOrReplaceChild("crown", CubeListBuilder.create().texOffs(24, 0).addBox(-7.0F, -10.0F, -4.75F, 14.0F, 5.0F, 0.0F), PartPose.ZERO);
		root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
		root.getChild("body").addOrReplaceChild("body_armor", CubeListBuilder.create()
				.texOffs(0, 32).addBox(-4.0F, 0.0F, -2.5F, 8.0F, 16.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
		root.getChild("right_arm").addOrReplaceChild("right_decor", CubeListBuilder.create()
				.texOffs(36, 32).addBox(-3.0F, -4.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-1.0F)), PartPose.ZERO);
		root.getChild("left_arm").addOrReplaceChild("left_decor", CubeListBuilder.create()
				.texOffs(36, 32).mirror().addBox(-2.0F, -4.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(-1.0F)), PartPose.ZERO);
		return mesh;
	}

	public static LayerDefinition createBodyLayer() {
		return LayerDefinition.create(createMesh(CubeDeformation.NONE), 64, 64);
	}

	private void animateArms(boolean isAttacking, float tick) {
		if(isAttacking) {
			float noise = Mth.sin(tick * Mth.PI / 40.0F) * 0.05F;
			this.leftArm.xRot = -Mth.HALF_PI * 1.35F + noise;
			this.rightArm.xRot = -Mth.HALF_PI * 1.35F - noise;
		}
	}
}
