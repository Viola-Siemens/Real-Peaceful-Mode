package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.RealPeacefulMode;
import com.hexagram2021.real_peaceful_mode.api.ChatHelper;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.ChatManager;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.MissionManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.function.BiFunction;

public class ForgeEventHandler {
	@SuppressWarnings("NotNullFieldNotInitialized")
	private static ChatManager chatManager;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private static MissionManager missionManager;

	@SubscribeEvent
	public void onResourceReload(AddReloadListenerEvent event) {
		chatManager = new ChatManager();
		missionManager = new MissionManager();
		event.addListener(chatManager);
		event.addListener(missionManager);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onMobInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() instanceof IFriendlyMonster monster) {
			BiFunction<ServerPlayer, ItemStack, Boolean> action = monster.rpm$getRandomEventNpcAction();
			if (action != null && event.getEntity() instanceof ServerPlayer serverPlayer) {
				if (event.getHand() == InteractionHand.MAIN_HAND && action.apply(serverPlayer, serverPlayer.getItemInHand(event.getHand()))) {
					event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
					event.setCanceled(true);
					return;
				}
			} else if(event.getLevel().isClientSide && event.getEntity().getItemInHand(event.getHand()).is(holder -> RealPeacefulMode.isInteractItem(holder, event.getTarget().getType()))) {
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
				return;
			}
		}
		if (event.getEntity() instanceof ServerPlayer serverPlayer && event.getTarget() instanceof LivingEntity npc) {
			chatManager.getChatFor(npc.getType()).ifPresent(chat -> {
				if(ChatHelper.triggerChatForPlayer(chat, serverPlayer, npc)) {
					event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
					event.setCanceled(true);
				}
			});
		}
	}

	public static ChatManager getChatManager() {
		return chatManager;
	}
	public static MissionManager getMissionManager() {
		return missionManager;
	}
}
