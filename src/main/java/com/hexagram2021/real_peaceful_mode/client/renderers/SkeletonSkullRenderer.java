package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.SkeletonSkullEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("deprecation")
public class SkeletonSkullRenderer extends EntityRenderer<SkeletonSkullEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private final SkullModel model;

    public SkeletonSkullRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SkullModel(context.bakeLayer(RPMModelLayers.SKELETON_SKULL));
    }

    public static LayerDefinition createSkullLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    protected int getBlockLightLevel(SkeletonSkullEntity entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(SkeletonSkullEntity entity, float f, float partialTick, PoseStack transform, MultiBufferSource multiBufferSource, int uv2) {
        transform.pushPose();
        transform.scale(-1.0F, -1.0F, 1.0F);
        float yRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.setupAnim(0.0F, yRot, xRot);
        this.model.renderToBuffer(transform, vertexconsumer, uv2, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        transform.popPose();
        super.render(entity, f, partialTick, transform, multiBufferSource, uv2);
        if(entity.isOnFire()) {
            renderSoulFlame(this.entityRenderDispatcher, transform, multiBufferSource, entity);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonSkullEntity entity) {
        return TEXTURE_LOCATION;
    }

    private static final Material SOUL_FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/soul_fire_0"));
    private static final Material SOUL_FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/soul_fire_1"));
    private static void renderSoulFlame(EntityRenderDispatcher dispatcher, PoseStack transform, MultiBufferSource bufferSource, Entity entity) {
        TextureAtlasSprite sprite1 = SOUL_FIRE_0.sprite();
        TextureAtlasSprite sprite2 = SOUL_FIRE_1.sprite();
        transform.pushPose();
        float scale = entity.getBbWidth() * 1.8F;
        transform.scale(scale, scale, scale);
        float x = 0.5F;
        float deep = entity.getBbHeight() / scale;
        float y = 0.0F;
        transform.mulPose(Axis.YP.rotationDegrees(-dispatcher.camera.getYRot()));
        transform.translate(0.0F, 0.0F, -0.3F + (float)((int)deep) * 0.02F);
        float z = 0.0F;
        int i = 0;
        VertexConsumer vertexconsumer = bufferSource.getBuffer(Sheets.cutoutBlockSheet());

        for(PoseStack.Pose pose = transform.last(); deep > 0.0F; ++i) {
            TextureAtlasSprite renderSprite = (i & 1) == 0 ? sprite1 : sprite2;
            float u0 = renderSprite.getU0();
            float v0 = renderSprite.getV0();
            float u1 = renderSprite.getU1();
            float v1 = renderSprite.getV1();
            if (i / 2 % 2 == 0) {
                float f10 = u1;
                u1 = u0;
                u0 = f10;
            }

            fireVertex(pose, vertexconsumer, x - 0.0F, 0.0F - y, z, u1, v1);
            fireVertex(pose, vertexconsumer, -x - 0.0F, 0.0F - y, z, u0, v1);
            fireVertex(pose, vertexconsumer, -x - 0.0F, 1.4F - y, z, u0, v0);
            fireVertex(pose, vertexconsumer, x - 0.0F, 1.4F - y, z, u1, v0);
            deep -= 0.45F;
            y -= 0.45F;
            x *= 0.9F;
            z += 0.03F;
        }

        transform.popPose();
    }

    private static void fireVertex(PoseStack.Pose pose, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v) {
        vertexConsumer.vertex(pose.pose(), x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(0, 10).uv2(240).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }
}
