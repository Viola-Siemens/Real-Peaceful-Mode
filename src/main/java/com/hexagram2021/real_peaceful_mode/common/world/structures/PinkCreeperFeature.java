package com.hexagram2021.real_peaceful_mode.common.world.structures;

import com.hexagram2021.real_peaceful_mode.common.register.RPMStructureTypes;
import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.PinkCreeperPieces;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class PinkCreeperFeature extends Structure {
	public static final Codec<PinkCreeperFeature> CODEC = simpleCodec(PinkCreeperFeature::new);

	public PinkCreeperFeature(StructureSettings settings) {
		super(settings);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		return Optional.of(new GenerationStub(context.chunkPos().getWorldPosition(), builder -> generatePieces(builder, context)));
	}

	private static void generatePieces(StructurePiecesBuilder builder, GenerationContext context) {
		BlockPos centerOfChunk = new BlockPos(context.chunkPos().getMinBlockX() + 9, 0, context.chunkPos().getMinBlockZ() + 9);
		BlockPos blockpos = new BlockPos(
				centerOfChunk.getX(),
				context.chunkGenerator().getBaseHeight(
						centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()
				),
				centerOfChunk.getZ()
		);
		Rotation rotation = Rotation.getRandom(context.random());
		PinkCreeperPieces.addPieces(context.structureTemplateManager(), blockpos, rotation, builder);
	}

	@Override
	public StructureType<?> type() {
		return RPMStructureTypes.PINK_CREEPER.get();
	}
}
