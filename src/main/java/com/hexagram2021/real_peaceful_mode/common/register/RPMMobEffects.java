package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.effect.GlueEffect;
import com.hexagram2021.real_peaceful_mode.common.effect.TranceEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMMobEffects {
	private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, MODID);

	public static final DeferredHolder<MobEffect, TranceEffect> TRANCE = REGISTER.register("trance", TranceEffect::new);
	public static final DeferredHolder<MobEffect, GlueEffect> GLUE = REGISTER.register("glue", GlueEffect::new);

	private RPMMobEffects() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
