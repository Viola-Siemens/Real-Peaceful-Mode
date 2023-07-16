package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.common.entity.PinkCreeperEntity;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class PinkCreeperRenderer extends MobRenderer<PinkCreeperEntity, CreeperModel<PinkCreeperEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/pink_creeper.png");

	public PinkCreeperRenderer(EntityRendererProvider.Context context) {
		super(context, new CreeperModel<>(context.bakeLayer(RPMModelLayers.PINK_CREEPER)), 0.5F);
	}

	public ResourceLocation getTextureLocation(PinkCreeperEntity entity) {
		return TEXTURE_LOCATION;
	}
}
