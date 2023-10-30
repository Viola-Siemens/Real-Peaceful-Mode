package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlockTags;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public interface ICrackable {
	void setCrackable(boolean crackable);
	boolean getCrackable();

	static <T extends Projectile & ICrackable> void onHitBlock(BlockHitResult blockHitResult, T projectile) {
		BlockState target = projectile.level().getBlockState(blockHitResult.getBlockPos());
		if(projectile.level() instanceof ServerLevel serverLevel && projectile.getCrackable() && target.is(RPMBlockTags.CRACKABLE)) {
			if(target.is(RPMBlocks.Decoration.INFESTED_GLOWING_CRYSTAL.get())) {
				MissionHelper.triggerMissionForPlayers(
						new ResourceLocation(MODID, "skeleton1"), SummonBlockEntity.SummonMissionType.FINISH,
						serverLevel, player -> player.closerThan(projectile, 32.0D), null, player -> {}
				);
				ExperienceOrb.award(serverLevel, blockHitResult.getBlockPos().getCenter(), 80);
			}
			serverLevel.removeBlock(blockHitResult.getBlockPos(), false);
			serverLevel.playSound(null, blockHitResult.getBlockPos(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS);
			projectile.setCrackable(false);
		}
	}
}
