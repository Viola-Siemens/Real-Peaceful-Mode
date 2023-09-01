package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.common.entity.misc.TinyFireballEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TinyFireballRenderer extends ThrownItemRenderer<TinyFireballEntity> {
	public TinyFireballRenderer(EntityRendererProvider.Context context) {
		super(context, 0.5F, true);
	}
}
