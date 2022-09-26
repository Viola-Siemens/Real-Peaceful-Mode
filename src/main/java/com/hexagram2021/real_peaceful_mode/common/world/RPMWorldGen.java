package com.hexagram2021.real_peaceful_mode.common.world;

import com.hexagram2021.real_peaceful_mode.common.register.RPMPlacedFeatures;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class RPMWorldGen {
	public static void biomeModification(final BiomeLoadingEvent event) {
		if(event.getCategory() == Biome.BiomeCategory.NETHER) {
			genNetherOres(event.getGeneration());
		} else if(event.getCategory() == Biome.BiomeCategory.THEEND) {
			genTheEndOres(event.getGeneration());
		} else {
			genOverworldOres(event.getGeneration());
		}
	}

	private static void genOverworldOres(BiomeGenerationSettingsBuilder builder) {
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RPMPlacedFeatures.ALUMINUM_ORE);
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RPMPlacedFeatures.SURFACE_ALUMINUM_ORE);
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RPMPlacedFeatures.SILVER_ORE);
	}

	private static void genNetherOres(BiomeGenerationSettingsBuilder builder) {
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RPMPlacedFeatures.INFERNO_DEBRIS_ORE);
	}

	private static void genTheEndOres(BiomeGenerationSettingsBuilder builder) {
		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RPMPlacedFeatures.MANGANESE_ORE);
	}
}
