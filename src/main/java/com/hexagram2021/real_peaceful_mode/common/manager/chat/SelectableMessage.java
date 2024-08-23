package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nullable;
import java.util.List;

public class SelectableMessage extends AbstractMessage {
	public static final Codec<SelectableMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(SelectableMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.NPC).forGetter(SelectableMessage::speaker),
			ChatSelection.CODEC.listOf().fieldOf("selection").forGetter(SelectableMessage::getSelections)
	).apply(instance, SelectableMessage::new));

	final List<ChatSelection> chatSelections;

	public SelectableMessage(String messageKey, Speaker speaker, List<ChatSelection> chatSelections) {
		super(messageKey, speaker);
		this.chatSelections = chatSelections;
	}

	@Override @Nullable
	public AbstractMessage getNext() {
		return null;
	}

	@Override
	public List<ChatSelection> getSelections() {
		return this.chatSelections;
	}

	@Override
	public IChatMessageType type() {
		return ChatMessageTypes.SELECTABLE;
	}
}
