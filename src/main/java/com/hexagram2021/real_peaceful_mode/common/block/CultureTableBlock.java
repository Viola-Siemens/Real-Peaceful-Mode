package com.hexagram2021.real_peaceful_mode.common.block;

import com.hexagram2021.real_peaceful_mode.common.block.entity.CultureTableBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class CultureTableBlock extends AbstractFurnaceBlock {
	public static final MapCodec<CultureTableBlock> CODEC = simpleCodec(CultureTableBlock::new);

	public CultureTableBlock(Properties props) {
		super(props);
	}

	@Override
	protected MapCodec<? extends CultureTableBlock> codec() {
		return CODEC;
	}

	@Override
	protected void openContainer(Level level, BlockPos blockPos, Player player) {
		BlockEntity blockentity = level.getBlockEntity(blockPos);
		if (blockentity instanceof CultureTableBlockEntity) {
			player.openMenu((MenuProvider)blockentity);
		}
	}

	@Override @Nullable
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new CultureTableBlockEntity(blockPos, blockState);
	}

	@Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, RPMBlockEntities.CULTURE_TABLE.get(), CultureTableBlockEntity::serverTick);
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

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	}

	@Override
	public boolean isPathfindable(BlockState blockState, PathComputationType context) {
		return false;
	}
}
