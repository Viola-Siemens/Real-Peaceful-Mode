package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.AbstractChatMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Objects;

public record MessagedChatInstance(Player player, LivingEntity npc, AbstractChatMessage message) implements MessagedChat {
	public static final StreamCodec<ByteBuf, MessagedChatInstance> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, instance -> instance.npc.getId(),
			AbstractChatMessage.REGISTRY_STREAM_CODEC, MessagedChatInstance::message,
			MessagedChatInstance::new
	);

	public MessagedChatInstance(int npcId, AbstractChatMessage message) {
		this(ScreenManager.getLocalPlayer(), npcId, message);
	}
	public MessagedChatInstance(Player player, int npcId, AbstractChatMessage message) {
		this(player, (LivingEntity) Objects.requireNonNull(player.level().getEntity(npcId)), message);
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
