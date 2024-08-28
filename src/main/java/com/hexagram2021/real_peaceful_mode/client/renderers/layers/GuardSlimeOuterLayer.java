package com.hexagram2021.real_peaceful_mode.client.renderers.layers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.client.models.GuardSlimeModel;
import com.hexagram2021.real_peaceful_mode.common.entity.GuardSlimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class GuardSlimeOuterLayer<T extends GuardSlimeEntity> extends RenderLayer<T, GuardSlimeModel<T>> {
	private final GuardSlimeModel<T> model;

	public GuardSlimeOuterLayer(RenderLayerParent<T, GuardSlimeModel<T>> parent, EntityModelSet modelSet) {
		super(parent);
		this.model = new GuardSlimeModel<>(modelSet.bakeLayer(RPMModelLayers.GUARD_SLIME_OUTER));
	}

	@Override
	public void render(PoseStack transform, MultiBufferSource bufferSource, int color, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		Minecraft minecraft = Minecraft.getInstance();
		boolean glowing = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
		if (!entity.isInvisible() || glowing) {
			VertexConsumer vertexconsumer;
			if (glowing) {
				vertexconsumer = bufferSource.getBuffer(RenderType.outline(this.getTextureLocation(entity)));
			} else {
				vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
			}

			this.getParentModel().copyPropertiesTo(this.model);
			this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
			this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			this.model.renderToBuffer(transform, vertexconsumer, color, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
