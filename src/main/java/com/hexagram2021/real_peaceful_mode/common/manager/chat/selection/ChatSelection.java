package com.hexagram2021.real_peaceful_mode.common.manager.chat.selection;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.AbstractMessage;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.IChatMessage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.Collection;

public class ChatSelection implements IChatMessage {
	public static final Codec<ChatSelection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(ChatSelection::messageKey),
			AbstractMessage.REGISTRY_CODEC.fieldOf("next").forGetter(ChatSelection::getNext),
			ISelectionCondition.REGISTRY_CODEC.fieldOf("condition").forGetter(ChatSelection::condition)
	).apply(instance, ChatSelection::new));

	final String messageKey;
	final AbstractMessage next;

	final ISelectionCondition condition;

	protected ChatSelection(String messageKey, AbstractMessage next, ISelectionCondition condition) {
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

	@Override
	public AbstractMessage getNext() {
		return this.next;
	}

	public ISelectionCondition condition() {
		return condition;
	}

	@Override @Nullable
	public Collection<ChatSelection> getSelections() {
		return null;
	}

	public boolean canShowFor(ServerPlayer player) {
		return this.condition.check(player);
	}
}
