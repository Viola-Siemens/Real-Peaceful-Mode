package com.hexagram2021.real_peaceful_mode.common.world.structures;

import com.hexagram2021.real_peaceful_mode.common.register.RPMStructureTypes;
import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.SlimeMazePieces;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;
import java.util.Optional;

public class SlimeMazeFeature extends Structure {
	public static final Codec<SlimeMazeFeature> CODEC = simpleCodec(SlimeMazeFeature::new);

	public SlimeMazeFeature(StructureSettings settings) {
		super(settings);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		BlockPos centerOfChunk = new BlockPos(context.chunkPos().getMinBlockX() + 10, 0, context.chunkPos().getMinBlockZ() + 14);
		return Optional.of(new GenerationStub(centerOfChunk, builder -> generatePieces(builder, centerOfChunk, context)));
	}

	private static void generatePieces(StructurePiecesBuilder builder, BlockPos centerOfChunk, GenerationContext context) {
		BlockPos blockpos = new BlockPos(
				centerOfChunk.getX(),
				context.chunkGenerator().getBaseHeight(
						centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()
				),
				centerOfChunk.getZ()
		);
		SlimeMazePieces.SlimeMazeEntrancePiece entrance = new SlimeMazePieces.SlimeMazeEntrancePiece(
				context.random(), blockpos.getX(), blockpos.getY() - 1, blockpos.getZ()
		);
		builder.addPiece(entrance);
		entrance.addChildren(entrance, builder, context.random());
		List<StructurePiece> list = entrance.pendingChildren;

		while(!list.isEmpty()) {
			int rank = context.random().nextInt(list.size());
			StructurePiece piece = list.remove(rank);
			piece.addChildren(entrance, builder, context.random());
		}
	}

	@Override
	public StructureType<SlimeMazeFeature> type() {
		return RPMStructureTypes.SLIME_MAZE.get();
	}
}
