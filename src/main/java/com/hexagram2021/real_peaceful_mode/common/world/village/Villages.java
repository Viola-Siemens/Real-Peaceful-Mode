package com.hexagram2021.real_peaceful_mode.common.world.village;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.register.RPMSounds;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.hexagram2021.real_peaceful_mode.mixin.HeroGiftsTaskAccess;
import com.hexagram2021.real_peaceful_mode.mixin.StructureTemplatePoolAccess;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;
import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;
import static com.hexagram2021.real_peaceful_mode.common.world.village.RPMTrades.*;

public class Villages {
	public static final ResourceLocation SENIOR = new ResourceLocation(MODID, "senior");
	public static final ResourceLocation BOTANIST = new ResourceLocation(MODID, "botanist");
	public static final ResourceLocation PLUMBER = new ResourceLocation(MODID, "plumber");

	public static void init() {
		HeroGiftsTaskAccess.getGifts().put(Registers.PROF_SENIOR.get(), new ResourceLocation(MODID, "gameplay/hero_of_the_village/senior_gift"));
		HeroGiftsTaskAccess.getGifts().put(Registers.PROF_BOTANIST.get(), new ResourceLocation(MODID, "gameplay/hero_of_the_village/botanist_gift"));
		HeroGiftsTaskAccess.getGifts().put(Registers.PROF_PLUMBER.get(), new ResourceLocation(MODID, "gameplay/hero_of_the_village/plumber_gift"));
	}

	public static void addAllStructuresToPool(RegistryAccess registryAccess) {
		addToPool(new ResourceLocation("village/desert/houses"), new ResourceLocation(MODID, "village/desert/houses/desert_refinery_1"), 4, null, registryAccess);
		addToPool(new ResourceLocation("village/plains/houses"), new ResourceLocation(MODID, "village/plains/houses/plains_refinery_1"), 4, ProcessorLists.MOSSIFY_10_PERCENT, registryAccess);
		addToPool(new ResourceLocation("village/savanna/houses"), new ResourceLocation(MODID, "village/savanna/houses/savanna_refinery_1"), 4, null, registryAccess);
		addToPool(new ResourceLocation("village/snowy/houses"), new ResourceLocation(MODID, "village/snowy/houses/snowy_refinery_1"), 4, null, registryAccess);
		addToPool(new ResourceLocation("village/taiga/houses"), new ResourceLocation(MODID, "village/taiga/houses/taiga_refinery_1"), 4, null, registryAccess);
		addToPool(new ResourceLocation("village/desert/houses"), new ResourceLocation(MODID, "village/desert/houses/desert_botanist_1"), 4, null, registryAccess);
		addToPool(new ResourceLocation("village/plains/houses"), new ResourceLocation(MODID, "village/plains/houses/plains_botanist_1"), 4, null, registryAccess);
		addToPool(new ResourceLocation("village/savanna/houses"), new ResourceLocation(MODID, "village/savanna/houses/savanna_botanist_1"), 4, null, registryAccess);
		addToPool(new ResourceLocation("village/snowy/houses"), new ResourceLocation(MODID, "village/snowy/houses/snowy_botanist_1"), 4, null, registryAccess);
		addToPool(new ResourceLocation("village/taiga/houses"), new ResourceLocation(MODID, "village/taiga/houses/taiga_botanist_1"), 4, null, registryAccess);
	}

	@SuppressWarnings("SameParameterValue")
	private static void addToPool(ResourceLocation poolName, ResourceLocation toAdd, int weight, @Nullable ResourceKey<StructureProcessorList> processorList, RegistryAccess registryAccess) {
		Registry<StructureTemplatePool> registry = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL);
		StructureTemplatePool structureTemplatePool = registry.get(poolName);
		if(structureTemplatePool == null) {
			RPMLogger.error("Ignored empty structure template pool: " + poolName);
			return;
		}
		StructureTemplatePoolAccess pool = (StructureTemplatePoolAccess)structureTemplatePool;
		List<Pair<StructurePoolElement, Integer>> rawTemplates = pool.getRawTemplates() instanceof ArrayList ?
				pool.getRawTemplates() : new ArrayList<>(pool.getRawTemplates());

		SinglePoolElement addedElement;
		if(processorList == null) {
			addedElement = SinglePoolElement.single(toAdd.toString()).apply(StructureTemplatePool.Projection.RIGID);
		} else {
			HolderGetter<StructureProcessorList> processorListHolder = registryAccess.lookupOrThrow(Registries.PROCESSOR_LIST);
			addedElement = SinglePoolElement.single(toAdd.toString(), processorListHolder.getOrThrow(processorList)).apply(StructureTemplatePool.Projection.RIGID);
		}
		rawTemplates.add(Pair.of(addedElement, weight));
		pool.getTemplates().add(addedElement);

		pool.setRawTemplates(rawTemplates);
	}

	public static class Registers {
		public static final DeferredRegister<PoiType> POINTS_OF_INTEREST = DeferredRegister.create(ForgeRegistries.POI_TYPES, MODID);
		public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, MODID);

		public static final RegistryObject<PoiType> POI_REFINEMENT_TABLE = POINTS_OF_INTEREST.register(
				"refinement_table", () -> createPOI(assembleStates(RPMBlocks.WorkStation.REFINEMENT_TABLE.get()))
		);
		public static final RegistryObject<PoiType> POI_CULTURE_TABLE = POINTS_OF_INTEREST.register(
				"culture_table", () -> createPOI(assembleStates(RPMBlocks.WorkStation.CULTURE_TABLE.get()))
		);
		public static final RegistryObject<PoiType> POI_PURIFIER = POINTS_OF_INTEREST.register(
				"purifier", () -> createPOI(assembleStates(RPMBlocks.WorkStation.PURIFIER.get()))
		);

		public static final RegistryObject<VillagerProfession> PROF_SENIOR = PROFESSIONS.register(
				"senior", () -> createProf(SENIOR, POI_REFINEMENT_TABLE::getKey, RPMSounds.VILLAGER_WORK_SENIOR)
		);
		public static final RegistryObject<VillagerProfession> PROF_BOTANIST = PROFESSIONS.register(
				"botanist", () -> createProf(BOTANIST, POI_CULTURE_TABLE::getKey, RPMSounds.VILLAGER_WORK_BOTANIST)
		);
		public static final RegistryObject<VillagerProfession> PROF_PLUMBER = PROFESSIONS.register(
				"plumber", () -> createProf(PLUMBER, POI_PURIFIER::getKey, RPMSounds.VILLAGER_WORK_PLUMBER)
		);

		public static void init(IEventBus bus) {
			POINTS_OF_INTEREST.register(bus);
			PROFESSIONS.register(bus);
		}
	}

	private static Collection<BlockState> assembleStates(Block block) {
		return block.getStateDefinition().getPossibleStates();
	}

	private static PoiType createPOI(Collection<BlockState> block) {
		return new PoiType(ImmutableSet.copyOf(block), 1, 1);
	}

	@SuppressWarnings("SameParameterValue")
	private static VillagerProfession createProf(ResourceLocation name, Supplier<ResourceKey<PoiType>> poi, SoundEvent sound) {
		ResourceKey<PoiType> poiName = poi.get();
		return new VillagerProfession(
				name.toString(),
				(p) -> p.is(poiName),
				(p) -> p.is(poiName),
				ImmutableSet.of(),
				ImmutableSet.of(),
				sound
		);
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class Events {
		@SubscribeEvent
		public static void registerTrades(VillagerTradesEvent event) {
			Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

			ResourceLocation currentVillagerProfession = getRegistryName(event.getType());
			if(SENIOR.equals(currentVillagerProfession)) {
				trades.get(1).add(new RPMTrades.WrittenBookForBead(
						Component.translatable("book.real_peaceful_mode.zombie.title"),
						Component.translatable("entity.minecraft.villager.real_peaceful_mode.senior"),
						RPMItems.SpiritBeads.ZOMBIE_SPIRIT_BEAD, 1,
						ONLY_SUPPLY_ONCE,
						XP_LEVEL_1_SELL,
						Component.translatable("book.real_peaceful_mode.zombie.content1"),
						Component.translatable("book.real_peaceful_mode.zombie.content2"),
						Component.translatable("book.real_peaceful_mode.zombie.content3")
				));
				trades.get(1).add(new RPMTrades.EmeraldForItems(Items.ROTTEN_FLESH, 32, 1, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY));
				trades.get(2).add(new RPMTrades.WrittenBookForBead(
						Component.translatable("book.real_peaceful_mode.skeleton.title"),
						Component.translatable("entity.minecraft.villager.real_peaceful_mode.senior"),
						RPMItems.SpiritBeads.SKELETON_SPIRIT_BEAD, 1,
						ONLY_SUPPLY_ONCE,
						XP_LEVEL_2_SELL,
						Component.translatable("book.real_peaceful_mode.skeleton.content1"),
						Component.translatable("book.real_peaceful_mode.skeleton.content2"),
						Component.translatable("book.real_peaceful_mode.skeleton.content3")
				));
				trades.get(2).add(new RPMTrades.EmeraldForItems(Items.BONE, 16, 1, COMMON_ITEMS_SUPPLY, XP_LEVEL_2_BUY));
				trades.get(3).add(new RPMTrades.EmeraldForItems(Items.GUNPOWDER, 16, 1, DEFAULT_SUPPLY, XP_LEVEL_3_BUY));
				trades.get(3).add(new RPMTrades.EmeraldForItems(Items.SPIDER_EYE, 13, 1, DEFAULT_SUPPLY, XP_LEVEL_3_BUY));
				trades.get(4).add(new RPMTrades.WrittenBookForBead(
						Component.translatable("book.real_peaceful_mode.guardian.title"),
						Component.translatable("entity.minecraft.villager.real_peaceful_mode.senior"),
						RPMItems.SpiritBeads.SLIME_SPIRIT_BEAD, 1,
						ONLY_SUPPLY_ONCE,
						XP_LEVEL_4_SELL,
						Component.translatable("book.real_peaceful_mode.guardian.content1"),
						Component.translatable("book.real_peaceful_mode.guardian.content2"),
						Component.translatable("book.real_peaceful_mode.guardian.content3")
				));
				trades.get(4).add(new RPMTrades.EmeraldForItems(Items.SEA_LANTERN, 5, 1, COMMON_ITEMS_SUPPLY, XP_LEVEL_4_BUY));
				trades.get(5).add(new RPMTrades.ItemsForEmeralds(RPMBlocks.WorkStation.REFINEMENT_TABLE, 16, 1, ONLY_SUPPLY_ONCE, XP_LEVEL_5_TRADE));
			} else if(BOTANIST.equals(currentVillagerProfession)) {
				trades.get(1).add(new RPMTrades.WrittenBookForBead(
						Component.translatable("book.real_peaceful_mode.creeper.title"),
						Component.translatable("entity.minecraft.villager.real_peaceful_mode.botanist"),
						RPMItems.SpiritBeads.CREEPER_SPIRIT_BEAD, 1,
						ONLY_SUPPLY_ONCE,
						XP_LEVEL_1_SELL,
						Component.translatable("book.real_peaceful_mode.creeper.content1"),
						Component.translatable("book.real_peaceful_mode.creeper.content2"),
						Component.translatable("book.real_peaceful_mode.creeper.content3")
				));
				trades.get(1).add(new RPMTrades.EmeraldForItems(Items.BONE_BLOCK, 7, 1, DEFAULT_SUPPLY, XP_LEVEL_1_BUY));
				trades.get(2).add(new RPMTrades.EmeraldForItems(Items.ORANGE_TULIP, 15, 1, DEFAULT_SUPPLY, XP_LEVEL_2_BUY));
				trades.get(2).add(new RPMTrades.EmeraldForItems(Items.PINK_TULIP, 15, 1, DEFAULT_SUPPLY, XP_LEVEL_2_BUY));
				trades.get(2).add(new RPMTrades.EmeraldForItems(Items.RED_TULIP, 15, 1, DEFAULT_SUPPLY, XP_LEVEL_2_BUY));
				trades.get(2).add(new RPMTrades.EmeraldForItems(Items.WHITE_TULIP, 15, 1, DEFAULT_SUPPLY, XP_LEVEL_2_BUY));
				trades.get(3).add(new RPMTrades.ItemsForEmeralds(Items.DEAD_BUSH, 1, 3, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_3_SELL));
				trades.get(3).add(new RPMTrades.EmeraldForItems(Items.TORCHFLOWER, 13, 1, DEFAULT_SUPPLY, XP_LEVEL_3_BUY));
				trades.get(4).add(new RPMTrades.WrittenBookForBead(
						Component.translatable("book.real_peaceful_mode.slime.title"),
						Component.translatable("entity.minecraft.villager.real_peaceful_mode.botanist"),
						RPMItems.SpiritBeads.SLIME_SPIRIT_BEAD, 1,
						ONLY_SUPPLY_ONCE,
						XP_LEVEL_4_SELL,
						Component.translatable("book.real_peaceful_mode.slime.content1"),
						Component.translatable("book.real_peaceful_mode.slime.content2"),
						Component.translatable("book.real_peaceful_mode.slime.content3")
				));
				trades.get(4).add(new RPMTrades.EmeraldForItems(Items.SLIME_BALL, 24, 1, COMMON_ITEMS_SUPPLY, XP_LEVEL_4_BUY));
				trades.get(5).add(new RPMTrades.ItemsForEmeralds(Items.GRASS_BLOCK, 1, 4, DEFAULT_SUPPLY, XP_LEVEL_5_TRADE));
				trades.get(5).add(new RPMTrades.ItemsForEmeralds(Items.FLOWER_BANNER_PATTERN, 3, 1, UNCOMMON_ITEMS_SUPPLY, XP_LEVEL_5_TRADE));
			} else if(PLUMBER.equals(currentVillagerProfession)) {
				trades.get(1).add(new RPMTrades.WrittenBookForBead(
						Component.translatable("book.real_peaceful_mode.husk.title"),
						Component.translatable("entity.minecraft.villager.real_peaceful_mode.plumber"),
						RPMItems.SpiritBeads.HUSK_SPIRIT_BEAD, 1,
						ONLY_SUPPLY_ONCE,
						XP_LEVEL_1_SELL,
						Component.translatable("book.real_peaceful_mode.husk.content1"),
						Component.translatable("book.real_peaceful_mode.husk.content2"),
						Component.translatable("book.real_peaceful_mode.husk.content3")
				));
				trades.get(1).add(new RPMTrades.EmeraldForItems(Items.SAND, 24, 1, COMMON_ITEMS_SUPPLY, XP_LEVEL_1_BUY));
				trades.get(2).add(new RPMTrades.WrittenBookForBead(
						Component.translatable("book.real_peaceful_mode.drowned.title"),
						Component.translatable("entity.minecraft.villager.real_peaceful_mode.plumber"),
						RPMItems.SpiritBeads.DROWNED_SPIRIT_BEAD, 1,
						ONLY_SUPPLY_ONCE,
						XP_LEVEL_2_SELL,
						Component.translatable("book.real_peaceful_mode.drowned.content1"),
						Component.translatable("book.real_peaceful_mode.drowned.content2"),
						Component.translatable("book.real_peaceful_mode.drowned.content3")
				));
				trades.get(2).add(new RPMTrades.ItemsForEmeralds(Items.WATER_BUCKET, 2, 1, COMMON_ITEMS_SUPPLY, XP_LEVEL_2_SELL));
			}
		}
	}
}
