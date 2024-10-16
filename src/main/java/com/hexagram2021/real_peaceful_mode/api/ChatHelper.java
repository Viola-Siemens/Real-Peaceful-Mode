package com.hexagram2021.real_peaceful_mode.api;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.Chat;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.PlayerMissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public class ChatHelper {
	@ApiStatus.Internal
	public static boolean triggerChatForPlayer(Chat chat, ServerPlayer player, LivingEntity npc) {
		if (player instanceof IMonsterHero hero && !player.getAbilities().instabuild && checkChat(hero, player, npc, chat)) {
			hero.rpm$getPlayerMissions().triggerChat(chat, npc);
			return true;
		}
		return false;
	}

	@ApiStatus.Internal
	public static boolean checkChat(IMonsterHero hero, ServerPlayer player, LivingEntity npc, Chat chat) {
		PlayerMissions playerMissions = hero.rpm$getPlayerMissions();
		if(IMonsterHero.chatDisabledFor(getRegistryName(chat.entityType()))) {
			return false;
		}
		if(!chat.formers().stream().allMatch(former -> former.checkComplete(playerMissions))) {
			return false;
		}
		return chat.triggerAnyway() || chat.hasAvailableSelections(player, npc);
	}
}
