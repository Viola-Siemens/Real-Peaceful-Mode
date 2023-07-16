package com.hexagram2021.real_peaceful_mode.common.block;

import com.hexagram2021.real_peaceful_mode.common.block.entity.CultureTableBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class CultureTableBlock extends AbstractFurnaceBlock {
	public CultureTableBlock(Properties props) {
		super(props);
	}

	@Override
	protected void openContainer(Level level, BlockPos blockPos, Player player) {

	}

	@Override @Nullable
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return null;
	}

	@Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, RPMBlockEntities.CULTURE_TABLE.get(), CultureTableBlockEntity::serverTick);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos blockPos, PathComputationType type) {
		return false;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasCustomHoverName()) {
			BlockEntity blockentity = level.getBlockEntity(blockPos);
			if (blockentity instanceof CultureTableBlockEntity cultureTableBlockEntity) {
				cultureTableBlockEntity.setCustomName(itemStack.getHoverName());
			}
		}
	}

	@Override
	public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newBlockState, boolean b) {
		if (!blockState.is(newBlockState.getBlock())) {
			BlockEntity blockentity = level.getBlockEntity(blockPos);
			if (blockentity instanceof CultureTableBlockEntity cultureTableBlockEntity) {
				if (level instanceof ServerLevel serverLevel) {
					Containers.dropContents(serverLevel, blockPos, cultureTableBlockEntity);
				}

				level.updateNeighbourForOutputSignal(blockPos, this);
			}

			super.onRemove(blockState, level, blockPos, newBlockState, b);
		}
	}
}
