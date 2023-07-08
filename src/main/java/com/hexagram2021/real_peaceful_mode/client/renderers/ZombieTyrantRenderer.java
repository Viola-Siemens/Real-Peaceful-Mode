package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.client.models.ZombieTyrantModel;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.ZombieTyrant;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@OnlyIn(Dist.CLIENT)
public class ZombieTyrantRenderer extends HumanoidMobRenderer<ZombieTyrant, ZombieTyrantModel<ZombieTyrant>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/zombie_tyrant.png");

	public ZombieTyrantRenderer(EntityRendererProvider.Context context) {
		this(context, RPMModelLayers.ZOMBIE_TYRANT);
	}

	public ZombieTyrantRenderer(EntityRendererProvider.Context context, ModelLayerLocation model) {
		this(context, new ZombieTyrantModel<>(context.bakeLayer(model)));
	}

	public ZombieTyrantRenderer(EntityRendererProvider.Context context, ZombieTyrantModel<ZombieTyrant> model) {
		super(context, model, 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(ZombieTyrant entity) {
		return TEXTURE_LOCATION;
	}
}
