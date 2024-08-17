package com.hexagram2021.real_peaceful_mode.server.utils;

import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.hexagram2021.real_peaceful_mode.common.util.RandomMaze;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;

@SuppressWarnings("WrongTypeInTranslationArgs")
public final class GenerateCommandUtils {
	private GenerateCommandUtils() {
	}

	private static final DynamicCommandExceptionType DIRECTION_INVALID = new DynamicCommandExceptionType(
			direction -> Component.translatable("commands.real_peaceful_mode.generate.maze.direction_invalid", direction)
	);
	private static final Dynamic3CommandExceptionType LENGTH_INVALID = new Dynamic3CommandExceptionType(
			(length, roadWidth, wallWidth) -> Component.translatable("commands.real_peaceful_mode.generate.maze.length_invalid", length, roadWidth, wallWidth)
	);

	public static int maze(ServerPlayer player, ServerLevel serverLevel, Direction direction, int fullLength, int roadWidth, int wallWidth, BlockInput wallBlock, long seed) throws CommandSyntaxException {
		return maze(player, serverLevel, direction, fullLength, roadWidth, wallWidth, wallBlock, seed, player.blockPosition());
	}
	public static int maze(@Nullable ServerPlayer player, ServerLevel serverLevel, Direction zp, int fullLength, int roadWidth, int wallWidth, BlockInput wallBlock, long seed, BlockPos startPos) throws CommandSyntaxException {
		if(zp.getAxis() == Direction.Axis.Y) {
			throw DIRECTION_INVALID.create(zp);
		}
		int nodeWidth = roadWidth + wallWidth;
		if(fullLength % nodeWidth != wallWidth) {
			throw LENGTH_INVALID.create(fullLength, roadWidth, wallWidth);
		}
		int length = fullLength / nodeWidth;
		Direction xp = zp.getCounterClockWise();
		RandomMaze maze = new RandomMaze(length, seed);
		PlaceFunction placeWall = (level, pos) -> wallBlock.place(level, pos, Block.UPDATE_CLIENTS);
		for(int x = 0; x < length; ++x) {
			for(int z = 0; z < length; ++z) {
				if(maze.isAir(2 * x, 2 * z)) {
					generateBox(
							serverLevel, PLACE_AIR, startPos,
							nodeWidth * x, 0, nodeWidth * z,
							nodeWidth * x + wallWidth, 3, nodeWidth * z + wallWidth,
							xp, zp
					);
				} else {
					generateBox(
							serverLevel, placeWall, startPos,
							nodeWidth * x, 0, nodeWidth * z,
							nodeWidth * x + wallWidth, 3, nodeWidth * z + wallWidth,
							xp, zp
					);
				}
				if(maze.isAir(2 * x + 1, 2 * z)) {
					generateBox(
							serverLevel, PLACE_AIR, startPos,
							nodeWidth * x + wallWidth, 0, nodeWidth * z,
							nodeWidth * x + nodeWidth, 3, nodeWidth * z + wallWidth,
							xp, zp
					);
				} else {
					generateBox(
							serverLevel, placeWall, startPos,
							nodeWidth * x + wallWidth, 0, nodeWidth * z,
							nodeWidth * x + nodeWidth, 3, nodeWidth * z + wallWidth,
							xp, zp
					);
				}
				if(maze.isAir(2 * x, 2 * z + 1)) {
					generateBox(
							serverLevel, PLACE_AIR, startPos,
							nodeWidth * x, 0, nodeWidth * z + wallWidth,
							nodeWidth * x + wallWidth, 3, nodeWidth * z + nodeWidth,
							xp, zp
					);
				} else {
					generateBox(
							serverLevel, placeWall, startPos,
							nodeWidth * x, 0, nodeWidth * z + wallWidth,
							nodeWidth * x + wallWidth, 3, nodeWidth * z + nodeWidth,
							xp, zp
					);
				}
				if(maze.isAir(2 * x + 1, 2 * z + 1)) {
					generateBox(
							serverLevel, PLACE_AIR, startPos,
							nodeWidth * x + wallWidth, 0, nodeWidth * z + wallWidth,
							nodeWidth * x + nodeWidth, 3, nodeWidth * z + nodeWidth,
							xp, zp
					);
				} else {
					generateBox(
							serverLevel, placeWall, startPos,
							nodeWidth * x + wallWidth, 0, nodeWidth * z + wallWidth,
							nodeWidth * x + nodeWidth, 3, nodeWidth * z + nodeWidth,
							xp, zp
					);
				}
			}
		}
		for(int z = 0; z < length; ++z) {
			if(maze.isAir(2 * length, 2 * z)) {
				generateBox(serverLevel, PLACE_AIR, startPos, fullLength - 1, 0, nodeWidth * z, fullLength, 3, nodeWidth * z + wallWidth, xp, zp);
			} else {
				generateBox(serverLevel, placeWall, startPos, fullLength - 1, 0, nodeWidth * z, fullLength, 3, nodeWidth * z + wallWidth, xp, zp);
			}
			if(maze.isAir(2 * length, 2 * z + 1)) {
				generateBox(serverLevel, PLACE_AIR, startPos, fullLength - 1, 0, nodeWidth * z + wallWidth, fullLength, 3, nodeWidth * z + nodeWidth, xp, zp);
			} else {
				generateBox(serverLevel, placeWall, startPos, fullLength - 1, 0, nodeWidth * z + wallWidth, fullLength, 3, nodeWidth * z + nodeWidth, xp, zp);
			}
		}
		for(int x = 0; x < length; ++x) {
			if(maze.isAir(2 * x, 2 * length)) {
				generateBox(serverLevel, PLACE_AIR, startPos, nodeWidth * x, 0, fullLength - 1, nodeWidth * x + wallWidth, 3, fullLength, xp, zp);
			} else {
				generateBox(serverLevel, placeWall, startPos, nodeWidth * x, 0, fullLength - 1, nodeWidth * x + wallWidth, 3, fullLength, xp, zp);
			}
			if(maze.isAir(2 * x + 1, 2 * length)) {
				generateBox(serverLevel, PLACE_AIR, startPos, nodeWidth * x + wallWidth, 0, fullLength - 1, nodeWidth * x + nodeWidth, 3, fullLength, xp, zp);
			} else {
				generateBox(serverLevel, placeWall, startPos, nodeWidth * x + wallWidth, 0, fullLength - 1, nodeWidth * x + nodeWidth, 3, fullLength, xp, zp);
			}
		}
		if(maze.isAir(2 * length, 2 * length)) {
			generateBox(serverLevel, PLACE_AIR, startPos, fullLength - 1, 0, fullLength - 1, fullLength - 1, 3, fullLength - 1, xp, zp);
		} else {
			generateBox(serverLevel, placeWall, startPos, fullLength - 1, 0, fullLength - 1, fullLength - 1, 3, fullLength - 1, xp, zp);
		}

		RPMLogger.info("Successfully generate maze at (%d, %d, %d).".formatted(startPos.getX(), startPos.getY(), startPos.getZ()));
		if(player != null) {
			player.sendSystemMessage(Component.translatable("commands.real_peaceful_mode.generate.maze.success"));
		}

		return Command.SINGLE_SUCCESS;
	}

	public static void generateBox(ServerLevel level, PlaceFunction placeFunc, BlockPos startPos, int x1, int y1, int z1, int x2, int y2, int z2, Direction xp, Direction zp) {
		for(int x = x1; x < x2; ++x) {
			for(int z = z1; z < z2; ++z) {
				for(int y = y1; y < y2; ++y) {
					BlockPos current = startPos.relative(xp, x).relative(zp, z).relative(Direction.UP, y);
					placeFunc.place(level, current);
				}
			}
		}
	}

	static final PlaceFunction PLACE_AIR = (level, pos) -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
	@FunctionalInterface
	public interface PlaceFunction {
		void place(ServerLevel level, BlockPos blockPos);
	}
}
