package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.register.RPMMapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(MapItemSavedData.class)
public class MapItemSavedDataMixin {
	@Shadow @Final
	Map<String, MapDecoration> decorations;

	@Inject(method = "isExplorationMap", at = @At(value = "HEAD"), cancellable = true)
	public void rpm$checkIsCustomMapDecoration(CallbackInfoReturnable<Boolean> cir) {
		for(MapDecoration decoration : this.decorations.values()) {
			if(RPMMapDecorationTypes.DECORATION_TYPES.contains(decoration.getType())) {
				cir.setReturnValue(true);
				cir.cancel();
				return;
			}
		}
	}
}
