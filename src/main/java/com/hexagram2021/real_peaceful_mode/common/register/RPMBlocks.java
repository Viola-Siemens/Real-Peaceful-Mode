package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.block.SummonBlock;
import com.hexagram2021.real_peaceful_mode.common.block.skull.RPMSkullTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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

public class RPMBlocks {
	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final class TechnicalBlocks {
		public static final BlockEntry<SummonBlock> SUMMON_BLOCK = new BlockEntry<>(
				"summon_block", () -> BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F)
				.noCollission().noLootTable()
				.isValidSpawn((blockState, level, blockPos, entityType) -> false)
				.noParticlesOnBreak().pushReaction(PushReaction.BLOCK), SummonBlock::new
		);

		private TechnicalBlocks() {}

		private static void init() {
			RPMItems.ItemEntry.register(SUMMON_BLOCK.getId().getPath(), () -> new BlockItem(SUMMON_BLOCK.get(), new Item.Properties()));
		}
	}

	public static final class WorkStation {
		public static final BlockEntry<Block> REFINEMENT_TABLE = new BlockEntry<>(
				"refinement_table", () -> BlockBehaviour.Properties.of()
				.mapColor(MapColor.COLOR_BLACK).strength(50.0F, 1200.0F).lightLevel(blockState -> 1)
				.noOcclusion().pushReaction(PushReaction.BLOCK), Block::new
		);

		private WorkStation() {}

		private static void init() {
			RPMItems.ItemEntry.register(REFINEMENT_TABLE.getId().getPath(), () -> new BlockItem(REFINEMENT_TABLE.get(), new Item.Properties()));
		}
	}

	public static final class Decoration {
		public static final BlockEntry<SkullBlock> DARK_ZOMBIE_KNIGHT_SKULL = new BlockEntry<>(
				"dark_zombie_knight_skull", () -> BlockBehaviour.Properties.of()
				.instrument(RPMNoteBlockInstruments.DARK_ZOMBIE_KNIGHT).strength(1.0F).pushReaction(PushReaction.DESTROY),
				props -> new SkullBlock(RPMSkullTypes.DARK_ZOMBIE_KNIGHT, props)
		);

		private Decoration() {}

		private static void init() {

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
