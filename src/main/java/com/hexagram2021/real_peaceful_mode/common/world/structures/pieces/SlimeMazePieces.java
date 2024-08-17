package com.hexagram2021.real_peaceful_mode.common.world.structures.pieces;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import com.hexagram2021.real_peaceful_mode.common.register.RPMStructurePieceTypes;
import com.hexagram2021.real_peaceful_mode.common.util.RandomMaze;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
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
	private static abstract sealed class AbstractSlimeMazePiece extends StructurePiece permits SlimeMazeEntrancePiece, SlimeMazeTunnelPiece, SlimeMazeMainPiece {
		protected AbstractSlimeMazePiece(StructurePieceType type, int depth, BoundingBox bbox) {
			super(type, depth, bbox);
		}
		protected AbstractSlimeMazePiece(StructurePieceType type, CompoundTag nbt) {
			super(type, nbt);
		}

		protected static final BlockState STONE = Blocks.STONE.defaultBlockState();
		protected static final BlockState STICKY_STONE = RPMBlocks.Decoration.STICKY_STONE.defaultBlockState();

		protected void generateStickyStoneBox(WorldGenLevel level, BoundingBox bbox, int x1, int y1, int z1, int x2, int y2, int z2, RandomSource random, float possibility) {
			for(int y = y1; y <= y2; ++y) {
				for(int x = x1; x <= x2; ++x) {
					for(int z = z1; z <= z2; ++z) {
						if(random.nextFloat() < possibility) {
							this.placeBlock(level, STICKY_STONE, x, y, z, bbox);
						} else {
							this.placeBlock(level, STONE, x, y, z, bbox);
						}
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

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random,
								BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			this.generateStickyStoneBox(level, boundingBox, 0, 0, 0, WIDTH - 1, 0, LENGTH - 1, random, 0.375F);
			this.generateBox(level, boundingBox, 3, 0, 3, 4, 0, 4, CRACKY_STICKY_STONE, CRACKY_STICKY_STONE, false);
			this.generateStickyStoneBox(level, boundingBox, 2, 1, 0, 5, 3, 0, random, 0.375F);
			this.generateStickyStoneBox(level, boundingBox, 1, 1, 1, 1, 3, 1, random, 0.375F);
			this.generateStickyStoneBox(level, boundingBox, 0, 1, 2, 0, 3, 5, random, 0.375F);
			this.generateStickyStoneBox(level, boundingBox, 1, 1, 6, 1, 3, 6, random, 0.375F);
			this.generateStickyStoneBox(level, boundingBox, 2, 1, 7, 5, 3, 7, random, 0.375F);
			this.generateStickyStoneBox(level, boundingBox, 6, 1, 6, 6, 3, 6, random, 0.375F);
			this.generateStickyStoneBox(level, boundingBox, 7, 1, 2, 7, 3, 5, random, 0.375F);
			this.generateStickyStoneBox(level, boundingBox, 6, 1, 1, 6, 3, 1, random, 0.375F);
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

			//torch;
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
		}

		@Nullable
		static SlimeMazeMainPiece createPiece(StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction, int depth) {
			BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -OFF_X, -OFF_Y, -OFF_Z, WIDTH, HEIGHT, LENGTH, direction);
			return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null ? new SlimeMazeMainPiece(depth, random, boundingbox, direction) : null;
		}
	}
}
