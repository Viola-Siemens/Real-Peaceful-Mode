package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.world.structures.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMStructureTypes {
	private static final DeferredRegister<StructureType<?>> REGISTER = DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);

	public static final DeferredHolder<StructureType<?>, StructureType<CrystalSkullIslandFeature>> CRYSTAL_SKULL_ISLAND = register("crystal_skull_island", () -> CrystalSkullIslandFeature.CODEC);
	public static final DeferredHolder<StructureType<?>, StructureType<AbandonedMagicPoolFeature>> ABANDONED_MAGIC_POOL = register("abandoned_magic_pool", () -> AbandonedMagicPoolFeature.CODEC);
	public static final DeferredHolder<StructureType<?>, StructureType<PinkCreeperFeature>> PINK_CREEPER = register("pink_creeper", () -> PinkCreeperFeature.CODEC);
	public static final DeferredHolder<StructureType<?>, StructureType<HuskWellFeature>> HUSK_WELL = register("husk_well", () -> HuskWellFeature.CODEC);
	public static final DeferredHolder<StructureType<?>, StructureType<ZombieFortFeature>> ZOMBIE_FORT = register("zombie_fort", () -> ZombieFortFeature.CODEC);
	public static final DeferredHolder<StructureType<?>, StructureType<SkeletonPalaceFeature>> SKELETON_PALACE = register("skeleton_palace", () -> SkeletonPalaceFeature.CODEC);
	public static final DeferredHolder<StructureType<?>, StructureType<PharaohAltarFeature>> PHARAOH_ALTAR = register("pharaoh_altar", () -> PharaohAltarFeature.CODEC);
	public static final DeferredHolder<StructureType<?>, StructureType<SlimeMazeFeature>> SLIME_MAZE = register("slime_maze", () -> SlimeMazeFeature.CODEC);
	public static final DeferredHolder<StructureType<?>, StructureType<HelplessGuardSlimeFeature>> HELPLESS_GUARD_SLIME = register("helpless_guard_slime", () -> HelplessGuardSlimeFeature.CODEC);

	private RPMStructureTypes() {
	}

	private static <T extends Structure> DeferredHolder<StructureType<?>, StructureType<T>> register(String name, StructureType<T> codec) {
		return REGISTER.register(name, () -> codec);
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
