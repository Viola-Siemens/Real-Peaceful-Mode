package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMBlocks {
	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static class OreBlocks {
		public static final BlockEntry<DropExperienceBlock> ALUMINUM_ORE = new BlockEntry<>(
				"aluminum_ore",
				() -> BlockBehaviour.Properties.copy(Blocks.IRON_ORE),
				DropExperienceBlock::new
		);
		public static final BlockEntry<DropExperienceBlock> DEEPSLATE_ALUMINUM_ORE = new BlockEntry<>(
				"deepslate_aluminum_ore",
				() -> BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE),
				DropExperienceBlock::new
		);
		public static final BlockEntry<DropExperienceBlock> SILVER_ORE = new BlockEntry<>(
				"silver_ore",
				() -> BlockBehaviour.Properties.copy(Blocks.GOLD_ORE),
				DropExperienceBlock::new
		);
		public static final BlockEntry<DropExperienceBlock> DEEPSLATE_SILVER_ORE = new BlockEntry<>(
				"deepslate_silver_ore",
				() -> BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_GOLD_ORE),
				DropExperienceBlock::new
		);
		public static final BlockEntry<DropExperienceBlock> MANGANESE_ORE = new BlockEntry<>(
				"manganese_ore",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 6.0F),
				DropExperienceBlock::new
		);
		public static final BlockEntry<Block> INFERNO_DEBRIS = new BlockEntry<>(
				"inferno_debris",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).requiresCorrectToolForDrops().strength(30.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS),
				Block::new
		);

		public static final BlockEntry<Block> RAW_ALUMINUM_BLOCK = new BlockEntry<>(
				"raw_aluminum_block",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F),
				Block::new
		);
		public static final BlockEntry<Block> RAW_SILVER_BLOCK = new BlockEntry<>(
				"raw_silver_block",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F),
				Block::new
		);
		public static final BlockEntry<Block> RAW_MANGANESE_BLOCK = new BlockEntry<>(
				"raw_manganese_block",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F),
				Block::new
		);

		public static final BlockEntry<Block> ALUMINUM_BLOCK = new BlockEntry<>(
				"aluminum_block",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL),
				Block::new
		);
		public static final BlockEntry<Block> SILVER_BLOCK = new BlockEntry<>(
				"silver_block",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL),
				Block::new
		);
		public static final BlockEntry<Block> MANGANESE_BLOCK = new BlockEntry<>(
				"manganese_block",
				() -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PINK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL),
				Block::new
		);

		private OreBlocks() {}

		public static void init() {
			RPMItems.ItemEntry.register(ALUMINUM_ORE.getId().getPath(), () -> new BlockItem(ALUMINUM_ORE.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(DEEPSLATE_ALUMINUM_ORE.getId().getPath(), () -> new BlockItem(DEEPSLATE_ALUMINUM_ORE.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(SILVER_ORE.getId().getPath(), () -> new BlockItem(SILVER_ORE.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(DEEPSLATE_SILVER_ORE.getId().getPath(), () -> new BlockItem(DEEPSLATE_SILVER_ORE.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(MANGANESE_ORE.getId().getPath(), () -> new BlockItem(MANGANESE_ORE.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(INFERNO_DEBRIS.getId().getPath(), () -> new BlockItem(INFERNO_DEBRIS.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(RAW_ALUMINUM_BLOCK.getId().getPath(), () -> new BlockItem(RAW_ALUMINUM_BLOCK.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(RAW_SILVER_BLOCK.getId().getPath(), () -> new BlockItem(RAW_SILVER_BLOCK.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(RAW_MANGANESE_BLOCK.getId().getPath(), () -> new BlockItem(RAW_MANGANESE_BLOCK.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(ALUMINUM_BLOCK.getId().getPath(), () -> new BlockItem(ALUMINUM_BLOCK.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(SILVER_BLOCK.getId().getPath(), () -> new BlockItem(SILVER_BLOCK.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
			RPMItems.ItemEntry.register(MANGANESE_BLOCK.getId().getPath(), () -> new BlockItem(MANGANESE_BLOCK.get(), new Item.Properties()), RPMItems.ItemEntry.ItemGroupType.BUILDING_BLOCKS);
		}
	}

	private RPMBlocks() {}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		OreBlocks.init();
	}

	public static final class BlockEntry<T extends Block> implements Supplier<T>, ItemLike {
		private final RegistryObject<T> regObject;
		private final Supplier<BlockBehaviour.Properties> properties;

		public BlockEntry(String name, Supplier<BlockBehaviour.Properties> properties, Function<BlockBehaviour.Properties, T> make) {
			this.properties = properties;
			this.regObject = REGISTER.register(name, () -> make.apply(properties.get()));
		}

		public T get() {
			return this.regObject.get();
		}

		public BlockState defaultBlockState() {
			return this.get().defaultBlockState();
		}

		public ResourceLocation getId() {
			return this.regObject.getId();
		}

		public BlockBehaviour.Properties getProperties() {
			return this.properties.get();
		}

		@Nonnull
		public Item asItem() {
			return this.get().asItem();
		}
	}
}
