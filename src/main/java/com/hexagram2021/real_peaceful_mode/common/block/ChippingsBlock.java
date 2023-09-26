package com.hexagram2021.real_peaceful_mode.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation")
public class ChippingsBlock extends Block {
	public ChippingsBlock(Properties props) {
		super(props);
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Block.box(4.0D, 0.0D, 4.0D, 12.0D, 1.0D, 12.0D);
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
		return isFaceFull(level.getBlockState(pos.below()).getCollisionShape(level, pos.below()), Direction.UP);
	}
}
