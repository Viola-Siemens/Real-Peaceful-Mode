package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.block.skull.RPMSkullTypes;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@Mixin(SkullBlockRenderer.class)
public class SkullBlockRendererMixin {
	@Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/blockentity/SkullBlockRenderer;SKIN_BY_TYPE:Ljava/util/Map;", shift = At.Shift.AFTER))
	private static void addRPMSkulls(CallbackInfo ci) {
		SkullBlockRenderer.SKIN_BY_TYPE.put(RPMSkullTypes.DARK_ZOMBIE_KNIGHT, new ResourceLocation(MODID, "textures/entity/dark_zombie_knight.png"));
	}
}
