package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.client.models.SeaDragonModel;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.SeaDragonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class SeaDragonRenderer extends MobRenderer<SeaDragonEntity, SeaDragonModel<SeaDragonEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/sea_dragon.png");

	public SeaDragonRenderer(EntityRendererProvider.Context context) {
		super(context, new SeaDragonModel<>(context.bakeLayer(RPMModelLayers.SEA_DRAGON)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(SeaDragonEntity entity) {
		return TEXTURE_LOCATION;
	}
}
