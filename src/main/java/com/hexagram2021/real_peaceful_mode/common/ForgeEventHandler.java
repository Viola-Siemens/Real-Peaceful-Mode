package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.RealPeacefulMode;
import com.hexagram2021.real_peaceful_mode.api.ChatHelper;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.capability.ConvertibleItemEntityHandler;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.ChatManager;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.MissionManager;
import com.hexagram2021.real_peaceful_mode.common.register.RPMCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.BiFunction;
import java.util.function.Consumer;

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

	@SubscribeEvent
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
				ChatHelper.triggerChatForPlayer(chat, serverPlayer, npc);
				event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
				event.setCanceled(true);
			});
		}
	}

	@SubscribeEvent
	public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject() instanceof ItemEntity) {
			event.addCapability(RPMCapabilities.ID_ITEM_ENTITY_CONVERTIBLE, new ConvertibleItemEntityHandler(1200));
		}
	}

	@SubscribeEvent
	public void onTickMob(LivingEvent.LivingTickEvent event) {
		if (event.getEntity() instanceof Mob mob && event.getEntity() instanceof IFriendlyMonster monster) {
			Consumer<Mob> tickAction = monster.rpm$getNpcExtraTickAction();
			if(tickAction != null) {
				tickAction.accept(mob);
			}
		}
	}

	public static ChatManager getChatManager() {
		return chatManager;
	}
	public static MissionManager getMissionManager() {
		return missionManager;
	}
}
