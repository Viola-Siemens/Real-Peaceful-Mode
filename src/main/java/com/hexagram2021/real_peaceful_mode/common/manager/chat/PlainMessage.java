package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nullable;
import java.util.Collection;

public class PlainMessage extends AbstractMessage {
	public static final Codec<PlainMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(PlainMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.NPC).forGetter(PlainMessage::speaker),
			AbstractMessage.REGISTRY_CODEC.fieldOf("next").forGetter(PlainMessage::getNext)
	).apply(instance, PlainMessage::new));

	final AbstractMessage next;

	public PlainMessage(String messageKey, Speaker speaker, AbstractMessage next) {
		super(messageKey, speaker);
		this.next = next;
	}

	@Override
	public AbstractMessage getNext() {
		return this.next;
	}

	@Override @Nullable
	public Collection<ChatSelection> getSelections() {
		return null;
	}

	@Override
	public IChatMessageType type() {
		return ChatMessageTypes.PLAIN;
	}
}
