package com.hexagram2021.real_peaceful_mode.common.world.structures.pieces;

import com.hexagram2021.real_peaceful_mode.common.register.RPMStructurePieceTypes;
import com.hexagram2021.real_peaceful_mode.common.world.structures.HelplessGuardSlimeFeature;
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

public class HelplessGuardSlimePieces {
	public static void addPieces(StructureTemplateManager structureManager, BlockPos pos, Rotation rotation, StructurePieceAccessor pieces, HelplessGuardSlimeFeature.Variant variant) {
		pieces.addPiece(new HelplessGuardSlimePiece(structureManager, variant.getTemplate(), pos, rotation));
	}

	public static class HelplessGuardSlimePiece extends TemplateStructurePiece {
		public HelplessGuardSlimePiece(StructureTemplateManager structureManager, ResourceLocation location, BlockPos pos, Rotation rotation) {
			super(RPMStructurePieceTypes.HELPLESS_GUARD_SLIME_TYPE, 0, structureManager, location, location.toString(), makeSettings(rotation), pos.offset(-1, 0, -1));
		}

		public HelplessGuardSlimePiece(StructurePieceSerializationContext context, CompoundTag tag) {
			super(RPMStructurePieceTypes.HELPLESS_GUARD_SLIME_TYPE, tag, context.structureTemplateManager(), (location) -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
		}


		private static StructurePlaceSettings makeSettings(Rotation rotation) {
			return (new StructurePlaceSettings())
					.setRotation(rotation)
					.setMirror(Mirror.LEFT_RIGHT)
					.setRotationPivot(new BlockPos(3, 1, 3))
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
