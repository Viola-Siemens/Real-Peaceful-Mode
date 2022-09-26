package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMPlacedFeatures {
	public static final Holder<PlacedFeature> ALUMINUM_ORE = register(
			"aluminum_ore", RPMConfiguredFeatures.ALUMINUM_ORE, commonOrePlacement(
					4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64))
			)
	);
	public static final Holder<PlacedFeature> SURFACE_ALUMINUM_ORE = register(
			"surface_aluminum_ore", RPMConfiguredFeatures.ALUMINUM_ORE_BURIED, commonOrePlacement(
					2, HeightRangePlacement.uniform(VerticalAnchor.absolute(16), VerticalAnchor.absolute(128))
			)
	);
	public static final Holder<PlacedFeature> SILVER_ORE = register(
			"silver_ore", RPMConfiguredFeatures.SILVER_ORE, commonOrePlacement(
					4, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(32))
			)
	);
	public static final Holder<PlacedFeature> INFERNO_DEBRIS_ORE = register(
			"inferno_debris_ore", RPMConfiguredFeatures.INFERNO_DEBRIS_ORE, List.of(
					InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.absolute(8), VerticalAnchor.absolute(40)), BiomeFilter.biome()
			)
	);
	public static final Holder<PlacedFeature> MANGANESE_ORE = register(
			"manganese_ore", RPMConfiguredFeatures.MANGANESE_ORE, commonOrePlacement(
					5, HeightRangePlacement.uniform(VerticalAnchor.absolute(16), VerticalAnchor.absolute(80))
			)
	);

	private RPMPlacedFeatures() {}

	private static Holder<PlacedFeature> register(String id, Holder<? extends ConfiguredFeature<?, ?>> cf, List<PlacementModifier> modifiers) {
		return BuiltinRegistries.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(MODID, id), new PlacedFeature(Holder.hackyErase(cf), List.copyOf(modifiers)));
	}


	private static List<PlacementModifier> orePlacement(PlacementModifier countModifier, PlacementModifier placementModifier) {
		return List.of(countModifier, InSquarePlacement.spread(), placementModifier, BiomeFilter.biome());
	}

	private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier placementModifier) {
		return orePlacement(CountPlacement.of(count), placementModifier);
	}
}
