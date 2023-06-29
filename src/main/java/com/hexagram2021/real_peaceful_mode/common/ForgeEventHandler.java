package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
	private static MissionManager missionManager;

	@SubscribeEvent
	public static void onEarnEvent(AdvancementEvent.AdvancementProgressEvent event) {
		if(event.getProgressType() == AdvancementEvent.AdvancementProgressEvent.ProgressType.GRANT) {
			ResourceLocation id = event.getAdvancement().getId();
			RPMLogger.debug("Player %s grant advancement %s".formatted(event.getEntity().getName().getString(), id));
			missionManager.getMissionByAdvancementId(id).ifPresent(mission -> {
				if(event.getEntity() instanceof IMonsterHero hero) {
					//TODO
				}
			});
		}
	}

	@SubscribeEvent
	public static void onResourceReload(AddReloadListenerEvent event) {
		missionManager = new MissionManager();
		event.addListener(missionManager);
	}
}
