package com.hexagram2021.real_peaceful_mode.client;

import com.hexagram2021.real_peaceful_mode.api.RandomEventSpawnerHelper;
import com.hexagram2021.real_peaceful_mode.api.event.RegisterRandomEventSpawnerEvent;
import com.hexagram2021.real_peaceful_mode.common.register.RPMKeys;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMobEffects;
import com.hexagram2021.real_peaceful_mode.network.GetMissionsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEventHandler {
	@SubscribeEvent
	public static void onClientPlayerLogging(ClientPlayerNetworkEvent.LoggingIn event) {
		RandomEventSpawnerHelper.clearRandomEventSpawners();
		NeoForge.EVENT_BUS.post(new RegisterRandomEventSpawnerEvent(Dist.CLIENT));
	}

	@SubscribeEvent
	public static void onKeyboardInput(InputEvent.Key event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		if (RPMKeys.MISSION_SCREEN.isDown()) {
			PacketDistributor.sendToServer(new GetMissionsPacket());
		}
	}

	private static boolean hasEffect = false;
	@SubscribeEvent
	public static void onRenderPlayerView(ViewportEvent.ComputeCameraAngles event) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (!mc.isPaused() && player != null) {
			if(hasEffect) {
				if(!player.hasEffect(RPMMobEffects.TRANCE)) {
					mc.gameRenderer.checkEntityPostEffect(player);
					hasEffect = false;
				}
				float deltaYaw = 15.0F * Mth.sin((float)(player.tickCount * Math.PI / 101.0D)) + 6.0F * Mth.sin((float)(player.tickCount * Math.PI / 89.0D + 13.0D * Math.PI));
				float deltaPitch = 12.0F * Mth.sin((float)(player.tickCount * Math.PI / 97.0D)) + 5.0F * Mth.sin((float)(player.tickCount * Math.PI / 83.0D + 11.0D * Math.PI));
				event.setYaw(event.getYaw() + deltaYaw);
				event.setPitch(event.getPitch() + deltaPitch);
			} else {
				if(player.hasEffect(RPMMobEffects.TRANCE)) {
					mc.gameRenderer.loadEffect(ResourceLocation.fromNamespaceAndPath(MODID, "shaders/post/trance.json"));
					hasEffect = true;
				}
			}
		}
	}
}
