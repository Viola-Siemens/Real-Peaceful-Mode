package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.ICrackable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile implements ICrackable {
	@Unique
	private boolean rpm$crackable;

	protected AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void rpm$setCrackable(boolean crackable) {
		this.rpm$crackable = crackable;
	}

	@Override
	public boolean rpm$getCrackable() {
		return this.rpm$crackable;
	}

	@Inject(method = "onHitBlock", at = @At(value = "TAIL"))
	protected void rpm$tryBreakCrackableBlock(BlockHitResult hitResult, CallbackInfo ci) {
		ICrackable.onHitBlock(hitResult, this);
	}
}
