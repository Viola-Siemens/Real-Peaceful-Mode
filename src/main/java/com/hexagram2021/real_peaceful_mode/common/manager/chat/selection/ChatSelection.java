package com.hexagram2021.real_peaceful_mode.common.manager.chat.selection;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.AbstractChatMessage;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.IChatMessage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ChatSelection implements IChatMessage {
	public static final Codec<ChatSelection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(ChatSelection::messageKey),
			AbstractChatMessage.REGISTRY_CODEC.fieldOf("next").forGetter(ChatSelection::getNext),
			ISelectionCondition.REGISTRY_CODEC.fieldOf("condition").forGetter(ChatSelection::condition)
	).apply(instance, ChatSelection::new));
	public static final StreamCodec<ByteBuf, ChatSelection> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, ChatSelection::messageKey,
			AbstractChatMessage.REGISTRY_STREAM_CODEC, ChatSelection::getNext,
			ISelectionCondition.DummySelectionCondition.STREAM_CODEC, ChatSelection::condition,
			ChatSelection::new
	);

	final String messageKey;
	final AbstractChatMessage next;

	final ISelectionCondition condition;

	protected ChatSelection(String messageKey, AbstractChatMessage next, ISelectionCondition condition) {
		this.messageKey = messageKey;
		this.next = next;
		this.condition = condition;
	}

	@Override
	public String messageKey() {
		return this.messageKey;
	}

	@Override
	public Speaker speaker() {
		return Speaker.PLAYER;
	}

	@Override @Nonnull
	public AbstractChatMessage getNext() {
		return this.next;
	}

	public ISelectionCondition condition() {
		return condition;
	}

	@Override @Nullable
	public List<ChatSelection> getSelections() {
		return null;
	}

	public boolean canShowFor(ServerPlayer player, LivingEntity npc) {
		return this.condition.check(player, npc);
	}
}
