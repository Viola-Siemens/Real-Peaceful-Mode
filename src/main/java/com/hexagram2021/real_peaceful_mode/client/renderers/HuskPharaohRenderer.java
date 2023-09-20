package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.client.models.HuskPharaohModel;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.HuskPharaoh;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class HuskPharaohRenderer extends HumanoidMobRenderer<HuskPharaoh, HuskPharaohModel<HuskPharaoh>> {
	private static final ResourceLocation STONE_TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/stone_husk_pharaoh.png");
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/husk_pharaoh.png");

	public HuskPharaohRenderer(EntityRendererProvider.Context context) {
		this(context, RPMModelLayers.HUSK_PHARAOH);
	}

	public HuskPharaohRenderer(EntityRendererProvider.Context context, ModelLayerLocation model) {
		this(context, new HuskPharaohModel<>(context.bakeLayer(model)));
	}

	public HuskPharaohRenderer(EntityRendererProvider.Context context, HuskPharaohModel<HuskPharaoh> model) {
		super(context, model, 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(HuskPharaoh entity) {
		return entity.isStone() ? STONE_TEXTURE_LOCATION : TEXTURE_LOCATION;
	}
}
