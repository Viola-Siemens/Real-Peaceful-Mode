package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.mojang.serialization.Codec;

public abstract class AbstractChatMessage implements IChatMessage {
	public static final Codec<AbstractChatMessage> REGISTRY_CODEC = IChatMessageType.REGISTRY_CODEC.dispatch(AbstractChatMessage::type, IChatMessageType::codec);

	final String messageKey;
	final Speaker speaker;

	@Override
	public String messageKey() {
		return this.messageKey;
	}

	@Override
	public Speaker speaker() {
		return this.speaker;
	}

	public abstract IChatMessageType type();

	protected AbstractChatMessage(String messageKey, Speaker speaker) {
		this.messageKey = messageKey;
		this.speaker = speaker;
	}
}
