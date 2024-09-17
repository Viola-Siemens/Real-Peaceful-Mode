package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.util.triggers.MissionFinishTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMTriggers {
	private static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create(Registries.TRIGGER_TYPE, MODID);
	public static final DeferredHolder<CriterionTrigger<?>, MissionFinishTrigger> MISSION_FINISH = register(MissionFinishTrigger.ID.getPath(), MissionFinishTrigger::new);

	public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, T> register(String name, Supplier<T> trigger) {
		return REGISTER.register(name, trigger);
	}

	private RPMTriggers() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
