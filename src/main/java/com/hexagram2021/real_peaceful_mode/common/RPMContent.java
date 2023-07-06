package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.common.crafting.compat.ModsCompatManager;
import com.hexagram2021.real_peaceful_mode.common.register.*;
import com.hexagram2021.real_peaceful_mode.common.world.village.Villages;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Consumer;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RPMContent {
	public static void modConstruction(IEventBus bus, Consumer<Runnable> runLater) {
		ModsCompatManager.compatModLoaded();

		RPMFluids.init(bus);
		RPMBlocks.init(bus);
		RPMItems.init(bus);
		Villages.Registers.init(bus);
		RPMBlockEntities.init(bus);
		RPMCreativeTabs.init(bus);
		RPMMenuTypes.init(bus);

	}

	public static void init() {
		Villages.init();
		ModVanillaCompat.setup();
	}

	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		RPMEntities.init(event);
	}
}
