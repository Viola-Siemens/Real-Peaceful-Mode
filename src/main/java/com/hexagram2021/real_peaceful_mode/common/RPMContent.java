package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.emeraldcraft.common.register.ECConfiguredStructures;
import com.hexagram2021.emeraldcraft.common.register.ECStructureSets;
import com.hexagram2021.emeraldcraft.common.register.ECStructures;
import com.hexagram2021.real_peaceful_mode.common.crafting.compat.ModsCompatManager;
import com.hexagram2021.real_peaceful_mode.common.register.*;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

import static com.hexagram2021.emeraldcraft.EmeraldCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RPMContent {
	public static void modConstruction(IEventBus bus, Consumer<Runnable> runLater) {
		ModsCompatManager.compatModLoaded();

		//RPMWoodType.init();
		RPMBlocks.init(bus);
		RPMItems.init(bus);
	}

	public static void init() {

	}

	@SubscribeEvent
	public static void registerStructures(RegistryEvent.Register<StructureFeature<?>> event) {
		ECStructures.init(event);
		ECConfiguredStructures.init();
		ECStructureSets.init();
	}
}
