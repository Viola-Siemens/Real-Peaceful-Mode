package com.hexagram2021.real_peaceful_mode.common.block;

import com.hexagram2021.real_peaceful_mode.common.block.entity.ContinuousSummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class ContinuousSummonBlock extends BaseEntityBlock {
	public ContinuousSummonBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState blockState, BlockGetter level, BlockPos blockPos) {
		return true;
	}

	@Override @Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
		return new ContinuousSummonBlockEntity(blockPos, blockState);
	}

	@Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
		return createContinuousSummonBlockTicker(level, blockEntityType, RPMBlockEntities.CONTINUOUS_SUMMON_BLOCK.get());
	}

	@Override
	public RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return Shapes.empty();
	}

	@Nullable
	private static <T extends BlockEntity> BlockEntityTicker<T> createContinuousSummonBlockTicker(Level level, BlockEntityType<T> blockEntityType1, BlockEntityType<? extends ContinuousSummonBlockEntity> blockEntityType2) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType1, blockEntityType2, ContinuousSummonBlockEntity::serverTick);
	}
}
