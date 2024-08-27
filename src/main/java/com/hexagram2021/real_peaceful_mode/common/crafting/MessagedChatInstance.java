package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.AbstractChatMessage;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

public class MessagedChatInstance implements MessagedChat {
	private final Player player;
	private final LivingEntity npc;
	private final AbstractChatMessage message;

	public MessagedChatInstance(Player player, LivingEntity npc, AbstractChatMessage message) {
		this.player = player;
		this.npc = npc;
		this.message = message;
	}

	public MessagedChatInstance(CompoundTag nbt) {
		this.player = ScreenManager.getLocalPlayer();
		int npcId = nbt.getInt(TAG_NPC);
		this.npc = (LivingEntity) this.player.level().getEntity(npcId);
		CompoundTag messages = nbt.getCompound(TAG_MESSAGE);
		this.message = AbstractChatMessage.REGISTRY_CODEC.parse(NbtOps.INSTANCE, messages).getOrThrow(false, RPMLogger::error);
	}

	@Override
	public Player player() {
		return this.player;
	}

	@Override
	public LivingEntity npc() {
		return this.npc;
	}

	@Override @Nonnull
	public AbstractChatMessage message() {
		return this.message;
	}
}
