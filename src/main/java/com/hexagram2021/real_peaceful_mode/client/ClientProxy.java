package com.hexagram2021.real_peaceful_mode.client;

import com.hexagram2021.real_peaceful_mode.client.screens.MissionMessageScreen;
import com.hexagram2021.real_peaceful_mode.common.CommonProxy;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {
	public static void modConstruction() {
		ReloadableResourceManager reloadableManager = (ReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		ClientEventHandler handler = new ClientEventHandler();
		reloadableManager.registerReloadListener(handler);
	}

	@SubscribeEvent
	public static void setup(final FMLClientSetupEvent event) {
		event.enqueueWork(ClientProxy::registerContainersAndScreens);
	}

	private static void registerContainersAndScreens() {
		MenuScreens.register(RPMMenuTypes.MISSION_MESSAGE_MENU.get(), MissionMessageScreen::new);
	}
}
