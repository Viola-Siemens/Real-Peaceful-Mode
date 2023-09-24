package com.hexagram2021.real_peaceful_mode.common.block;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.HuskWorkmanEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("deprecation")
public class DirtyWaterBlock extends Block {
	private static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.9D, 1.0D);

	public DirtyWaterBlock(BlockBehaviour.Properties props) {
		super(props);
	}

	@Override
	public boolean skipRendering(BlockState blockState, BlockState other, Direction direction) {
		return other.is(this) || super.skipRendering(blockState, other, direction);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter level, BlockPos blockPos) {
		return Shapes.empty();
	}

	@Override
	public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
		if(level instanceof ServerLevel serverLevel && entity instanceof ItemEntity itemEntity) {
			ItemStack itemStack = itemEntity.getItem();
			if(itemStack.is(RPMItems.Materials.PAC.get())) {
				for(int x = -2; x <= 2; ++x) {
					for(int z = -2; z <= 2; ++z) {
						for(int y = -2; y < 2; ++y) {
							BlockPos current = blockPos.offset(x, y, z);
							if(serverLevel.getBlockState(current).is(this)) {
								serverLevel.setBlock(current, Blocks.WATER.defaultBlockState(), UPDATE_ALL);
							}
						}
					}
				}
				level.getEntities(itemEntity, itemEntity.getBoundingBox().inflate(16.0D)).stream()
						.filter(e -> e instanceof HuskWorkmanEntity).map(e -> (HuskWorkmanEntity) e).findAny()
						.ifPresent(huskWorkman -> MissionHelper.triggerMissionForPlayers(
								new ResourceLocation(MODID, "husk2"), SummonBlockEntity.SummonMissionType.FINISH, serverLevel,
								player -> huskWorkman.closerThan(player, 16.0D), huskWorkman, player -> {}
						));
				itemEntity.discard();
			}
		}
	}

	@Override
	public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float fallDistance) {
		if (fallDistance >= 4.0F && entity instanceof LivingEntity livingentity) {
			LivingEntity.Fallsounds sounds = livingentity.getFallSounds();
			SoundEvent soundevent = (double)fallDistance < 7.0D ? sounds.small() : sounds.big();
			entity.playSound(soundevent, 1.0F, 1.0F);
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		if (context instanceof EntityCollisionContext entitycollisioncontext) {
			Entity entity = entitycollisioncontext.getEntity();
			if (entity != null) {
				if (entity.fallDistance > 2.5F) {
					return FALLING_COLLISION_SHAPE;
				}
			}
		}

		return Shapes.empty();
	}

	@Override
	public VoxelShape getVisualShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public boolean isPathfindable(BlockState blockState, BlockGetter level, BlockPos blockPos, PathComputationType p_154261_) {
		return false;
	}
}
