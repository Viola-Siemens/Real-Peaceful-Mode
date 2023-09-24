package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.client.models.HuskWorkmanModel;
import com.hexagram2021.real_peaceful_mode.common.entity.HuskWorkmanEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class HuskWorkmanRenderer extends MobRenderer<HuskWorkmanEntity, HuskWorkmanModel<HuskWorkmanEntity>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/husk_workman.png");

	public HuskWorkmanRenderer(EntityRendererProvider.Context context) {
		super(context, new HuskWorkmanModel<>(context.bakeLayer(RPMModelLayers.HUSK_WORKMAN)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(HuskWorkmanEntity entity) {
		return TEXTURE_LOCATION;
	}
}
