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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class HuskWellPieces {
	private static final ResourceLocation HUSK_WELL = new ResourceLocation(MODID, "husk_well/husk_well");

	public static void addPieces(StructureTemplateManager structureManager, BlockPos pos, Rotation rotation, StructurePieceAccessor pieces) {
		pieces.addPiece(new HuskWellPiece(structureManager, HUSK_WELL, pos, rotation));
	}

	public static class HuskWellPiece extends TemplateStructurePiece {
		public HuskWellPiece(StructureTemplateManager structureManager, ResourceLocation location, BlockPos pos, Rotation rotation) {
			super(RPMStructurePieceTypes.HUSK_WELL_TYPE, 0, structureManager, location, location.toString(), makeSettings(rotation), pos.offset(-5, 0, -3));
		}

		public HuskWellPiece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(RPMStructurePieceTypes.HUSK_WELL_TYPE, tag, context.structureTemplateManager(), (location) -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
		}


		private static StructurePlaceSettings makeSettings(Rotation rotation) {
			return (new StructurePlaceSettings())
					.setRotation(rotation)
					.setMirror(Mirror.LEFT_RIGHT)
					.setRotationPivot(new BlockPos(5, 3, 3))
					.addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
		}


		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
			super.addAdditionalSaveData(context, tag);
			tag.putString("Rot", this.placeSettings.getRotation().name());
		}

		public static final double SUSPICIOUS_PERCENTAGE = 0.01D;

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random,
								BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			super.postProcess(level, structureManager, chunkGenerator, random, boundingBox, chunkPos, blockPos);
			BoundingBox curBoundingBox = this.getBoundingBox();
			for(int x = 1; x < curBoundingBox.getXSpan() - 1; ++x) {
				for(int z = 1; z < curBoundingBox.getZSpan() - 1; ++z) {
					for(int y = 1; y < curBoundingBox.getYSpan() - 3; ++y) {
						BlockState blockstate = this.getBlock(level, x, y, z, boundingBox);
						if(random.nextDouble() < SUSPICIOUS_PERCENTAGE && blockstate.is(Blocks.SAND)) {
							BlockState newBlock = Blocks.SUSPICIOUS_SAND.defaultBlockState();
							this.placeBlock(level, newBlock, x, y, z, boundingBox);
							BlockPos pos = this.getWorldPos(x, y, z);
							level.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK).ifPresent(brushableBlockEntity ->
									brushableBlockEntity.setLootTable(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, pos.asLong()));
						}
					}
				}
			}
		}

		@Override
		protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox sbb) {
		}
	}
}
