package com.hexagram2021.real_peaceful_mode.client;

import com.hexagram2021.real_peaceful_mode.RealPeacefulMode;
import com.hexagram2021.real_peaceful_mode.common.register.RPMKeys;
import com.hexagram2021.real_peaceful_mode.network.GetMissionsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
	@SubscribeEvent
	public static void onKeyboardInput(InputEvent.Key event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		if (RPMKeys.MISSION_SCREEN.isDown()) {
			RealPeacefulMode.packetHandler.sendToServer(new GetMissionsPacket());
		}
	}
}
