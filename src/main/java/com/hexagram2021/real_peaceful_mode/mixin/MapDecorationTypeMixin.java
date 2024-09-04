package com.hexagram2021.real_peaceful_mode.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

import static com.hexagram2021.real_peaceful_mode.common.register.RPMMapDecorationTypes.*;

@Mixin(MapDecoration.Type.class)
public class MapDecorationTypeMixin {
	@SuppressWarnings("unused")
	MapDecorationTypeMixin(String name, int ord, boolean renderedOnFrame, boolean trackCount) {
		throw new UnsupportedOperationException("Replaced by Mixin");
	}

	@SuppressWarnings("unused")
	MapDecorationTypeMixin(String name, int ord, boolean renderedOnFrame, int mapColor, boolean trackCount) {
		throw new UnsupportedOperationException("Replaced by Mixin");
	}

	@Shadow @Final @Mutable
	private static MapDecoration.Type[] $VALUES;

	@Inject(method = "<clinit>()V", at = @At(value = "FIELD", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/level/saveddata/maps/MapDecoration$Type;$VALUES:[Lnet/minecraft/world/level/saveddata/maps/MapDecoration$Type;"))
	private static void rpm$injectEnum(CallbackInfo ci) {
		int ordinal = $VALUES.length;
		$VALUES = Arrays.copyOf($VALUES, ordinal + 2);

		SLIME_MAZE = $VALUES[ordinal] = (MapDecoration.Type)(Object)new MapDecorationTypeMixin("RPM$SLIME_MAZE", ordinal, true, 0x8eb683, false);
		CRYSTAL_SKULL_ISLAND = $VALUES[ordinal + 1] = (MapDecoration.Type)(Object)new MapDecorationTypeMixin("RPM$CRYSTAL_SKULL_ISLAND", ordinal + 1, true, 0x80bbc9, false);

		DECORATION_TYPES = ImmutableSet.of(SLIME_MAZE, CRYSTAL_SKULL_ISLAND);
	}
}
