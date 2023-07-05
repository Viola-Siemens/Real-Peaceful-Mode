package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.register.RPMSounds;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Locale;

import static com.hexagram2021.real_peaceful_mode.common.register.RPMNoteBlockInstruments.DARK_ZOMBIE_KNIGHT;

@SuppressWarnings("unused")
@Mixin(NoteBlockInstrument.class)
public class NoteBlockInstrumentMixin {
	NoteBlockInstrumentMixin(String enumName, int ord, String name, Holder<SoundEvent> sound, NoteBlockInstrument.Type type) {
		throw new UnsupportedOperationException("Replaced by Mixin");
	}

	@Shadow @Mutable @Final
	private static NoteBlockInstrument[] $VALUES;

	@SuppressWarnings("SameParameterValue")
	private static NoteBlockInstrument createSkullInstrument(String enumName, int ord, SoundEvent soundEvent) {
		return (NoteBlockInstrument)(Object)new NoteBlockInstrumentMixin(
				enumName, ord, enumName.toLowerCase(Locale.ROOT), Holder.direct(soundEvent), NoteBlockInstrument.Type.MOB_HEAD
		);
	}

	@Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/state/properties/NoteBlockInstrument;$VALUES:[Lnet/minecraft/world/level/block/state/properties/NoteBlockInstrument;", shift = At.Shift.AFTER))
	private static void ipp_injectEnum(CallbackInfo ci) {
		int ordinal = $VALUES.length;
		$VALUES = Arrays.copyOf($VALUES, ordinal + 1);

		DARK_ZOMBIE_KNIGHT = $VALUES[ordinal] =
				createSkullInstrument("BASSOON", ordinal, RPMSounds.NOTE_BLOCK_IMITATE_DARK_ZOMBIE_KNIGHT);
	}
}
