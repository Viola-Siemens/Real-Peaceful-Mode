package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.effect.TranceEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMMobEffects {
	private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

	public static final RegistryObject<MobEffect> TRANCE = REGISTER.register("trance", TranceEffect::new);

	private RPMMobEffects() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
