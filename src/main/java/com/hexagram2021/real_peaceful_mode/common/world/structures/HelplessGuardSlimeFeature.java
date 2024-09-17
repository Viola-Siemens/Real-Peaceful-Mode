package com.hexagram2021.real_peaceful_mode.common.world.structures;

import com.hexagram2021.real_peaceful_mode.common.register.RPMStructureTypes;
import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.HelplessGuardSlimePieces;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class HelplessGuardSlimeFeature extends Structure {
	public static final MapCodec<HelplessGuardSlimeFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			settingsCodec(instance), Variant.CODEC.fieldOf("variant").forGetter(structure -> structure.variant)
	).apply(instance, HelplessGuardSlimeFeature::new));

	private final Variant variant;

	public HelplessGuardSlimeFeature(StructureSettings settings, Variant variant) {
		super(settings);
		this.variant = variant;
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		return Optional.of(new GenerationStub(context.chunkPos().getWorldPosition(), builder -> generatePieces(builder, context, this.variant)));
	}

	private static void generatePieces(StructurePiecesBuilder builder, GenerationContext context, Variant variant) {
		BlockPos centerOfChunk = new BlockPos(context.chunkPos().getMinBlockX() + 9, 0, context.chunkPos().getMinBlockZ() + 9);
		BlockPos blockpos = new BlockPos(
				centerOfChunk.getX(),
				context.chunkGenerator().getBaseHeight(
						centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()
				),
				centerOfChunk.getZ()
		);
		Rotation rotation = Rotation.getRandom(context.random());
		HelplessGuardSlimePieces.addPieces(context.structureTemplateManager(), blockpos, rotation, builder, variant);
	}

	@Override
	public StructureType<HelplessGuardSlimeFeature> type() {
		return RPMStructureTypes.HELPLESS_GUARD_SLIME.get();
	}

	public enum Variant implements StringRepresentable {
		SWAMP("swamp", ResourceLocation.fromNamespaceAndPath(MODID, "guard_slime/helpless_guard_slime_swamp")),
		MANGROVE("mangrove", ResourceLocation.fromNamespaceAndPath(MODID, "guard_slime/helpless_guard_slime_mangrove"));

		public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

		private final String name;
		private final ResourceLocation template;

		Variant(String name, ResourceLocation template) {
			this.name = name;
			this.template = template;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		public ResourceLocation getTemplate() {
			return this.template;
		}
	}
}
