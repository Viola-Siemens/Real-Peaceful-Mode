package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.List;

public class PlainChatMessage extends AbstractChatMessage {
	public static final MapCodec<PlainChatMessage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(PlainChatMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.NPC).forGetter(PlainChatMessage::speaker),
			AbstractChatMessage.REGISTRY_CODEC.fieldOf("next").forGetter(PlainChatMessage::getNext)
	).apply(instance, PlainChatMessage::new));
	public static final StreamCodec<ByteBuf, PlainChatMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, PlainChatMessage::messageKey,
			Speaker.STREAM_CODEC, PlainChatMessage::speaker,
			AbstractChatMessage.REGISTRY_STREAM_CODEC, PlainChatMessage::getNext,
			PlainChatMessage::new
	);

	final AbstractChatMessage next;

	public PlainChatMessage(String messageKey, Speaker speaker, AbstractChatMessage next) {
		super(messageKey, speaker);
		this.next = next;
	}

	@Override
	public AbstractChatMessage getNext() {
		return this.next;
	}

	@Override @Nullable
	public List<ChatSelection> getSelections() {
		return null;
	}

	@Override
	public IChatMessageType type() {
		return ChatMessageTypes.PLAIN;
	}
}
