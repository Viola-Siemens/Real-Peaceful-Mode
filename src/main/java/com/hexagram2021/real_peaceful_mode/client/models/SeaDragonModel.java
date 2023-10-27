package com.hexagram2021.real_peaceful_mode.client.models;

import com.hexagram2021.real_peaceful_mode.common.entity.boss.SeaDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SeaDragonModel<T extends SeaDragonEntity> extends EntityModel<T> {

	private final ModelPart neck;
	private final ModelPart body;
	private final ModelPart rearLegRight;
	private final ModelPart rearLegLeft;
	private final ModelPart frontLegRight;
	private final ModelPart frontLegLeft;
	private final ModelPart tail;

	public SeaDragonModel(ModelPart root) {
		this.neck = root.getChild("neck");
		this.body = root.getChild("body");
		this.rearLegRight = root.getChild("RearLegLeft");
		this.rearLegLeft = root.getChild("RearLegLeft");
		this.frontLegRight = root.getChild("FrontLegRight");
		this.frontLegLeft = root.getChild("FrontLegLeft");
		this.tail = root.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();

		root.addOrReplaceChild(
				"neck",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -3.0F, -12.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(2.0F))
						.texOffs(48, 0).addBox(-1.0F, -9.0F, -10.0F, 2.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -11.0F, -24.0F)
		).addOrReplaceChild(
				"neck2",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, -14.0F)
		).addOrReplaceChild(
				"neck3",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -5.0F, -10.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-1.0F, -9.0F, -8.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, -10.0F)
		).addOrReplaceChild(
				"head",
				CubeListBuilder.create()
						.texOffs(64, 0).addBox(-6.0F, -1.0F, -30.0F, 12.0F, 5.0F, 16.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-8.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
						.texOffs(0, 32).mirror().addBox(-5.0F, -16.0F, -10.0F, 2.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(0, 32).addBox(3.0F, -16.0F, -10.0F, 2.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
						.texOffs(60, 0).mirror().addBox(-5.0F, -3.0F, -28.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(60, 0).addBox(3.0F, -3.0F, -28.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 1.0F, -10.0F)
		).addOrReplaceChild(
				"jaw",
				CubeListBuilder.create().texOffs(120, 0)
						.addBox(-6.0F, 0.0F, -17.0F, 12.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 4.0F, -13.0F, 0.2618F, 0.0F, 0.0F)
		);

		root.addOrReplaceChild(
				"body",
				CubeListBuilder.create()
						.texOffs(0, 32).addBox(-8.0F, -20.0F, -32.0F, 16.0F, 16.0F, 48.0F, new CubeDeformation(0.0F))
						.texOffs(48, 0).addBox(-1.0F, -24.0F, -28.0F, 2.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
						.texOffs(48, 0).addBox(-1.0F, -24.0F, -12.0F, 2.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
						.texOffs(48, 0).addBox(-1.0F, -24.0F, 4.0F, 2.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 4.0F, 8.0F)
		);

		root.addOrReplaceChild(
				"RearLegRight",
				CubeListBuilder.create()
						.texOffs(0, 46).addBox(-16.0F, -10.0F, -30.0F, 10.0F, 20.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-6.0F, 2.0F, 42.0F, 0.0F, 0.0F, 0.5236F)
		).addOrReplaceChild(
				"RearLegTipRight",
				CubeListBuilder.create()
						.texOffs(80, 41).addBox(-4.0F, -24.0F, -29.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 28.0F, 1.0F, 0.0F, 0.0F, -0.5236F)
		).addOrReplaceChild(
				"RearFootRight",
				CubeListBuilder.create()
						.texOffs(120, 20).addBox(-6.0F, -20.0F, -35.0F, 10.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 12.0F, -2.0F)
		);

		root.addOrReplaceChild(
				"RearLegLeft",
				CubeListBuilder.create()
						.texOffs(0, 46).mirror().addBox(8.0F, -10.0F, -30.0F, 10.0F, 20.0F, 10.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(4.0F, 2.0F, 42.0F, 0.0F, 0.0F, -0.5236F)
		).addOrReplaceChild(
				"RearLegTipLeft",
				CubeListBuilder.create()
						.texOffs(80, 41).mirror().addBox(-2.0F, -24.0F, -29.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 28.0F, 1.0F, 0.0F, 0.0F, 0.5236F)
		).addOrReplaceChild(
				"RearFootLeft",
				CubeListBuilder.create()
						.texOffs(120, 20).mirror().addBox(-4.0F, -40.0F, -35.0F, 10.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 32.0F, -2.0F)
		);

		root.addOrReplaceChild(
				"FrontLegRight",
				CubeListBuilder.create()
						.texOffs(0, 96).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-12.0F, -7.0F, -14.0F, 0.0F, 0.0F, 0.5236F)
		).addOrReplaceChild("FrontLegTipRight",
				CubeListBuilder.create()
						.texOffs(80, 41).addBox(1.0F, -8.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 0.0F, 0.0F, -0.5236F)
		).addOrReplaceChild(
				"FrontFootRight",
				CubeListBuilder.create().texOffs(32, 96).addBox(0.0F, -14.0F, -10.0F, 8.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 22.0F, 0.0F)
		);

		root.addOrReplaceChild(
				"FrontLegLeft",
				CubeListBuilder.create()
						.texOffs(0, 96).mirror().addBox(-4.0F, -4.0F, -4.0F, 8.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(12.0F, -7.0F, -14.0F, 0.0F, 0.0F, -0.5236F)
		).addOrReplaceChild(
				"FrontLegTipLeft",
				CubeListBuilder.create()
						.texOffs(80, 41).mirror().addBox(-7.0F, -8.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.5236F)
		).addOrReplaceChild(
				"FrontFootLeft",
				CubeListBuilder.create().texOffs(32, 96).mirror().addBox(-8.0F, -14.0F, -10.0F, 8.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 22.0F, 0.0F)
		);

		root.addOrReplaceChild(
				"tail",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -11.0F, 24.0F)
		).addOrReplaceChild(
				"tail2",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 10.0F)
		).addOrReplaceChild(
				"tail3",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 10.0F)
		).addOrReplaceChild(
				"tail4",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 10.0F)
		).addOrReplaceChild(
				"tail5",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 10.0F)
		).addOrReplaceChild(
				"tail6",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -6.0F, -1.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-1.0F))
						.texOffs(0, 0).addBox(-1.0F, -9.0F, 2.0F, 2.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, 10.0F)
		).addOrReplaceChild(
				"tail7",
				CubeListBuilder.create()
						.texOffs(80, 21).addBox(-5.0F, -7.0F, -4.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-2.0F)),
				PartPose.offset(0.0F, 0.0F, 10.0F)
		);

		return LayerDefinition.create(meshdefinition, 256, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack transform, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
		this.neck.render(transform, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
		this.body.render(transform, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
		this.rearLegRight.render(transform, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
		this.rearLegLeft.render(transform, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
		this.frontLegRight.render(transform, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
		this.frontLegLeft.render(transform, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
		this.tail.render(transform, vertexConsumer, packedLight, packedOverlay, r, g, b, a);
	}
}