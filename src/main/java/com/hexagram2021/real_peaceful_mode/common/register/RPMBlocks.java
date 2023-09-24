package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.block.ContinuousSummonBlock;
import com.hexagram2021.real_peaceful_mode.common.block.CultureTableBlock;
import com.hexagram2021.real_peaceful_mode.common.block.DirtyWaterBlock;
import com.hexagram2021.real_peaceful_mode.common.block.SummonBlock;
import com.hexagram2021.real_peaceful_mode.common.block.skull.RPMSkullTypes;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;
import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public class RPMBlocks {
	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final class TechnicalBlocks {
		public static final BlockEntry<SummonBlock> SUMMON_BLOCK = new BlockEntry<>(
				"summon_block", () -> BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F)
				.noCollission().noLootTable()
				.isValidSpawn((blockState, level, blockPos, entityType) -> false)
				.noParticlesOnBreak().pushReaction(PushReaction.BLOCK), SummonBlock::new
		);
		public static final BlockEntry<ContinuousSummonBlock> CONTINUOUS_SUMMON_BLOCK = new BlockEntry<>(
				"continuous_summon_block", () -> BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F)
				.noCollission().noLootTable()
				.isValidSpawn((blockState, level, blockPos, entityType) -> false)
				.noParticlesOnBreak().pushReaction(PushReaction.BLOCK), ContinuousSummonBlock::new
		);

		public static final BlockEntry<DirtyWaterBlock> DIRTY_WATER = new BlockEntry<>(
				"dirty_water", () -> BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).strength(0.25F)
				.sound(SoundType.POWDER_SNOW).dynamicShape()
				.isRedstoneConductor((blockState, level, pos) -> false), DirtyWaterBlock::new
		);

		private TechnicalBlocks() {}

		private static void init() {
			RPMItems.ItemEntry.register(SUMMON_BLOCK.getId().getPath(), () -> new BlockItem(SUMMON_BLOCK.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CONTINUOUS_SUMMON_BLOCK.getId().getPath(), () -> new BlockItem(CONTINUOUS_SUMMON_BLOCK.get(), new Item.Properties()));
		}
	}

	public static final class WorkStation {
		public static final BlockEntry<Block> REFINEMENT_TABLE = new BlockEntry<>(
				"refinement_table", () -> BlockBehaviour.Properties.of()
				.mapColor(MapColor.COLOR_BLACK).strength(50.0F, 1200.0F).lightLevel(blockState -> 1)
				.noOcclusion().pushReaction(PushReaction.BLOCK), Block::new
		);
		public static final BlockEntry<CultureTableBlock> CULTURE_TABLE = new BlockEntry<>(
				"culture_table", () -> BlockBehaviour.Properties.of()
				.mapColor(MapColor.WOOD).strength(2.0F).lightLevel(blockState -> blockState.getValue(AbstractFurnaceBlock.LIT) ? 3 : 0)
				.noOcclusion().pushReaction(PushReaction.BLOCK), CultureTableBlock::new
		);
		public static final BlockEntry<Block> PURIFIER = new BlockEntry<>(
				"purifier", () -> BlockBehaviour.Properties.of()
				.mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0F)
				.noOcclusion().pushReaction(PushReaction.BLOCK), Block::new
		);

		private WorkStation() {}

		private static void init() {
			RPMItems.ItemEntry.register(REFINEMENT_TABLE.getId().getPath(), () -> new BlockItem(REFINEMENT_TABLE.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CULTURE_TABLE.getId().getPath(), () -> new BlockItem(CULTURE_TABLE.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(PURIFIER.getId().getPath(), () -> new BlockItem(PURIFIER.get(), new Item.Properties()));
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Decoration {
		private static String changeNameTo(String name, String postfix) {
			if(name.endsWith("_block")) {
				name = name.replaceAll("_block", postfix);
			} else if(name.endsWith("_bricks")) {
				name = name.replaceAll("_bricks", "_brick" + postfix);
			} else if(name.endsWith("_planks")) {
				name = name.replaceAll("_planks", postfix);
			} else {
				name = name + postfix;
			}
			return name;
		}
		private static <T extends Block> BlockEntry<StairBlock> registerStairs(BlockEntry<T> fullBlock) {
			String name = changeNameTo(fullBlock.getId().getPath(), "_stairs");
			return new BlockEntry<>(name, fullBlock::getProperties, p -> new StairBlock(fullBlock::defaultBlockState, p));
		}
		private static BlockEntry<StairBlock> registerStairs(Block fullBlock) {
			String name = changeNameTo(getRegistryName(fullBlock).getPath(), "_stairs");
			return new BlockEntry<>(name, () -> BlockBehaviour.Properties.copy(fullBlock), p -> new StairBlock(fullBlock::defaultBlockState, p));
		}

		private static <T extends Block> BlockEntry<SlabBlock> registerSlab(BlockEntry<T> fullBlock) {
			String name = changeNameTo(fullBlock.getId().getPath(), "_slab");
			return new BlockEntry<>(
					name, fullBlock::getProperties,
					p -> new SlabBlock(p.isSuffocating(
							(state, world, pos) ->
									fullBlock.defaultBlockState().isSuffocating(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
					).isRedstoneConductor(
							(state, world, pos) ->
									fullBlock.defaultBlockState().isRedstoneConductor(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
					))
			);
		}
		private static BlockEntry<SlabBlock> registerSlab(Block fullBlock) {
			String name = changeNameTo(getRegistryName(fullBlock).getPath(), "_slab");
			return new BlockEntry<>(
					name,
					() -> BlockBehaviour.Properties.copy(fullBlock),
					p -> new SlabBlock(
							p.isSuffocating(
									(state, world, pos) -> fullBlock.defaultBlockState().isSuffocating(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
							).isRedstoneConductor(
									(state, world, pos) -> fullBlock.defaultBlockState().isRedstoneConductor(world, pos) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE
							)
					)
			);
		}

		private static <T extends Block> BlockEntry<WallBlock> registerWall(BlockEntry<T> fullBlock) {
			String name = changeNameTo(fullBlock.getId().getPath(), "_wall");
			return new BlockEntry<>(name, fullBlock::getProperties, WallBlock::new);
		}
		private static BlockEntry<WallBlock> registerWall(Block fullBlock) {
			String name = changeNameTo(getRegistryName(fullBlock).getPath(), "_wall");
			return new BlockEntry<>(name, () -> BlockBehaviour.Properties.copy(fullBlock), WallBlock::new);
		}

		public static final BlockEntry<SkullBlock> DARK_ZOMBIE_KNIGHT_SKULL = new BlockEntry<>(
				"dark_zombie_knight_skull", () -> BlockBehaviour.Properties.of()
				.instrument(RPMNoteBlockInstruments.DARK_ZOMBIE_KNIGHT).strength(1.0F).pushReaction(PushReaction.DESTROY),
				props -> new SkullBlock(RPMSkullTypes.DARK_ZOMBIE_KNIGHT, props)
		);
		public static final BlockEntry<WallSkullBlock> DARK_ZOMBIE_KNIGHT_WALL_SKULL = new BlockEntry<>(
				"dark_zombie_knight_wall_skull", () -> BlockBehaviour.Properties.of()
				.instrument(RPMNoteBlockInstruments.DARK_ZOMBIE_KNIGHT).strength(1.0F).pushReaction(PushReaction.DESTROY),
				props -> new WallSkullBlock(RPMSkullTypes.DARK_ZOMBIE_KNIGHT, props)
		);
		
		public static final BlockEntry<Block> TUFF_BRICKS = new BlockEntry<>(
				"tuff_bricks", () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY)
				.instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F),
				Block::new
		);
		public static final BlockEntry<StairBlock> TUFF_BRICK_STAIR = registerStairs(TUFF_BRICKS);
		public static final BlockEntry<SlabBlock> TUFF_BRICK_SLAB = registerSlab(TUFF_BRICKS);
		public static final BlockEntry<WallBlock> TUFF_BRICK_WALL = registerWall(TUFF_BRICKS);

		public static final BlockEntry<Block> CRACKED_TUFF_BRICKS = new BlockEntry<>(
				"cracked_tuff_bricks", () -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY)
				.instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F),
				Block::new
		);
		public static final BlockEntry<StairBlock> CRACKED_TUFF_BRICK_STAIR = registerStairs(CRACKED_TUFF_BRICKS);
		public static final BlockEntry<SlabBlock> CRACKED_TUFF_BRICK_SLAB = registerSlab(CRACKED_TUFF_BRICKS);
		public static final BlockEntry<WallBlock> CRACKED_TUFF_BRICK_WALL = registerWall(CRACKED_TUFF_BRICKS);

		public static final BlockEntry<StairBlock> CALCITE_STAIRS = registerStairs(Blocks.CALCITE);
		public static final BlockEntry<SlabBlock> CALCITE_SLAB = registerSlab(Blocks.CALCITE);
		public static final BlockEntry<WallBlock> CALCITE_WALL = registerWall(Blocks.CALCITE);
		
		public static final BlockEntry<Block> POLISHED_CALCITE = new BlockEntry<>(
				"polished_calcite", () -> BlockBehaviour.Properties.copy(Blocks.CALCITE),
				Block::new
		);
		public static final BlockEntry<Block> CUT_CALCITE = new BlockEntry<>(
				"cut_calcite", () -> BlockBehaviour.Properties.copy(Blocks.CALCITE),
				Block::new
		);

		public static final BlockEntry<Block> SILTSTONE = new BlockEntry<>(
				"siltstone", () -> BlockBehaviour.Properties.copy(Blocks.SANDSTONE).mapColor(MapColor.DIRT),
				Block::new
		);
		public static final BlockEntry<StairBlock> SILTSTONE_STAIRS = registerStairs(SILTSTONE);
		public static final BlockEntry<SlabBlock> SILTSTONE_SLAB = registerSlab(SILTSTONE);
		public static final BlockEntry<WallBlock> SILTSTONE_WALL = registerWall(SILTSTONE);

		public static final BlockEntry<Block> SMOOTH_SILTSTONE = new BlockEntry<>(
				"smooth_siltstone", () -> BlockBehaviour.Properties.copy(Blocks.SANDSTONE).mapColor(MapColor.DIRT),
				Block::new
		);
		public static final BlockEntry<StairBlock> SMOOTH_SILTSTONE_STAIRS = registerStairs(SMOOTH_SILTSTONE);
		public static final BlockEntry<SlabBlock> SMOOTH_SILTSTONE_SLAB = registerSlab(SMOOTH_SILTSTONE);

		public static final BlockEntry<Block> INFESTED_GLOWING_CRYSTAL = new BlockEntry<>(
				"infested_glowing_crystal", () -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
				.instrument(NoteBlockInstrument.BASEDRUM).strength(-1.0F, 3600000.0F).noLootTable(),
				Block::new
		);

		private Decoration() {}

		private static void init() {
			RPMItems.ItemEntry.register(DARK_ZOMBIE_KNIGHT_SKULL.getId().getPath(), () -> new StandingAndWallBlockItem(
					DARK_ZOMBIE_KNIGHT_SKULL.get(), DARK_ZOMBIE_KNIGHT_WALL_SKULL.get(), new Item.Properties().rarity(Rarity.UNCOMMON), Direction.DOWN
			));
			RPMItems.ItemEntry.register(TUFF_BRICKS.getId().getPath(), () -> new BlockItem(TUFF_BRICKS.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(TUFF_BRICK_STAIR.getId().getPath(), () -> new BlockItem(TUFF_BRICK_STAIR.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(TUFF_BRICK_SLAB.getId().getPath(), () -> new BlockItem(TUFF_BRICK_SLAB.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(TUFF_BRICK_WALL.getId().getPath(), () -> new BlockItem(TUFF_BRICK_WALL.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CRACKED_TUFF_BRICKS.getId().getPath(), () -> new BlockItem(CRACKED_TUFF_BRICKS.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CRACKED_TUFF_BRICK_STAIR.getId().getPath(), () -> new BlockItem(CRACKED_TUFF_BRICK_STAIR.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CRACKED_TUFF_BRICK_SLAB.getId().getPath(), () -> new BlockItem(CRACKED_TUFF_BRICK_SLAB.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CRACKED_TUFF_BRICK_WALL.getId().getPath(), () -> new BlockItem(CRACKED_TUFF_BRICK_WALL.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CALCITE_STAIRS.getId().getPath(), () -> new BlockItem(CALCITE_STAIRS.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CALCITE_SLAB.getId().getPath(), () -> new BlockItem(CALCITE_SLAB.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CALCITE_WALL.getId().getPath(), () -> new BlockItem(CALCITE_WALL.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(POLISHED_CALCITE.getId().getPath(), () -> new BlockItem(POLISHED_CALCITE.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(CUT_CALCITE.getId().getPath(), () -> new BlockItem(CUT_CALCITE.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(SILTSTONE.getId().getPath(), () -> new BlockItem(SILTSTONE.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(SILTSTONE_STAIRS.getId().getPath(), () -> new BlockItem(SILTSTONE_STAIRS.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(SILTSTONE_SLAB.getId().getPath(), () -> new BlockItem(SILTSTONE_SLAB.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(SILTSTONE_WALL.getId().getPath(), () -> new BlockItem(SILTSTONE_WALL.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(SMOOTH_SILTSTONE.getId().getPath(), () -> new BlockItem(SMOOTH_SILTSTONE.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(SMOOTH_SILTSTONE_STAIRS.getId().getPath(), () -> new BlockItem(SMOOTH_SILTSTONE_STAIRS.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(SMOOTH_SILTSTONE_SLAB.getId().getPath(), () -> new BlockItem(SMOOTH_SILTSTONE_SLAB.get(), new Item.Properties()));
			RPMItems.ItemEntry.register(INFESTED_GLOWING_CRYSTAL.getId().getPath(), () -> new BlockItem(INFESTED_GLOWING_CRYSTAL.get(), new Item.Properties()));
		}
	}

	private RPMBlocks() {}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		TechnicalBlocks.init();
		WorkStation.init();
		Decoration.init();
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
