package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nullable;
import java.util.Collection;

public class EndMessage extends AbstractMessage {
	public static final Codec<EndMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(EndMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.NPC).forGetter(EndMessage::speaker)
	).apply(instance, EndMessage::new));

	public EndMessage(String messageKey, Speaker speaker) {
		super(messageKey, speaker);
	}

	@Override @Nullable
	public AbstractMessage getNext() {
		return null;
	}

	@Override @Nullable
	public Collection<ChatSelection> getSelections() {
		return null;
	}

	@Override
	public IChatMessageType type() {
		return ChatMessageTypes.END;
	}
}
