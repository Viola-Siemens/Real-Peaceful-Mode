package com.hexagram2021.real_peaceful_mode.common.world.structures.pieces;

import com.hexagram2021.real_peaceful_mode.common.entity.misc.TinyFireballEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
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

public class PharaohAltarPieces {
	private static final ResourceLocation PHARAOH_ALTAR = new ResourceLocation(MODID, "mission/pharaoh_altar");

	public static void addPieces(StructureTemplateManager structureManager, BlockPos pos, Rotation rotation, StructurePieceAccessor pieces) {
		pieces.addPiece(new PharaohAltarPiece(structureManager, PHARAOH_ALTAR, pos, rotation));
	}

	public static class PharaohAltarPiece extends TemplateStructurePiece {
		public PharaohAltarPiece(StructureTemplateManager structureManager, ResourceLocation location, BlockPos pos, Rotation rotation) {
			super(RPMStructurePieceTypes.PHARAOH_ALTAR_TYPE, 0, structureManager, location, location.toString(), makeSettings(rotation), pos.offset(-8, 0, -8));
		}

		public PharaohAltarPiece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(RPMStructurePieceTypes.PHARAOH_ALTAR_TYPE, tag, context.structureTemplateManager(), (location) -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
		}


		private static StructurePlaceSettings makeSettings(Rotation rotation) {
			return new StructurePlaceSettings()
					.setRotation(rotation)
					.setMirror(Mirror.LEFT_RIGHT)
					.setRotationPivot(new BlockPos(8, 0, 8))
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

		public static final double SILT_PERCENTAGE = 0.15D;

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random,
								BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			super.postProcess(level, structureManager, chunkGenerator, random, boundingBox, chunkPos, blockPos);
			BoundingBox curBoundingBox = this.getBoundingBox();
			for(int x = 0; x < curBoundingBox.getXSpan(); ++x) {
				for(int z = 0; z < curBoundingBox.getZSpan(); ++z) {
					for(int y = 0; y < curBoundingBox.getYSpan(); ++y) {
						BlockState blockstate = this.getBlock(level, x, y, z, boundingBox);
						if(random.nextDouble() < SILT_PERCENTAGE) {
							silt(blockstate, level, x, y, z, boundingBox);
						}
					}
				}
			}
		}

		private void silt(BlockState blockstate, WorldGenLevel level, int x, int y, int z, BoundingBox boundingBox) {
			BlockState newBlock = TinyFireballEntity.getSiltedBlockState(blockstate);
			if(newBlock != null) {
				this.placeBlock(level, newBlock, x, y, z, boundingBox);
			}
		}
	}
}
