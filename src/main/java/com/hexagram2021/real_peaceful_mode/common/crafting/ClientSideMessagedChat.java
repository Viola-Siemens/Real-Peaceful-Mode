package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.common.manager.chat.AbstractChatMessage;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.EndChatMessage;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public record ClientSideMessagedChat(Player player) implements MessagedChat {
	@Override
	public LivingEntity npc() {
		return this.player;
	}

	@Override
	public AbstractChatMessage message() {
		return EndChatMessage.EMPTY;
	}
}
