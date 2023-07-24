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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class ZombieFortPieces {
	private static final ResourceLocation ZOMBIE_FORT = new ResourceLocation(MODID, "mission/zombie_fort");

	public static void addPieces(StructureTemplateManager structureManager, BlockPos pos, Rotation rotation, StructurePieceAccessor pieces) {
		pieces.addPiece(new ZombieFortPiece(structureManager, ZOMBIE_FORT, pos, rotation));
	}

	public static class ZombieFortPiece extends TemplateStructurePiece {
		public ZombieFortPiece(StructureTemplateManager structureManager, ResourceLocation location, BlockPos pos, Rotation rotation) {
			super(RPMStructurePieceTypes.ZOMBIE_FORT_TYPE, 0, structureManager, location, location.toString(), makeSettings(rotation), pos.offset(-16, -1, -24));
		}

		public ZombieFortPiece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(RPMStructurePieceTypes.ZOMBIE_FORT_TYPE, tag, context.structureTemplateManager(), (location) -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
		}


		private static StructurePlaceSettings makeSettings(Rotation rotation) {
			return new StructurePlaceSettings()
					.setRotation(rotation)
					.setMirror(Mirror.LEFT_RIGHT)
					.setRotationPivot(new BlockPos(16, 1, 24))
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

		public static final double MOSSY_PERCENTAGE = 0.65D;
		public static final double COBWEB_PERCENTAGE = 0.1D;

		@Override
		public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random,
								BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			super.postProcess(level, structureManager, chunkGenerator, random, boundingBox, chunkPos, blockPos);
			BoundingBox curBoundingBox = this.getBoundingBox();
			for(int x = 0; x < curBoundingBox.getXSpan(); ++x) {
				for(int z = 0; z < curBoundingBox.getZSpan(); ++z) {
					for(int y = 0; y < curBoundingBox.getYSpan(); ++y) {
						BlockState blockstate = this.getBlock(level, x, y, z, boundingBox);
						if(random.nextDouble() < MOSSY_PERCENTAGE) {
							mossify(blockstate, level, x, y, z, boundingBox);
						}
						if((blockstate.is(Blocks.BARREL) || blockstate.is(Blocks.HAY_BLOCK)) && random.nextDouble() < COBWEB_PERCENTAGE) {
							this.placeBlock(level, Blocks.COBWEB.defaultBlockState(), x, y, z, boundingBox);
						}
					}
				}
			}
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		private void mossify(BlockState blockstate, WorldGenLevel level, int x, int y, int z, BoundingBox boundingBox) {
			BlockState newBlock = null;
			if (blockstate.is(Blocks.STONE_BRICKS)) {
				newBlock = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
			} else if (blockstate.is(Blocks.COBBLESTONE)) {
				newBlock = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
			} else if (blockstate.is(Blocks.STONE_BRICK_WALL)) {
				newBlock = Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState();
			} else if (blockstate.is(Blocks.COBBLESTONE_SLAB)) {
				newBlock = Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState();
			} else if (blockstate.is(Blocks.COBBLESTONE_STAIRS)) {
				newBlock = Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState();
			}
			if(newBlock != null) {
				for(Property property: blockstate.getProperties()) {
					if(newBlock.hasProperty(property)) {
						newBlock.setValue(property, blockstate.getValue(property));
					}
				}
				this.placeBlock(level, newBlock, x, y, z, boundingBox);
			}
		}
	}
}
