package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
	@SubscribeEvent
	public static void onEarnEvent(AdvancementEvent.AdvancementProgressEvent event) {
		if(event.getProgressType() == AdvancementEvent.AdvancementProgressEvent.ProgressType.GRANT) {
			ResourceLocation id = event.getAdvancement().getId();
			if(event.getEntity() instanceof IMonsterHero hero) {
				//TODO
			}
		}
	}
}
