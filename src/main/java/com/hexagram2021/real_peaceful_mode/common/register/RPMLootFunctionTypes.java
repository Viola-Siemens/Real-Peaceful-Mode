package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.server.loots.ShiftMapDecorationFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMLootFunctionTypes {
	private static final DeferredRegister<LootItemFunctionType<?>> REGISTER = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);

	public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<ShiftMapDecorationFunction>> SHIFT_MAP_DECORATION = register("shift_map_decoration", ShiftMapDecorationFunction.CODEC);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private RPMLootFunctionTypes() {
	}

	@SuppressWarnings("SameParameterValue")
	private static <T extends LootItemFunction> DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<T>> register(String name, MapCodec<T> codec) {
		return REGISTER.register(name, () -> new LootItemFunctionType<>(codec));
	}
}
