package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ForgeEventHandler {
	private static MissionManager missionManager;

	@SubscribeEvent
	public void onResourceReload(AddReloadListenerEvent event) {
		missionManager = new MissionManager();
		event.addListener(missionManager);
	}

	@SubscribeEvent
	public void onMobInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() instanceof IFriendlyMonster monster) {
			BiFunction<ServerPlayer, ItemStack, Boolean> action = monster.getRandomEventNpcAction();
			if (action != null) {
				if(event.getEntity() instanceof ServerPlayer serverPlayer) {
					if (event.getHand() == InteractionHand.MAIN_HAND && action.apply(serverPlayer, serverPlayer.getItemInHand(event.getHand()))) {
						event.setCancellationResult(InteractionResult.SUCCESS);
						event.setCanceled(true);
						return;
					}
				} else {
					event.setCancellationResult(InteractionResult.SUCCESS);
					event.setCanceled(true);
					return;
				}
			}

			if(event.getEntity() instanceof IMonsterHero hero && hero.isHero(event.getTarget().getType())) {
				//TODO Other interactions.
				//event.setCancellationResult(InteractionResult.SUCCESS);
				//event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onTickMob(LivingEvent.LivingTickEvent event) {
		if (event.getEntity() instanceof Mob mob && event.getEntity() instanceof IFriendlyMonster monster) {
			Consumer<Mob> tickAction = monster.getNpcExtraTickAction();
			if(tickAction != null) {
				tickAction.accept(mob);
			}
		}
	}

	public static MissionManager getMissionManager() {
		return missionManager;
	}
}
