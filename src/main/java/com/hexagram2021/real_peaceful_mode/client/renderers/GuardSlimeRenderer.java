package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.client.models.GuardSlimeModel;
import com.hexagram2021.real_peaceful_mode.client.renderers.layers.GuardSlimeOuterLayer;
import com.hexagram2021.real_peaceful_mode.common.entity.GuardSlimeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class GuardSlimeRenderer extends MobRenderer<GuardSlimeEntity, GuardSlimeModel<GuardSlimeEntity>> {
	private static final ResourceLocation GUARD_SLIME_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/guard_slime.png");

	public GuardSlimeRenderer(EntityRendererProvider.Context context) {
		super(context, new GuardSlimeModel<>(context.bakeLayer(RPMModelLayers.GUARD_SLIME)), 0.25F);
		this.addLayer(new GuardSlimeOuterLayer<>(this, context.getModelSet()));
	}

	@Override
	public void render(GuardSlimeEntity entity, float y, float ticks, PoseStack transform, MultiBufferSource bufferSource, int h) {
		this.shadowRadius = 0.25F * (float)entity.getSize();
		super.render(entity, y, ticks, transform, bufferSource, h);
	}

	@Override
	protected void scale(GuardSlimeEntity entity, PoseStack transform, float partialTick) {
		transform.scale(0.999F, 0.999F, 0.999F);
		transform.translate(0.0F, 0.001F, 0.0F);
		float f1 = (float)entity.getSize();
		float f2 = Mth.lerp(partialTick, entity.oSquish, entity.squish) / (f1 * 0.5F + 1.0F);
		float f3 = 1.0F / (f2 + 1.0F);
		transform.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
	}

	@Override
	public ResourceLocation getTextureLocation(GuardSlimeEntity entity) {
		return GUARD_SLIME_LOCATION;
	}
}
