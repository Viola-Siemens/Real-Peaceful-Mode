package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.ICrackable;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlockTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin implements ICrackable {
	private boolean crackable;

	@Override
	public void setCrackable(boolean crackable) {
		this.crackable = crackable;
	}

	@Override
	public boolean getCrackable() {
		return this.crackable;
	}

	@Inject(method = "onHitBlock", at = @At(value = "TAIL"))
	protected void tryBreakCrackableBlock(BlockHitResult hitResult, CallbackInfo ci) {
		AbstractArrow current = (AbstractArrow)(Object)this;
		if(this.getCrackable() && current.level().getBlockState(hitResult.getBlockPos()).is(RPMBlockTags.CRACKABLE)) {
			current.level().removeBlock(hitResult.getBlockPos(), false);
			current.level().playSound(null, hitResult.getBlockPos(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS);
			this.setCrackable(false);
		}
	}
}
