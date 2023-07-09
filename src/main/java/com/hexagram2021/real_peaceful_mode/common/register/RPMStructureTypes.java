package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.world.structures.CrystalSkullIslandFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMStructureTypes {
	private static final DeferredRegister<StructureType<?>> REGISTER = DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);

	public static final RegistryObject<StructureType<CrystalSkullIslandFeature>> CRYSTAL_SKULL_ISLAND = register("crystal_skull_island", () -> CrystalSkullIslandFeature.CODEC);

	private RPMStructureTypes() {
	}

	private static <T extends Structure> RegistryObject<StructureType<T>> register(String name, StructureType<T> codec) {
		return REGISTER.register(name, () -> codec);
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
