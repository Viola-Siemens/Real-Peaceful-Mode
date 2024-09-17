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

public class SelectableChatMessage extends AbstractChatMessage {
	public static final MapCodec<SelectableChatMessage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(SelectableChatMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.NPC).forGetter(SelectableChatMessage::speaker),
			ChatSelection.CODEC.listOf().fieldOf("selection").forGetter(SelectableChatMessage::getSelections)
	).apply(instance, SelectableChatMessage::new));
	public static final StreamCodec<ByteBuf, SelectableChatMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, SelectableChatMessage::messageKey,
			Speaker.STREAM_CODEC, SelectableChatMessage::speaker,
			ChatSelection.STREAM_CODEC.apply(ByteBufCodecs.list()), SelectableChatMessage::getSelections,
			SelectableChatMessage::new
	);

	final List<ChatSelection> chatSelections;

	public SelectableChatMessage(String messageKey, Speaker speaker, List<ChatSelection> chatSelections) {
		super(messageKey, speaker);
		this.chatSelections = chatSelections;
	}

	@Override @Nullable
	public AbstractChatMessage getNext() {
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
