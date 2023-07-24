package com.hexagram2021.real_peaceful_mode.common.world.structures.pieces;

import com.hexagram2021.real_peaceful_mode.common.register.RPMStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class AbandonedMagicPoolPieces {
	private static final ResourceLocation ABANDONED_MAGIC_POOL = new ResourceLocation(MODID, "abandoned_magic_pool/abandoned_magic_pool");

	public static void addPieces(StructureTemplateManager structureManager, BlockPos pos, Rotation rotation, StructurePieceAccessor pieces) {
		pieces.addPiece(new AbandonedMagicPoolPiece(structureManager, ABANDONED_MAGIC_POOL, pos, rotation));
	}

	public static class AbandonedMagicPoolPiece extends TemplateStructurePiece {
		public AbandonedMagicPoolPiece(StructureTemplateManager structureManager, ResourceLocation location, BlockPos pos, Rotation rotation) {
			super(RPMStructurePieceTypes.ABANDONED_MAGIC_POOL_TYPE, 0, structureManager, location, location.toString(), makeSettings(rotation), pos.offset(-5, -1, -5));
		}

		public AbandonedMagicPoolPiece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(RPMStructurePieceTypes.ABANDONED_MAGIC_POOL_TYPE, tag, context.structureTemplateManager(), (location) -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
		}


		private static StructurePlaceSettings makeSettings(Rotation rotation) {
			return (new StructurePlaceSettings())
					.setRotation(rotation)
					.setMirror(Mirror.LEFT_RIGHT)
					.setRotationPivot(new BlockPos(5, 1, 5))
					.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
		}


		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
			super.addAdditionalSaveData(context, tag);
			tag.putString("Rot", this.placeSettings.getRotation().name());
		}

		@Override
		protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox sbb) {
		}

		public static final double BROKEN_BRICKS_PERCENTAGE = 0.25D;
		public static final double BROKEN_WOOD_PERCENTAGE = 0.15D;
		public static final double COBWEB_PERCENTAGE = 0.4D;

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random,
								BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			super.postProcess(level, structureManager, chunkGenerator, random, boundingBox, chunkPos, blockPos);
			BoundingBox curBoundingBox = this.getBoundingBox();
			for(int x = 0; x < curBoundingBox.getXSpan(); ++x) {
				for(int z = 0; z < curBoundingBox.getZSpan(); ++z) {
					for(int y = 0; y < curBoundingBox.getYSpan(); ++y) {
						BlockState blockstate = this.getBlock(level, x, y, z, boundingBox);
						if(isBricks(blockstate)) {
							if (random.nextDouble() < BROKEN_BRICKS_PERCENTAGE) {
								if(random.nextDouble() < COBWEB_PERCENTAGE) {
									this.placeBlock(level, Blocks.COBWEB.defaultBlockState(), x, y, z, boundingBox);
								} else {
									this.placeBlock(level, Blocks.AIR.defaultBlockState(), x, y, z, boundingBox);
								}
							}
						} else if(isWood(blockstate)) {
							if (random.nextDouble() < BROKEN_WOOD_PERCENTAGE) {
								if(random.nextDouble() < COBWEB_PERCENTAGE) {
									this.placeBlock(level, Blocks.COBWEB.defaultBlockState(), x, y, z, boundingBox);
								} else {
									this.placeBlock(level, Blocks.AIR.defaultBlockState(), x, y, z, boundingBox);
								}
							}
						}
					}
				}
			}
		}

		private static boolean isBricks(BlockState blockstate) {
			return blockstate.is(Blocks.BRICKS) || blockstate.is(Blocks.BRICK_SLAB) || blockstate.is(Blocks.BRICK_STAIRS) || blockstate.is(Blocks.BRICK_WALL);
		}

		private static boolean isWood(BlockState blockstate) {
			return blockstate.is(Blocks.MANGROVE_PLANKS) || blockstate.is(Blocks.MANGROVE_STAIRS) || blockstate.is(Blocks.MANGROVE_SLAB) || blockstate.is(Blocks.MANGROVE_TRAPDOOR) || blockstate.is(Blocks.MANGROVE_LOG);
		}
	}
}
