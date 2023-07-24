package com.hexagram2021.real_peaceful_mode.common.world.structures.pieces;

import com.hexagram2021.real_peaceful_mode.common.register.RPMStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class PinkCreeperPieces {
	private static final ResourceLocation PINK_CREEPER = new ResourceLocation(MODID, "pink_creeper/pink_creeper");

	public static void addPieces(StructureTemplateManager structureManager, BlockPos pos, Rotation rotation, StructurePieceAccessor pieces) {
		pieces.addPiece(new PinkCreeperPiece(structureManager, PINK_CREEPER, pos, rotation));
	}

	public static class PinkCreeperPiece extends TemplateStructurePiece {
		public PinkCreeperPiece(StructureTemplateManager structureManager, ResourceLocation location, BlockPos pos, Rotation rotation) {
			super(RPMStructurePieceTypes.PINK_CREEPER_TYPE, 0, structureManager, location, location.toString(), makeSettings(rotation), pos.offset(1, 0, 1));
		}

		public PinkCreeperPiece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(RPMStructurePieceTypes.PINK_CREEPER_TYPE, tag, context.structureTemplateManager(), (location) -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
		}


		private static StructurePlaceSettings makeSettings(Rotation rotation) {
			return (new StructurePlaceSettings())
					.setRotation(rotation)
					.setMirror(Mirror.LEFT_RIGHT)
					.setRotationPivot(new BlockPos(1, 1, 1))
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
	}
}
