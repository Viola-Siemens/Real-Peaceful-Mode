package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.client.map.MapCustomIcons;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(targets = "net.minecraft.client.gui.MapRenderer$MapInstance")
public class MapInstanceMixin {
	@Unique @Nullable
	private RenderType rpm$bufferDecorationRenderType = null;

	@WrapOperation(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/saveddata/maps/MapDecoration;getImage()B"))
	private byte getImageAndUpdateBuffer(MapDecoration instance, Operation<Byte> original) {
		MapDecoration.Type type = instance.getType();
		this.rpm$bufferDecorationRenderType = MapCustomIcons.RENDER_TYPES.get(type);
		return MapCustomIcons.ORDINARIES.getOrDefault(type, original.call(instance));
	}

	@WrapOperation(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;", ordinal = 1))
	private VertexConsumer getVertexConsumerForCustomIcons(MultiBufferSource instance, RenderType renderType, Operation<VertexConsumer> original) {
		return original.call(instance, Objects.requireNonNullElse(this.rpm$bufferDecorationRenderType, renderType));
	}
}
