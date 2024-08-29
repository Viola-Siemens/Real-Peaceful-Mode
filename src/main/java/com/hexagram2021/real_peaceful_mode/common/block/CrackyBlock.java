package com.hexagram2021.real_peaceful_mode.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.List;
import java.util.function.Predicate;

public class CrackyBlock extends Block {
	public static final IntegerProperty AGE_4 = BlockStateProperties.AGE_4;

	public CrackyBlock(BlockBehaviour.Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE_4, 0));
	}

	@Override
	public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
		if (!level.isClientSide() && this.testStepOnEntity(entity) && !level.getBlockTicks().willTickThisTick(blockPos, this)) {
			level.scheduleTick(blockPos, this, 10);
		}

		super.stepOn(level, blockPos, blockState, entity);
	}

	protected boolean testStepOnEntity(Entity entity) {
		return true;
	}
	protected Class<? extends Entity> responsibleEntityType() {
		return Entity.class;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource random) {
		int age = blockState.getValue(AGE_4);
		Class<? extends Entity> clazz = this.responsibleEntityType();
		Predicate<Entity> entityPredicate = entity -> entity.onGround() && entity.getOnPosLegacy().equals(blockPos) && this.testStepOnEntity(entity);
		List<? extends Entity> entitiesOnBlock;
		if(clazz == Player.class || clazz == ServerPlayer.class) {
			entitiesOnBlock = level.getPlayers(entityPredicate);
		} else {
			entitiesOnBlock = level.getEntities(EntityTypeTest.forClass(clazz), entityPredicate);
		}
		if(entitiesOnBlock.isEmpty()) {
			if(age > 0) {
				if(random.nextInt(100 / age / age) < 10) {
					level.setBlock(blockPos, blockState.setValue(AGE_4, age - 1), Block.UPDATE_ALL);
				}
				level.scheduleTick(blockPos, this, 10);
			}
		} else if(age == 4) {
			for(int i = -1; i <= 1; ++i) {
				for(int j = -1; j <= 1; ++j) {
					BlockPos pos = blockPos.offset(i, 0, j);
					if (level.getBlockState(pos).is(this)) {
						level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
					}
				}
			}
			level.playSound(null, blockPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.75F, 1.25F);
			level.playSound(null, blockPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.25F, 1.0F);
			level.playSound(null, blockPos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8F, 0.8F);
		} else {
			level.setBlock(blockPos, blockState.setValue(AGE_4, age + 1), Block.UPDATE_ALL);
			level.playSound(null, blockPos, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 0.5F, 0.9F + random.nextFloat() * 0.2F);
			level.scheduleTick(blockPos, this, 10);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AGE_4);
	}
}
