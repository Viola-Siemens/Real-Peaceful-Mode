package com.hexagram2021.real_peaceful_mode.client;

import com.hexagram2021.real_peaceful_mode.client.screens.MissionMessageScreen;
import com.hexagram2021.real_peaceful_mode.common.CommonProxy;
import com.hexagram2021.real_peaceful_mode.common.block.skull.RPMSkullTypes;
import com.hexagram2021.real_peaceful_mode.common.register.RPMKeys;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.SkullModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {
	public static void modConstruction() {
	}

	@SubscribeEvent
	public static void setup(FMLClientSetupEvent event) {
		event.enqueueWork(ClientProxy::registerContainersAndScreens);
		RPMKeys.init();
	}

	private static void registerContainersAndScreens() {
		MenuScreens.register(RPMMenuTypes.MISSION_MESSAGE_MENU.get(), MissionMessageScreen::new);
	}

	@SubscribeEvent
	public static void onCreateSkullModel(EntityRenderersEvent.CreateSkullModels event) {
		event.registerSkullModel(RPMSkullTypes.DARK_ZOMBIE_KNIGHT, new SkullModel(event.getEntityModelSet().bakeLayer(RPMModelLayers.DARK_ZOMBIE_KNIGHT_SKULL)));
	}

	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(RPMModelLayers.DARK_ZOMBIE_KNIGHT_SKULL, SkullModel::createMobHeadLayer);
	}

	@SubscribeEvent
	public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {

	}
}
