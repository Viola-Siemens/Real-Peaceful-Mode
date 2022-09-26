package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.Tags;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMConfiguredFeatures {
	public static final RuleTest END_STONE_ORE_REPLACEABLES = new TagMatchTest(Tags.Blocks.END_STONES);
	public static final List<OreConfiguration.TargetBlockState> ORE_ALUMINUM_TARGET_LIST = List.of(
			OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, RPMBlocks.OreBlocks.ALUMINUM_ORE.defaultBlockState()),
			OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, RPMBlocks.OreBlocks.DEEPSLATE_ALUMINUM_ORE.defaultBlockState())
	);
	public static final List<OreConfiguration.TargetBlockState> ORE_SILVER_TARGET_LIST = List.of(
			OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, RPMBlocks.OreBlocks.SILVER_ORE.defaultBlockState()),
			OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, RPMBlocks.OreBlocks.DEEPSLATE_SILVER_ORE.defaultBlockState())
	);
	public static final List<OreConfiguration.TargetBlockState> ORE_MANGANESE_TARGET_LIST = List.of(
			OreConfiguration.target(END_STONE_ORE_REPLACEABLES, RPMBlocks.OreBlocks.MANGANESE_ORE.defaultBlockState())
	);

	public static final Holder<ConfiguredFeature<?, ?>> ALUMINUM_ORE = register(
			"aluminum_ore", Feature.ORE, new OreConfiguration(ORE_ALUMINUM_TARGET_LIST, 8)
	);
	public static final Holder<ConfiguredFeature<?, ?>> ALUMINUM_ORE_BURIED = register(
			"aluminum_ore_buried", Feature.ORE, new OreConfiguration(ORE_ALUMINUM_TARGET_LIST, 8, 0.5F)
	);
	public static final Holder<ConfiguredFeature<?, ?>> SILVER_ORE = register(
			"silver_ore", Feature.ORE, new OreConfiguration(ORE_SILVER_TARGET_LIST, 8)
	);
	public static final Holder<ConfiguredFeature<?, ?>> MANGANESE_ORE = register(
			"manganese_ore", Feature.ORE, new OreConfiguration(ORE_MANGANESE_TARGET_LIST, 4)
	);
	public static final Holder<ConfiguredFeature<?, ?>> INFERNO_DEBRIS_ORE = register(
			"inferno_debris_ore", Feature.SCATTERED_ORE,
			new OreConfiguration(OreFeatures.NETHER_ORE_REPLACEABLES, RPMBlocks.OreBlocks.INFERNO_DEBRIS.defaultBlockState(), 3, 1.0F)
	);

	private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(String id, F f, FC fc) {
		return BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, new ResourceLocation(MODID, id), new ConfiguredFeature<>(f, fc));
	}
}
