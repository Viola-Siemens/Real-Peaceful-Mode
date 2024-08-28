package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMStructurePieceTypes {
	public static final StructurePieceType CRYSTAL_SKULL_ISLAND_TYPE = register("crystal_skull_island", CrystalSkullIslandPieces.CrystalSkullIslandPiece::new);
	public static final StructurePieceType ABANDONED_MAGIC_POOL_TYPE = register("abandoned_magic_pool", AbandonedMagicPoolPieces.AbandonedMagicPoolPiece::new);
	public static final StructurePieceType PINK_CREEPER_TYPE = register("pink_creeper", PinkCreeperPieces.PinkCreeperPiece::new);
	public static final StructurePieceType HUSK_WELL_TYPE = register("husk_well", HuskWellPieces.HuskWellPiece::new);
	public static final StructurePieceType ZOMBIE_FORT_TYPE = register("zombie_fort", ZombieFortPieces.ZombieFortPiece::new);
	public static final StructurePieceType SKELETON_PALACE_TYPE = register("skeleton_palace", SkeletonPalacePieces.SkeletonPalacePiece::new);
	public static final StructurePieceType PHARAOH_ALTAR_TYPE = register("pharaoh_altar", PharaohAltarPieces.PharaohAltarPiece::new);
	public static final StructurePieceType SLIME_MAZE_ENTRANCE_TYPE = register("slime_maze_entrance", SlimeMazePieces.SlimeMazeEntrancePiece::new);
	public static final StructurePieceType SLIME_MAZE_TUNNEL_TYPE = register("slime_maze_tunnel", SlimeMazePieces.SlimeMazeTunnelPiece::new);
	public static final StructurePieceType SLIME_MAZE_MAIN_TYPE = register("slime_maze_main", SlimeMazePieces.SlimeMazeMainPiece::new);
	public static final StructurePieceType HELPLESS_GUARD_SLIME_TYPE = register("helpless_guard_slime", HelplessGuardSlimePieces.HelplessGuardSlimePiece::new);

	private RPMStructurePieceTypes() {
	}

	private static StructurePieceType register(String name, StructurePieceType type) {
		return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, new ResourceLocation(MODID, name), type);
	}

	public static void init() {
	}
}
