package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.CrystalSkullIslandPieces;
import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.SkeletonPalacePieces;
import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.ZombieFortPieces;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMStructurePieceTypes {
	public static final StructurePieceType CRYSTAL_SKULL_ISLAND_TYPE = register("crystal_skull_island", CrystalSkullIslandPieces.CrystalSkullIslandPiece::new);
	public static final StructurePieceType ZOMBIE_FORT_TYPE = register("zombie_fort", ZombieFortPieces.ZombieFortPiece::new);
	public static final StructurePieceType SKELETON_PALACE_TYPE = register("skeleton_palace", SkeletonPalacePieces.SkeletonPalacePiece::new);

	private RPMStructurePieceTypes() {
	}

	private static StructurePieceType register(String name, StructurePieceType type) {
		return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, new ResourceLocation(MODID, name), type);
	}

	public static void init() {
	}
}
