package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.FlameEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@OnlyIn(Dist.CLIENT)
public class FlameCrystalRenderer extends EntityRenderer<FlameEntity> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/flame_crystal.png");

	private final ModelPart tinyFlames;
	private final ModelPart mainFlame;

	public FlameCrystalRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.25F;
		ModelPart root = context.bakeLayer(RPMModelLayers.FLAME_CRYSTAL);
		this.mainFlame = root.getChild("MainFlame");
		this.tinyFlames = root.getChild("TinyFlames");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("MainFlame", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
		PartDefinition tinyFlames = partdefinition.addOrReplaceChild("TinyFlames", CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
		tinyFlames.addOrReplaceChild("Moons", CubeListBuilder.create()
						.texOffs(0, 0).addBox(-10.0F, -10.0F, -10.0F, 4.0F, 4.0F, 4.0F)
						.texOffs(0, 0).addBox(-10.0F, -10.0F, 6.0F, 4.0F, 4.0F, 4.0F)
						.texOffs(0, 0).addBox(-10.0F, 6.0F, -10.0F, 4.0F, 4.0F, 4.0F)
						.texOffs(0, 0).addBox(-10.0F, 6.0F, 6.0F, 4.0F, 4.0F, 4.0F)
						.texOffs(0, 0).addBox(6.0F, -10.0F, -10.0F, 4.0F, 4.0F, 4.0F)
						.texOffs(0, 0).addBox(6.0F, -10.0F, 6.0F, 4.0F, 4.0F, 4.0F)
						.texOffs(0, 0).addBox(6.0F, 6.0F, -10.0F, 4.0F, 4.0F, 4.0F)
						.texOffs(0, 0).addBox(6.0F, 6.0F, 6.0F, 4.0F, 4.0F, 4.0F),
				PartPose.ZERO);
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public static float getY(float tick) {
		float f = Mth.sin(tick * Mth.PI / 8.0F) / 2.0F + 0.5F;
		return (f * f + f) * 0.4F - 1.4F;
	}

	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);
	private static final float SIN_45 = (float)Math.sin((Math.PI / 4D));

	@Override
	public void render(FlameEntity entity, float yRot, float tick, PoseStack transform, MultiBufferSource bufferSource, int uv2) {
		transform.pushPose();
		float y = getY(tick);
		float rotation = tick * 4.0F;
		VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
		transform.translate(0.0F, 0.5F + y / 2.0F, 0.0F);
		BlockPos beamTarget = entity.getBeamTarget();
		if(beamTarget != null) {
			Vec3 vec = beamTarget.getCenter().subtract(entity.position());
			if(vec.length() > 1.0D) {
				transform.mulPose(Axis.YP.rotationDegrees(((float)Mth.atan2(vec.x, vec.z)) * 180.0F / Mth.PI));
			}
		}
		this.mainFlame.render(transform, vertexconsumer, uv2, OverlayTexture.NO_OVERLAY);
		transform.scale(1.2F, 1.2F, 1.2F);
		transform.mulPose((new Quaternionf()).setAngleAxis(Mth.PI * entity.tickCount / 90.0F, SIN_45, 0.0F, SIN_45));
		transform.mulPose(Axis.YP.rotationDegrees(rotation));
		this.tinyFlames.render(transform, vertexconsumer, uv2, OverlayTexture.NO_OVERLAY);
		transform.popPose();

		super.render(entity, yRot, tick, transform, bufferSource, uv2);
	}

	public ResourceLocation getTextureLocation(FlameEntity entity) {
		return TEXTURE_LOCATION;
	}
}
