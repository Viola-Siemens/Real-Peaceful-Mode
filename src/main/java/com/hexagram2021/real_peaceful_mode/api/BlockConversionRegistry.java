package com.hexagram2021.real_peaceful_mode.api;

import com.google.common.collect.Maps;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

//Please do not use these fields before FMLCommonSetupEvent is fired, because some blocks may not be registered!
public final class BlockConversionRegistry {
	private BlockConversionRegistry() {
	}

	/**
	 * @see com.hexagram2021.real_peaceful_mode.common.entity.misc.TinyFireballEntity#getSiltedBlockState
	 */
	public static final Map<Block, Block> SILTABLES = Util.make(Maps.newHashMap(), map -> {
		map.put(Blocks.SANDSTONE, RPMBlocks.Decoration.SILTSTONE.get());
		map.put(Blocks.SANDSTONE_SLAB, RPMBlocks.Decoration.SILTSTONE_SLAB.get());
		map.put(Blocks.SANDSTONE_STAIRS, RPMBlocks.Decoration.SILTSTONE_STAIRS.get());
		map.put(Blocks.SANDSTONE_WALL, RPMBlocks.Decoration.SILTSTONE_WALL.get());
		map.put(Blocks.SMOOTH_SANDSTONE, RPMBlocks.Decoration.SMOOTH_SILTSTONE.get());
		map.put(Blocks.SMOOTH_SANDSTONE_SLAB, RPMBlocks.Decoration.SMOOTH_SILTSTONE_SLAB.get());
		map.put(Blocks.SMOOTH_SANDSTONE_STAIRS, RPMBlocks.Decoration.SMOOTH_SILTSTONE_STAIRS.get());
		map.put(Blocks.GRASS_BLOCK, Blocks.DIRT);
	});

	/**
	 * @see com.hexagram2021.real_peaceful_mode.common.item.SlimeScepterItem#useOn
	 */
	public static final Map<Block, Block> STICKABLES = Util.make(Maps.newHashMap(), map -> {
		map.put(Blocks.STONE, RPMBlocks.Decoration.STICKY_STONE.get());
		map.put(Blocks.STONE_SLAB, RPMBlocks.Decoration.STICKY_STONE_SLAB.get());
		map.put(Blocks.STONE_STAIRS, RPMBlocks.Decoration.STICKY_STONE_STAIRS.get());
		map.put(Blocks.COBBLESTONE, RPMBlocks.Decoration.COBBLED_STICKY_STONE.get());
		map.put(Blocks.COBBLESTONE_SLAB, RPMBlocks.Decoration.COBBLED_STICKY_STONE_SLAB.get());
		map.put(Blocks.COBBLESTONE_STAIRS, RPMBlocks.Decoration.COBBLED_STICKY_STONE_STAIRS.get());
		map.put(Blocks.COBBLESTONE_WALL, RPMBlocks.Decoration.COBBLED_STICKY_STONE_WALL.get());
		map.put(Blocks.SMOOTH_STONE, RPMBlocks.Decoration.SMOOTH_STICKY_STONE.get());
		map.put(Blocks.SMOOTH_STONE_SLAB, RPMBlocks.Decoration.SMOOTH_STICKY_STONE_SLAB.get());
		map.put(Blocks.STONE_BRICKS, RPMBlocks.Decoration.STICKY_STONE_BRICKS.get());
		map.put(Blocks.STONE_BRICK_SLAB, RPMBlocks.Decoration.STICKY_STONE_BRICK_SLAB.get());
		map.put(Blocks.STONE_BRICK_STAIRS, RPMBlocks.Decoration.STICKY_STONE_BRICK_STAIRS.get());
		map.put(Blocks.STONE_BRICK_WALL, RPMBlocks.Decoration.STICKY_STONE_BRICK_WALL.get());
		map.put(Blocks.CRACKED_STONE_BRICKS, RPMBlocks.Decoration.CRACKED_STICKY_STONE_BRICKS.get());
	});
	/**
	 * @see com.hexagram2021.real_peaceful_mode.common.item.SlimeScepterItem#useOn
	 */
	public static final Map<Block, Block> UNSTICKABLES = Util.make(Maps.newHashMap(), map -> {
		map.put(RPMBlocks.Decoration.STICKY_STONE.get(), Blocks.STONE);
		map.put(RPMBlocks.Decoration.STICKY_STONE_SLAB.get(), Blocks.STONE_SLAB);
		map.put(RPMBlocks.Decoration.STICKY_STONE_STAIRS.get(), Blocks.STONE_STAIRS);
		map.put(RPMBlocks.Decoration.COBBLED_STICKY_STONE.get(), Blocks.COBBLESTONE);
		map.put(RPMBlocks.Decoration.COBBLED_STICKY_STONE_SLAB.get(), Blocks.COBBLESTONE_SLAB);
		map.put(RPMBlocks.Decoration.COBBLED_STICKY_STONE_STAIRS.get(), Blocks.COBBLESTONE_STAIRS);
		map.put(RPMBlocks.Decoration.COBBLED_STICKY_STONE_WALL.get(), Blocks.COBBLESTONE_WALL);
		map.put(RPMBlocks.Decoration.SMOOTH_STICKY_STONE.get(), Blocks.SMOOTH_STONE);
		map.put(RPMBlocks.Decoration.SMOOTH_STICKY_STONE_SLAB.get(), Blocks.SMOOTH_STONE_SLAB);
		map.put(RPMBlocks.Decoration.STICKY_STONE_BRICKS.get(), Blocks.STONE_BRICKS);
		map.put(RPMBlocks.Decoration.STICKY_STONE_BRICK_SLAB.get(), Blocks.STONE_BRICK_SLAB);
		map.put(RPMBlocks.Decoration.STICKY_STONE_BRICK_STAIRS.get(), Blocks.STONE_BRICK_STAIRS);
		map.put(RPMBlocks.Decoration.STICKY_STONE_BRICK_WALL.get(), Blocks.STONE_BRICK_WALL);
		map.put(RPMBlocks.Decoration.CRACKED_STICKY_STONE_BRICKS.get(), Blocks.CRACKED_STONE_BRICKS);
	});
}
