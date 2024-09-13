package com.hexagram2021.real_peaceful_mode.common.world.structures.pieces;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.api.MissionType;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.GuardSlimeEntity;
import com.hexagram2021.real_peaceful_mode.common.register.*;
import com.hexagram2021.real_peaceful_mode.common.util.RandomMaze;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import javax.annotation.Nullable;
import java.util.List;

public class SlimeMazePieces {
	private static abstract sealed class AbstractSlimeMazePiece extends StructurePiece permits SlimeMazeEntrancePiece, SlimeMazeHousingPiece, SlimeMazeMainPiece, SlimeMazeTunnelPiece {
		protected AbstractSlimeMazePiece(StructurePieceType type, int depth, BoundingBox bbox) {
			super(type, depth, bbox);
		}
		protected AbstractSlimeMazePiece(StructurePieceType type, CompoundTag nbt) {
			super(type, nbt);
		}

		protected static final BlockState STONE = Blocks.STONE.defaultBlockState();
		protected static final BlockState STICKY_STONE = RPMBlocks.Decoration.STICKY_STONE.defaultBlockState();

		protected void placeStickyStone(WorldGenLevel level, BoundingBox bbox, int x, int y, int z, RandomSource random, float possibility) {
			if(random.nextFloat() < possibility) {
				this.placeBlock(level, STICKY_STONE, x, y, z, bbox);
			} else {
				this.placeBlock(level, STONE, x, y, z, bbox);
			}
		}

		protected static final BlockState SUMMON_BLOCK = RPMBlocks.TechnicalBlocks.SUMMON_BLOCK.defaultBlockState();

		@SuppressWarnings("SameParameterValue")
		@Nullable
		protected SummonBlockEntity placeSummonBlock(WorldGenLevel level, int x, int y, int z,
										@Nullable ResourceLocation missionId, MissionType missionType,
										@Nullable CompoundTag summonTag, int distance) {
			BlockPos summonBlockPos = this.getWorldPos(x, y, z);
			level.setBlock(summonBlockPos, SUMMON_BLOCK, WallTorchBlock.UPDATE_CLIENTS);
			BlockEntity blockEntity = level.getBlockEntity(summonBlockPos);
			if(blockEntity instanceof SummonBlockEntity summonBlockEntity) {
				summonBlockEntity.setTriggerableMission(
						missionId == null ? null : ForgeEventHandler.getMissionManager().getMission(missionId).orElse(null),
						missionType
				);
				summonBlockEntity.setSummonTag(summonTag);
				summonBlockEntity.setDistance(distance);

				return summonBlockEntity;
			}
			return null;
		}

		@SuppressWarnings("SameParameterValue")
		@Nullable
		protected GuardSlimeEntity spawnGuardSlime(WorldGenLevel level, int x, int y, int z) {
			GuardSlimeEntity guardSlime = RPMEntities.GUARD_SLIME.create(level.getLevel());
			if(guardSlime != null) {
				BlockPos spawnPos = this.getWorldPos(x, y, z);
				guardSlime.moveTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.0F, 0.0F);
				guardSlime.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.STRUCTURE, null, null);
				level.addFreshEntity(guardSlime);
			}
			return guardSlime;
		}

		protected void generateStickyStoneBox(WorldGenLevel level, BoundingBox bbox, int x1, int y1, int z1, int x2, int y2, int z2, RandomSource random, float possibility) {
			for(int y = y1; y <= y2; ++y) {
				for(int x = x1; x <= x2; ++x) {
					for(int z = z1; z <= z2; ++z) {
						this.placeStickyStone(level, bbox, x, y, z, random, possibility);
					}
				}
			}
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		}

		@Override
		public abstract void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random);

		@SuppressWarnings({"SameParameterValue", "UnusedReturnValue"})
		@Nullable
		protected AbstractSlimeMazePiece generateChildForward(SlimeMazeEntrancePiece entrance, StructurePieceAccessor pieces, RandomSource random, int xOffset, int yOffset) {
			Direction direction = this.getOrientation();
			if (direction != null) {
				switch(direction) {
					case NORTH:
						return this.generateAndAddPiece(entrance, pieces, random, this.boundingBox.minX() + xOffset, this.boundingBox.minY() + yOffset, this.boundingBox.minZ() - 1, direction);
					case SOUTH:
						return this.generateAndAddPiece(entrance, pieces, random, this.boundingBox.minX() + xOffset, this.boundingBox.minY() + yOffset, this.boundingBox.maxZ() + 1, direction);
					case WEST:
						return this.generateAndAddPiece(entrance, pieces, random, this.boundingBox.minX() - 1, this.boundingBox.minY() + yOffset, this.boundingBox.minZ() + xOffset, direction);
					case EAST:
						return this.generateAndAddPiece(entrance, pieces, random, this.boundingBox.maxX() + 1, this.boundingBox.minY() + yOffset, this.boundingBox.minZ() + xOffset, direction);
				}
			}

			return null;
		}

		@SuppressWarnings({"SameParameterValue", "UnusedReturnValue"})
		@Nullable
		protected AbstractSlimeMazePiece generateChildDown(SlimeMazeEntrancePiece entrance, StructurePieceAccessor pieces, RandomSource random, int xOffset, int zOffset) {
			Direction direction = this.getOrientation();
			if (direction != null) {
				switch(direction) {
					case NORTH:
						return this.generateAndAddPiece(entrance, pieces, random, this.boundingBox.minX() + xOffset, this.boundingBox.minY() - 1, this.boundingBox.minZ() + zOffset + 3, direction);
					case SOUTH:
						return this.generateAndAddPiece(entrance, pieces, random, this.boundingBox.minX() + xOffset, this.boundingBox.minY() - 1, this.boundingBox.minZ() + zOffset, direction);
					case WEST:
						return this.generateAndAddPiece(entrance, pieces, random, this.boundingBox.minX() + zOffset + 3, this.boundingBox.minY() - 1, this.boundingBox.minZ() + xOffset, direction);
					case EAST:
						return this.generateAndAddPiece(entrance, pieces, random, this.boundingBox.minX() + zOffset, this.boundingBox.minY() - 1, this.boundingBox.minZ() + xOffset, direction);
				}
			}

			return null;
		}

		@Nullable
		private AbstractSlimeMazePiece generateAndAddPiece(SlimeMazeEntrancePiece entrance, StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction) {
			AbstractSlimeMazePiece mazePiece = this.generateChildPiece(pieces, random, x, y, z, direction);
			if (mazePiece != null) {
				pieces.addPiece(mazePiece);
				entrance.pendingChildren.add(mazePiece);
			}

			return mazePiece;
		}

		@Nullable
		protected AbstractSlimeMazePiece generateChildPiece(StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction) {
			return null;
		}

		protected static boolean isOkBox(@Nullable BoundingBox bbox) {
			return bbox != null && bbox.minY() > -32;
		}
	}

	public static final class SlimeMazeEntrancePiece extends AbstractSlimeMazePiece {
		private static final int WIDTH = 8;
		private static final int HEIGHT = 6;
		private static final int LENGTH = 8;

		public final List<StructurePiece> pendingChildren = Lists.newArrayList();

		public SlimeMazeEntrancePiece(RandomSource random, int x, int y, int z) {
			this(random, x, y, z, getRandomHorizontalDirection(random));
		}
		public SlimeMazeEntrancePiece(RandomSource random, int x, int y, int z, Direction direction) {
			this(0, random, makeBoundingBox(x, y, z, direction, WIDTH, HEIGHT, LENGTH), direction);
		}

		public SlimeMazeEntrancePiece(int depth, RandomSource random, BoundingBox bbox, Direction direction) {
			this(RPMStructurePieceTypes.SLIME_MAZE_ENTRANCE_TYPE, depth, random, bbox, direction);

		}
		public SlimeMazeEntrancePiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			this(RPMStructurePieceTypes.SLIME_MAZE_ENTRANCE_TYPE, context, nbt);
		}

		private SlimeMazeEntrancePiece(StructurePieceType type, int depth, @SuppressWarnings("unused") RandomSource random, BoundingBox bbox, Direction direction) {
			super(type, depth, bbox);
			this.setOrientation(direction);
		}
		private SlimeMazeEntrancePiece(StructurePieceType type, @SuppressWarnings("unused") StructurePieceSerializationContext context, CompoundTag nbt) {
			super(type, nbt);
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
			super.addAdditionalSaveData(context, tag);
		}

		@Override
		public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
			if(piece instanceof SlimeMazeEntrancePiece entrance) {
				this.generateChildDown(entrance, pieces, random, 3, 3);
			}
		}

		@Override @Nullable
		protected SlimeMazeTunnelPiece generateChildPiece(StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction) {
			return SlimeMazeTunnelPiece.createPiece(pieces, random, x, y, z, direction, this.getGenDepth());
		}

		private static final BlockState CRACKY_STICKY_STONE = RPMBlocks.Decoration.CRACKY_STICKY_STONE.defaultBlockState();
		private static final float POSSIBILITY = 0.375F;

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random,
								BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			this.generateStickyStoneBox(level, boundingBox, 0, 0, 0, WIDTH - 1, 0, LENGTH - 1, random, POSSIBILITY);
			this.generateBox(level, boundingBox, 3, 0, 3, 4, 0, 4, CRACKY_STICKY_STONE, CRACKY_STICKY_STONE, false);
			this.generateStickyStoneBox(level, boundingBox, 2, 1, 0, 5, 3, 0, random, POSSIBILITY);
			this.generateBox(level, boundingBox, 3, 1, 0, 4, 3, 5, CAVE_AIR, CAVE_AIR, false);
			this.generateStickyStoneBox(level, boundingBox, 1, 1, 1, 1, 3, 1, random, POSSIBILITY);
			this.generateStickyStoneBox(level, boundingBox, 0, 1, 2, 0, 3, 5, random, POSSIBILITY);
			this.generateStickyStoneBox(level, boundingBox, 1, 1, 6, 1, 3, 6, random, POSSIBILITY);
			this.generateStickyStoneBox(level, boundingBox, 2, 1, 7, 5, 3, 7, random, POSSIBILITY);
			this.generateStickyStoneBox(level, boundingBox, 6, 1, 6, 6, 3, 6, random, POSSIBILITY);
			this.generateStickyStoneBox(level, boundingBox, 7, 1, 2, 7, 3, 5, random, POSSIBILITY);
			this.generateStickyStoneBox(level, boundingBox, 6, 1, 1, 6, 3, 1, random, POSSIBILITY);

			this.placeStickyStone(level, boundingBox, 2, 4, 1, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 3, 4, 1, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 4, 4, 1, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 5, 4, 1, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 1, 4, 2, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 2, 4, 2, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 5, 4, 2, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 6, 4, 2, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 1, 4, 3, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 6, 4, 3, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 1, 4, 4, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 6, 4, 4, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 1, 4, 5, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 2, 4, 5, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 5, 4, 5, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 6, 4, 5, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 2, 4, 6, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 3, 4, 6, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 4, 4, 6, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 5, 4, 6, random, POSSIBILITY);

			this.placeStickyStone(level, boundingBox, 2, 5, 3, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 2, 5, 4, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 3, 5, 2, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 3, 5, 3, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 3, 5, 4, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 3, 5, 5, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 4, 5, 2, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 4, 5, 3, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 4, 5, 4, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 4, 5, 5, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 5, 5, 3, random, POSSIBILITY);
			this.placeStickyStone(level, boundingBox, 5, 5, 4, random, POSSIBILITY);
		}
	}

	public static final class SlimeMazeTunnelPiece extends AbstractSlimeMazePiece {
		private static final int WIDTH = 4;
		private static final int HEIGHT = 25;
		private static final int LENGTH = 4;

		private static final int OFF_X = 1;
		private static final int OFF_Y = 24;
		private static final int OFF_Z = 1;

		public SlimeMazeTunnelPiece(int depth, RandomSource random, BoundingBox bbox, Direction direction) {
			this(RPMStructurePieceTypes.SLIME_MAZE_TUNNEL_TYPE, depth, random, bbox, direction);

		}
		public SlimeMazeTunnelPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			this(RPMStructurePieceTypes.SLIME_MAZE_TUNNEL_TYPE, context, nbt);
		}

		private SlimeMazeTunnelPiece(StructurePieceType type, int depth, @SuppressWarnings("unused") RandomSource random, BoundingBox bbox, Direction direction) {
			super(type, depth, bbox);
			this.setOrientation(direction);
		}
		private SlimeMazeTunnelPiece(StructurePieceType type, @SuppressWarnings("unused") StructurePieceSerializationContext context, CompoundTag nbt) {
			super(type, nbt);
		}

		@Override
		public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
			if(piece instanceof SlimeMazeEntrancePiece entrance) {
				this.generateChildForward(entrance, pieces, random, 1, 2);
			}
		}

		@Override @Nullable
		protected SlimeMazeMainPiece generateChildPiece(StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction) {
			return SlimeMazeMainPiece.createPiece(pieces, random, x, y, z, direction, this.getGenDepth());
		}

		private static final BlockState SLIME_BLOCK = Blocks.SLIME_BLOCK.defaultBlockState();

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random,
								BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			this.generateStickyStoneBox(level, boundingBox, 0, 0, 0, 3, HEIGHT - 1, 0, random, 0.625F);
			this.generateStickyStoneBox(level, boundingBox, 1, 3, 3, 2, HEIGHT - 1, 3, random, 0.625F);
			this.generateStickyStoneBox(level, boundingBox, 0, 0, 1, 0, HEIGHT - 1, 3, random, 0.625F);
			this.generateStickyStoneBox(level, boundingBox, 3, 0, 1, 3, HEIGHT - 1, 3, random, 0.625F);
			this.generateStickyStoneBox(level, boundingBox, 1, 0, 1, 2, 0, 3, random, 0.625F);
			this.generateStickyStoneBox(level, boundingBox, 1, 1, 3, 2, 1, 3, random, 0.625F);
			this.generateBox(level, boundingBox, 1, 1, 1, 2, 1, 2, SLIME_BLOCK, SLIME_BLOCK, false);
			this.generateBox(level, boundingBox, 1, 2, 1, 2, HEIGHT - 1, 2, CAVE_AIR, CAVE_AIR, false);
			this.generateBox(level, boundingBox, 1, 2, 3, 2, 3, 3, CAVE_AIR, CAVE_AIR, false);
		}

		@Nullable
		static SlimeMazeTunnelPiece createPiece(StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new SlimeMazeTunnelPiece(depth, random, boundingbox, direction) : null;
		}
	}

	public static final class SlimeMazeMainPiece extends AbstractSlimeMazePiece {

		private static final int WIDTH = 64;
		private static final int HEIGHT = 5;
		private static final int LENGTH = 64;

		private static final int OFF_X = 1;
		private static final int OFF_Y = 1;
		private static final int OFF_Z = 0;

		private static final int MAZE_LENGTH = 21;

		private final long seed;
		private final RandomMaze maze;

		public SlimeMazeMainPiece(int depth, RandomSource random, BoundingBox bbox, Direction direction) {
			this(RPMStructurePieceTypes.SLIME_MAZE_MAIN_TYPE, depth, random, bbox, direction);
		}
		public SlimeMazeMainPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			this(RPMStructurePieceTypes.SLIME_MAZE_MAIN_TYPE, context, nbt);
		}

		private SlimeMazeMainPiece(StructurePieceType type, int depth, @SuppressWarnings("unused") RandomSource random, BoundingBox bbox, Direction direction) {
			super(type, depth, bbox);
			this.setOrientation(direction);
			this.seed = random.nextLong();
			this.maze = new RandomMaze(MAZE_LENGTH, this.seed);
		}
		private SlimeMazeMainPiece(StructurePieceType type, @SuppressWarnings("unused") StructurePieceSerializationContext context, CompoundTag nbt) {
			super(type, nbt);
			this.seed = nbt.getLong("maze_seed");
			this.maze = new RandomMaze(MAZE_LENGTH, this.seed);
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
			super.addAdditionalSaveData(context, tag);
			tag.putLong("maze_seed", this.seed);
		}

		@Override
		public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
			if(piece instanceof SlimeMazeEntrancePiece entrance) {
				this.generateChildForward(entrance, pieces, random, 61, 1);
			}
		}

		@Override @Nullable
		protected SlimeMazeHousingPiece generateChildPiece(StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction) {
			return SlimeMazeHousingPiece.createPiece(pieces, random, x, y, z, direction, this.getGenDepth());
		}

		public static final BlockState WALL_TORCH = Blocks.WALL_TORCH.defaultBlockState();

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random,
								BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			//ceil and floor
			this.generateStickyStoneBox(level, boundingBox, 0, 0, 0, WIDTH - 1, 0, LENGTH - 1, random, 0.75F);
			this.generateStickyStoneBox(level, boundingBox, 0, HEIGHT - 1, 0, WIDTH - 1, HEIGHT - 1, LENGTH - 1, random, 0.75F);

			//main
			for(int i = 0; i < MAZE_LENGTH; ++i) {
				for(int j = 0; j < MAZE_LENGTH; ++j) {
					if(this.maze.isAir(2 * i, 2 * j)) {
						this.generateBox(level, boundingBox, 3 * i, 1, 3 * j, 3 * i, 3, 3 * j, CAVE_AIR, CAVE_AIR, false);
					} else {
						this.generateStickyStoneBox(level, boundingBox, 3 * i, 1, 3 * j, 3 * i, 3, 3 * j, random, 0.75F);
					}
					if(this.maze.isAir(2 * i + 1, 2 * j + 1)) {
						this.generateBox(level, boundingBox, 3 * i + 1, 1, 3 * j + 1, 3 * i + 2, 3, 3 * j + 2, CAVE_AIR, CAVE_AIR, false);
						if(random.nextInt(200) == 0) {
							this.spawnGuardSlime(level, 3 * i + 1, 1, 3 * j + 1);
						}
					} else {
						this.generateStickyStoneBox(level, boundingBox, 3 * i + 1, 1, 3 * j + 1, 3 * i + 2, 3, 3 * j + 2, random, 0.75F);
					}
					if(this.maze.isAir(2 * i + 1, 2 * j)) {
						this.generateBox(level, boundingBox, 3 * i + 1, 1, 3 * j, 3 * i + 2, 3, 3 * j, CAVE_AIR, CAVE_AIR, false);
					} else {
						this.generateStickyStoneBox(level, boundingBox, 3 * i + 1, 1, 3 * j, 3 * i + 2, 3, 3 * j, random, 0.75F);
					}
					if(this.maze.isAir(2 * i, 2 * j + 1)) {
						this.generateBox(level, boundingBox, 3 * i, 1, 3 * j + 1, 3 * i, 3, 3 * j + 2, CAVE_AIR, CAVE_AIR, false);
					} else {
						this.generateStickyStoneBox(level, boundingBox, 3 * i, 1, 3 * j + 1, 3 * i, 3, 3 * j + 2, random, 0.75F);
					}
				}
			}
			for(int i = 0; i < MAZE_LENGTH; ++i) {
				if(this.maze.isAir(2 * i, 2 * MAZE_LENGTH)) {
					this.generateBox(level, boundingBox, 3 * i, 1, LENGTH - 1, 3 * i, 3, LENGTH - 1, CAVE_AIR, CAVE_AIR, false);
				} else {
					this.generateStickyStoneBox(level, boundingBox, 3 * i, 1, LENGTH - 1, 3 * i, 3, LENGTH - 1, random, 0.75F);
				}
				if(this.maze.isAir(2 * i + 1, 2 * MAZE_LENGTH)) {
					this.generateBox(level, boundingBox, 3 * i + 1, 1, LENGTH - 1, 3 * i + 2, 3, LENGTH - 1, CAVE_AIR, CAVE_AIR, false);
				} else {
					this.generateStickyStoneBox(level, boundingBox, 3 * i + 1, 1, LENGTH - 1, 3 * i + 2, 3, LENGTH - 1, random, 0.75F);
				}
			}
			for(int j = 0; j < MAZE_LENGTH; ++j) {
				if(this.maze.isAir(2 * MAZE_LENGTH, 2 * j)) {
					this.generateBox(level, boundingBox, WIDTH - 1, 1, 3 * j, WIDTH - 1, 3, 3 * j, CAVE_AIR, CAVE_AIR, false);
				} else {
					this.generateStickyStoneBox(level, boundingBox, WIDTH - 1, 1, 3 * j, WIDTH - 1, 3, 3 * j, random, 0.75F);
				}
				if(this.maze.isAir(2 * MAZE_LENGTH, 2 * j + 1)) {
					this.generateBox(level, boundingBox, WIDTH - 1, 1, 3 * j + 1, WIDTH - 1, 3, 3 * j + 2, CAVE_AIR, CAVE_AIR, false);
				} else {
					this.generateStickyStoneBox(level, boundingBox, WIDTH - 1, 1, 3 * j + 1, WIDTH - 1, 3, 3 * j + 2, random, 0.75F);
				}
			}
			if(this.maze.isAir(2 * MAZE_LENGTH, 2 * MAZE_LENGTH)) {
				this.generateBox(level, boundingBox, WIDTH - 1, 1, LENGTH - 1, WIDTH - 1, 3, LENGTH - 1, CAVE_AIR, CAVE_AIR, false);
			} else {
				this.generateStickyStoneBox(level, boundingBox, WIDTH - 1, 1, LENGTH - 1, WIDTH - 1, 3, LENGTH - 1, random, 0.75F);
			}

			//torch
			for(int i = 0; i < MAZE_LENGTH; i += 4) {
				for(int j = 0; j < MAZE_LENGTH; j += 4) {
					if(!this.maze.isAir(2 * i + 1, 2 * j)) {
						BlockState wallTorch = WALL_TORCH.setValue(WallTorchBlock.FACING, Direction.NORTH);
						this.placeBlock(level, wallTorch, 3 * i + 1, 2, 3 * j + 1, boundingBox);
						this.placeBlock(level, wallTorch, 3 * i + 2, 2, 3 * j + 1, boundingBox);
					}
					if (!this.maze.isAir(2 * i + 1, 2 * j + 2)) {
						BlockState wallTorch = WALL_TORCH.setValue(WallTorchBlock.FACING, Direction.SOUTH);
						this.placeBlock(level, wallTorch, 3 * i + 1, 2, 3 * j + 2, boundingBox);
						this.placeBlock(level, wallTorch, 3 * i + 2, 2, 3 * j + 2, boundingBox);
					}
					if(!this.maze.isAir(2 * i, 2 * j + 1)) {
						BlockState wallTorch = WALL_TORCH.setValue(WallTorchBlock.FACING, Direction.EAST);
						this.placeBlock(level, wallTorch, 3 * i + 1, 2, 3 * j + 1, boundingBox);
						this.placeBlock(level, wallTorch, 3 * i + 1, 2, 3 * j + 2, boundingBox);
					}
					if (!this.maze.isAir(2 * i + 2, 2 * j + 1)) {
						BlockState wallTorch = WALL_TORCH.setValue(WallTorchBlock.FACING, Direction.WEST);
						this.placeBlock(level, wallTorch, 3 * i + 2, 2, 3 * j + 1, boundingBox);
						this.placeBlock(level, wallTorch, 3 * i + 2, 2, 3 * j + 2, boundingBox);
					}
				}
			}

			this.placeSummonBlock(level, 1, 1, 1, GuardSlimeEntity.GuardSlimeMissions.SEEK_HELP.missionId(), MissionType.FINISH, null, 6);
		}

		@Nullable
		static SlimeMazeMainPiece createPiece(StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new SlimeMazeMainPiece(depth, random, boundingbox, direction) : null;
		}
	}

	public static final class SlimeMazeHousingPiece extends AbstractSlimeMazePiece {
		private static final int WIDTH = 10;
		private static final int HEIGHT = 9;
		private static final int LENGTH = 10;

		private static final int OFF_X = 4;
		private static final int OFF_Y = 5;
		private static final int OFF_Z = 0;

		public SlimeMazeHousingPiece(int depth, RandomSource random, BoundingBox bbox, Direction direction) {
			this(RPMStructurePieceTypes.SLIME_MAZE_HOUSING_TYPE, depth, random, bbox, direction);

		}
		public SlimeMazeHousingPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
			this(RPMStructurePieceTypes.SLIME_MAZE_HOUSING_TYPE, context, nbt);
		}

		private SlimeMazeHousingPiece(StructurePieceType type, int depth, @SuppressWarnings("unused") RandomSource random, BoundingBox bbox, Direction direction) {
			super(type, depth, bbox);
			this.setOrientation(direction);
		}
		private SlimeMazeHousingPiece(StructurePieceType type, @SuppressWarnings("unused") StructurePieceSerializationContext context, CompoundTag nbt) {
			super(type, nbt);
		}

		@Override
		public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
		}

		private static final BlockState STONE_STAIRS = Blocks.STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
		private static final BlockState STICKY_STONE_STAIRS = RPMBlocks.Decoration.STICKY_STONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);

		private void placeStickyStoneStair(WorldGenLevel level, BoundingBox bbox, int x, int y, int z, RandomSource random, float possibility) {
			if(random.nextFloat() < possibility) {
				this.placeBlock(level, STICKY_STONE_STAIRS, x, y, z, bbox);
			} else {
				this.placeBlock(level, STONE_STAIRS, x, y, z, bbox);
			}
		}

		public static final BlockState WALL_TORCH = Blocks.WALL_TORCH.defaultBlockState();

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random,
								BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			this.generateBox(level, boundingBox, 1, 1, 1, WIDTH - 2, HEIGHT - 2, LENGTH - 2, CAVE_AIR, CAVE_AIR, false);
			this.generateStickyStoneBox(level, boundingBox, 0, 0, 0, WIDTH - 1, 0, LENGTH - 1, random, 0.9F);
			this.generateStickyStoneBox(level, boundingBox, 0, HEIGHT - 1, 0, WIDTH - 1, HEIGHT - 1, LENGTH - 1, random, 0.75F);
			this.generateStickyStoneBox(level, boundingBox, 0, 1, 0, 0, HEIGHT - 2, LENGTH - 1, random, 0.75F);
			this.generateStickyStoneBox(level, boundingBox, WIDTH - 1, 1, 0, WIDTH - 1, HEIGHT - 2, LENGTH - 1, random, 0.75F);
			this.generateStickyStoneBox(level, boundingBox, 1, 1, 0, WIDTH - 2, HEIGHT - 2, 0, random, 0.75F);
			this.generateStickyStoneBox(level, boundingBox, 1, 1, LENGTH - 1, WIDTH - 2, HEIGHT - 2, LENGTH - 1, random, 0.75F);
			this.generateBox(level, boundingBox, 4, 5, 0, 5, 6, 0, CAVE_AIR, CAVE_AIR, false);
			this.placeStickyStoneStair(level, boundingBox, 4, 4, 1, random, 0.8F);
			this.placeStickyStoneStair(level, boundingBox, 5, 4, 1, random, 0.8F);
			this.generateStickyStoneBox(level, boundingBox, 4, 1, 1, 5, 3, 1, random, 0.75F);
			this.placeStickyStoneStair(level, boundingBox, 4, 3, 2, random, 0.85F);
			this.placeStickyStoneStair(level, boundingBox, 5, 3, 2, random, 0.85F);
			this.generateStickyStoneBox(level, boundingBox, 4, 1, 2, 5, 2, 2, random, 0.75F);
			this.placeStickyStoneStair(level, boundingBox, 4, 2, 3, random, 0.85F);
			this.placeStickyStoneStair(level, boundingBox, 5, 2, 3, random, 0.85F);
			this.generateStickyStoneBox(level, boundingBox, 4, 1, 3, 5, 1, 3, random, 0.75F);
			this.placeStickyStoneStair(level, boundingBox, 4, 1, 4, random, 0.85F);
			this.placeStickyStoneStair(level, boundingBox, 5, 1, 4, random, 0.85F);
			BlockState southWallTorch = WALL_TORCH.setValue(WallTorchBlock.FACING, Direction.SOUTH);
			this.placeBlock(level, southWallTorch, 4, 3, LENGTH - 2, boundingBox);
			this.placeBlock(level, southWallTorch, 5, 3, LENGTH - 2, boundingBox);
			BlockState eastWallTorch = WALL_TORCH.setValue(WallTorchBlock.FACING, Direction.EAST);
			this.placeBlock(level, eastWallTorch, 1, 3, 4, boundingBox);
			this.placeBlock(level, eastWallTorch, 1, 3, 5, boundingBox);
			BlockState westWallTorch = WALL_TORCH.setValue(WallTorchBlock.FACING, Direction.WEST);
			this.placeBlock(level, westWallTorch, WIDTH - 2, 3, 4, boundingBox);
			this.placeBlock(level, westWallTorch, WIDTH - 2, 3, 5, boundingBox);

			CompoundTag summonTagSickSlime = new CompoundTag();
			summonTagSickSlime.putString("id", RPMEntityKeys.GUARD_SLIME.location().toString());
			summonTagSickSlime.putInt("Size", 1);
			summonTagSickSlime.putBoolean("HasArmor", false);
			summonTagSickSlime.putBoolean("IsSick", true);
			summonTagSickSlime.putBoolean("PersistenceRequired", true);
			ListTag activeEffects = new ListTag();
			activeEffects.add(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 2, false, false).save(new CompoundTag()));
			summonTagSickSlime.put("ActiveEffects", activeEffects);
			SummonBlockEntity summonBlockEntity = this.placeSummonBlock(
					level, 2, 1, 1,
					GuardSlimeEntity.GuardSlimeMissions.SAVE_ME.missionId(), MissionType.RECEIVE,
					summonTagSickSlime, 4
			);
			if(summonBlockEntity != null) {
				summonBlockEntity.setExtraWork(RPMSummonBlockEvents.WORK_SLIME_LADDER.toString());
			}

			CompoundTag summonTagHelpSeeker = new CompoundTag();
			summonTagHelpSeeker.putString("id", RPMEntityKeys.GUARD_SLIME.location().toString());
			summonTagHelpSeeker.putBoolean("HasArmor", false);
			summonTagHelpSeeker.putBoolean("PersistenceRequired", true);
			this.placeSummonBlock(level, 1, 1, 4, GuardSlimeEntity.GuardSlimeMissions.QUARREL.missionId(), MissionType.RECEIVE, summonTagHelpSeeker, 6);

			GuardSlimeEntity guardSlimeEntity;
			guardSlimeEntity = this.spawnGuardSlime(level, 2, 2, 7);
			if(guardSlimeEntity != null) {
				guardSlimeEntity.setHasArmor(false);
			}
			guardSlimeEntity = this.spawnGuardSlime(level, WIDTH - 3, 2, 7);
			if(guardSlimeEntity != null) {
				guardSlimeEntity.setHasArmor(false);
			}
		}

		@Nullable
		static SlimeMazeHousingPiece createPiece(StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new SlimeMazeHousingPiece(depth, random, boundingbox, direction) : null;
		}

		@SuppressWarnings("deprecation")
		public static void extraWorkAfterTrigger(ServerLevel level, BlockPos pos) {
			BlockState ladder = Blocks.LADDER.defaultBlockState();
			if(Blocks.LADDER.canSurvive(ladder.setValue(LadderBlock.FACING, Direction.SOUTH), level, pos)) {
				ladder = ladder.setValue(LadderBlock.FACING, Direction.SOUTH);
			} else if(Blocks.LADDER.canSurvive(ladder.setValue(LadderBlock.FACING, Direction.NORTH), level, pos)) {
				ladder = ladder.setValue(LadderBlock.FACING, Direction.NORTH);
			} else if(Blocks.LADDER.canSurvive(ladder.setValue(LadderBlock.FACING, Direction.EAST), level, pos)) {
				ladder = ladder.setValue(LadderBlock.FACING, Direction.EAST);
			} else if(Blocks.LADDER.canSurvive(ladder.setValue(LadderBlock.FACING, Direction.WEST), level, pos)) {
				ladder = ladder.setValue(LadderBlock.FACING, Direction.WEST);
			} else {
				ladder = null;
			}
			int y = 0;
			BlockPos blockPos = pos.offset(0, y, 0);
			while(level.getBlockState(blockPos).isAir()) {
				if(ladder != null) {
					level.setBlock(blockPos, ladder, Block.UPDATE_ALL);
				}
				y += 1;
				blockPos = pos.offset(0, y, 0);
			}
			if(ladder == null) {
				ladder = Blocks.LADDER.defaultBlockState();
			}
			BlockState target = level.getBlockState(blockPos);
			while(target.canOcclude()) {
				level.setBlock(blockPos, ladder, Block.UPDATE_ALL);
				y += 1;
				blockPos = pos.offset(0, y, 0);
				target = level.getBlockState(blockPos);
			}
			level.setBlock(blockPos.below(), Blocks.OAK_TRAPDOOR.defaultBlockState(), Block.UPDATE_ALL);
		}
	}
}
