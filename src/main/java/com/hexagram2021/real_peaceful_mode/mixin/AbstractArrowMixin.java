package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.ICrackable;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlockTags;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

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
		BlockState target = current.level().getBlockState(hitResult.getBlockPos());
		if(current.level() instanceof ServerLevel serverLevel && this.getCrackable() && target.is(RPMBlockTags.CRACKABLE)) {
			if(target.is(RPMBlocks.Decoration.INFESTED_GLOWING_CRYSTAL.get())) {
				MissionHelper.triggerMissionForPlayers(
						new ResourceLocation(MODID, "skeleton1"), SummonBlockEntity.SummonMissionType.FINISH,
						serverLevel, player -> player.closerThan(current, 32.0D), null, player -> {}
				);
				ExperienceOrb.award(serverLevel, current.position(), 80);
			}
			current.level().removeBlock(hitResult.getBlockPos(), false);
			current.level().playSound(null, hitResult.getBlockPos(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS);
			this.setCrackable(false);
		}
	}
}
