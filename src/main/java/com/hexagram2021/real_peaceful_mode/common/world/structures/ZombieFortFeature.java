package com.hexagram2021.real_peaceful_mode.common.world.structures;

import com.hexagram2021.real_peaceful_mode.common.register.RPMStructureTypes;
import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.ZombieFortPieces;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class ZombieFortFeature extends Structure {
	public static final Codec<ZombieFortFeature> CODEC = simpleCodec(ZombieFortFeature::new);

	public ZombieFortFeature(Structure.StructureSettings settings) {
		super(settings);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, (builder) -> generatePieces(builder, context));
	}

	private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
		BlockPos centerOfChunk = new BlockPos(context.chunkPos().getMinBlockX() + 7, 0, context.chunkPos().getMinBlockZ() + 7);
		BlockPos blockpos = new BlockPos(
				centerOfChunk.getX(),
				context.chunkGenerator().getBaseHeight(
						centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()
				),
				centerOfChunk.getZ()
		);
		Rotation rotation = Rotation.getRandom(context.random());
		ZombieFortPieces.addPieces(context.structureTemplateManager(), blockpos, rotation, builder);
	}

	@Override
	public StructureType<?> type() {
		return RPMStructureTypes.ZOMBIE_FORT.get();
	}
}
