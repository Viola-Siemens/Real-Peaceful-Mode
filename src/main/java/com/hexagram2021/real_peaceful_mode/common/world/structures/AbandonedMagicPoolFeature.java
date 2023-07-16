package com.hexagram2021.real_peaceful_mode.common.world.structures;

import com.hexagram2021.real_peaceful_mode.common.register.RPMStructureTypes;
import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.AbandonedMagicPoolPieces;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class AbandonedMagicPoolFeature extends Structure {
	public static final Codec<AbandonedMagicPoolFeature> CODEC = simpleCodec(AbandonedMagicPoolFeature::new);

	public AbandonedMagicPoolFeature(StructureSettings settings) {
		super(settings);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		return Optional.of(new GenerationStub(context.chunkPos().getWorldPosition(), builder -> generatePieces(builder, context)));
	}

	private static void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
		BlockPos blockpos = new BlockPos(context.chunkPos().getMinBlockX(), 65, context.chunkPos().getMinBlockZ());
		Rotation rotation = Rotation.getRandom(context.random());
		AbandonedMagicPoolPieces.addPieces(context.structureTemplateManager(), blockpos, rotation, builder);
	}

	@Override
	public StructureType<?> type() {
		return RPMStructureTypes.ABANDONED_MAGIC_POOL.get();
	}
}