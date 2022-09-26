package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.RealPeacefulMode;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMBlocks {
	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static class OreBlocks {
		public static final BlockEntry<OreBlock> ALUMINUM_ORE = new BlockEntry<>(
				"aluminum_ore",
				() -> BlockBehaviour.Properties.copy(Blocks.IRON_ORE),
				OreBlock::new
		);
		public static final BlockEntry<OreBlock> DEEPSLATE_ALUMINUM_ORE = new BlockEntry<>(
				"deepslate_aluminum_ore",
				() -> BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE),
				OreBlock::new
		);
		public static final BlockEntry<OreBlock> SILVER_ORE = new BlockEntry<>(
				"silver_ore",
				() -> BlockBehaviour.Properties.copy(Blocks.GOLD_ORE),
				OreBlock::new
		);
		public static final BlockEntry<OreBlock> DEEPSLATE_SILVER_ORE = new BlockEntry<>(
				"deepslate_silver_ore",
				() -> BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_GOLD_ORE),
				OreBlock::new
		);
		public static final BlockEntry<OreBlock> MANGANESE_ORE = new BlockEntry<>(
				"manganese_ore",
				() -> BlockBehaviour.Properties.of(Material.STONE, MaterialColor.SAND).requiresCorrectToolForDrops().strength(3.0F, 6.0F),
				OreBlock::new
		);
		public static final BlockEntry<Block> INFERNO_DEBRIS = new BlockEntry<>(
				"inferno_debris",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_MAGENTA).requiresCorrectToolForDrops().strength(30.0F, 1200.0F).sound(SoundType.ANCIENT_DEBRIS),
				Block::new
		);

		public static final BlockEntry<Block> RAW_ALUMINUM_BLOCK = new BlockEntry<>(
				"raw_aluminum_block",
				() -> BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().strength(5.0F, 6.0F),
				Block::new
		);
		public static final BlockEntry<Block> RAW_SILVER_BLOCK = new BlockEntry<>(
				"raw_silver_block",
				() -> BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F, 6.0F),
				Block::new
		);
		public static final BlockEntry<Block> RAW_MANGANESE_BLOCK = new BlockEntry<>(
				"raw_manganese_block",
				() -> BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_PINK).requiresCorrectToolForDrops().strength(5.0F, 6.0F),
				Block::new
		);

		public static final BlockEntry<Block> ALUMINUM_BLOCK = new BlockEntry<>(
				"aluminum_block",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL),
				Block::new
		);
		public static final BlockEntry<Block> SILVER_BLOCK = new BlockEntry<>(
				"silver_block",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_GRAY).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL),
				Block::new
		);
		public static final BlockEntry<Block> MANGANESE_BLOCK = new BlockEntry<>(
				"manganese_block",
				() -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_PINK).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL),
				Block::new
		);

		private OreBlocks() {}

		public static void init() {
			RPMItems.REGISTER.register(ALUMINUM_ORE.getId().getPath(), () -> new BlockItem(ALUMINUM_ORE.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(DEEPSLATE_ALUMINUM_ORE.getId().getPath(), () -> new BlockItem(DEEPSLATE_ALUMINUM_ORE.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(SILVER_ORE.getId().getPath(), () -> new BlockItem(SILVER_ORE.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(DEEPSLATE_SILVER_ORE.getId().getPath(), () -> new BlockItem(DEEPSLATE_SILVER_ORE.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(MANGANESE_ORE.getId().getPath(), () -> new BlockItem(MANGANESE_ORE.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(INFERNO_DEBRIS.getId().getPath(), () -> new BlockItem(INFERNO_DEBRIS.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(RAW_ALUMINUM_BLOCK.getId().getPath(), () -> new BlockItem(RAW_ALUMINUM_BLOCK.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(RAW_SILVER_BLOCK.getId().getPath(), () -> new BlockItem(RAW_SILVER_BLOCK.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(RAW_MANGANESE_BLOCK.getId().getPath(), () -> new BlockItem(RAW_MANGANESE_BLOCK.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(ALUMINUM_BLOCK.getId().getPath(), () -> new BlockItem(ALUMINUM_BLOCK.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(SILVER_BLOCK.getId().getPath(), () -> new BlockItem(SILVER_BLOCK.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
			RPMItems.REGISTER.register(MANGANESE_BLOCK.getId().getPath(), () -> new BlockItem(MANGANESE_BLOCK.get(), new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)));
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

		public static BlockEntry<Block> simple(String name, Supplier<BlockBehaviour.Properties> properties, Consumer<Block> extra) {
			return new BlockEntry<>(name, properties, (p) -> Util.make(new Block(p), extra));
		}

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
