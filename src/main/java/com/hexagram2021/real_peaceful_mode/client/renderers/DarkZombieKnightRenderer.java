package com.hexagram2021.real_peaceful_mode.client.renderers;

import com.hexagram2021.real_peaceful_mode.client.RPMModelLayers;
import com.hexagram2021.real_peaceful_mode.client.models.DarkZombieKnightModel;
import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@OnlyIn(Dist.CLIENT)
public class DarkZombieKnightRenderer extends HumanoidMobRenderer<DarkZombieKnight, DarkZombieKnightModel<DarkZombieKnight>> {
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/entity/dark_zombie_knight.png");

	public DarkZombieKnightRenderer(EntityRendererProvider.Context context) {
		this(context, RPMModelLayers.DARK_ZOMBIE_KNIGHT, RPMModelLayers.DARK_ZOMBIE_KNIGHT_INNER_ARMOR, RPMModelLayers.DARK_ZOMBIE_KNIGHT_OUTER_ARMOR);
	}

	public DarkZombieKnightRenderer(EntityRendererProvider.Context context, ModelLayerLocation model, ModelLayerLocation inner, ModelLayerLocation outer) {
		this(context, new DarkZombieKnightModel<>(context.bakeLayer(model)), new DarkZombieKnightModel<>(context.bakeLayer(inner)), new DarkZombieKnightModel<>(context.bakeLayer(outer)));
	}

	public DarkZombieKnightRenderer(EntityRendererProvider.Context context, DarkZombieKnightModel<DarkZombieKnight> model,
									DarkZombieKnightModel<DarkZombieKnight> inner, DarkZombieKnightModel<DarkZombieKnight> outer) {
		super(context, model, 0.5F);
		this.addLayer(new HumanoidArmorLayer<>(this, inner, outer, context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(DarkZombieKnight entity) {
		return TEXTURE_LOCATION;
	}
}
