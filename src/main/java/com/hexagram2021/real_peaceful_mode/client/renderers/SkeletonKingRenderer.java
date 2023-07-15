package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.client.models.SkeletonKingModel;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.SkeletonKing;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@OnlyIn(Dist.CLIENT)
public class SkeletonKingRenderer extends HumanoidMobRenderer<SkeletonKing, SkeletonKingModel<SkeletonKing>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/skeleton_king.png");

	public SkeletonKingRenderer(EntityRendererProvider.Context context) {
		this(context, RPMModelLayers.SKELETON_KING);
	}

	public SkeletonKingRenderer(EntityRendererProvider.Context context, ModelLayerLocation model) {
		this(context, new SkeletonKingModel<>(context.bakeLayer(model)));
	}

	public SkeletonKingRenderer(EntityRendererProvider.Context context, SkeletonKingModel<SkeletonKing> model) {
		super(context, model, 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(SkeletonKing entity) {
		return TEXTURE_LOCATION;
	}
}
