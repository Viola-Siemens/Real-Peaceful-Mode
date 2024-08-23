package com.hexagram2021.real_peaceful_mode.client.models;

import com.hexagram2021.real_peaceful_mode.common.entity.GuardSlime;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuardSlimeModel<T extends GuardSlime> extends HierarchicalModel<T> {
	private final ModelPart root;
	private final ModelPart armor;

	public GuardSlimeModel(ModelPart root) {
		this.root = root;
		this.armor = root.getChild("armor");
	}

	public static LayerDefinition createOuterBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition root = meshDefinition.getRoot();
		root.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
		root.addOrReplaceChild("armor", CubeListBuilder.create().texOffs(32, 16).addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.125F)), PartPose.ZERO);
		return LayerDefinition.create(meshDefinition, 64, 32);
	}

	public static LayerDefinition createInnerBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition root = meshDefinition.getRoot();
		root.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);
		root.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(32, 0).addBox(-3.25F, 19.0F, -3.5F, 2.0F, 2.0F, 2.0F), PartPose.ZERO);
		root.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(32, 4).addBox(1.25F, 19.0F, -3.5F, 2.0F, 2.0F, 2.0F), PartPose.ZERO);
		root.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(32, 8).addBox(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F), PartPose.ZERO);
		return LayerDefinition.create(meshDefinition, 64, 32);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.armor.skipDraw = entity.hasArmor();
	}

	public ModelPart root() {
		return this.root;
	}
}
