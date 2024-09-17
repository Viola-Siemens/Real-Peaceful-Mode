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

public class EndChatMessage extends AbstractChatMessage {
	public static final MapCodec<EndChatMessage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(EndChatMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.NPC).forGetter(EndChatMessage::speaker)
	).apply(instance, EndChatMessage::new));
	public static final StreamCodec<ByteBuf, EndChatMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, EndChatMessage::messageKey,
			Speaker.STREAM_CODEC, EndChatMessage::speaker,
			EndChatMessage::new
	);

	public static final EndChatMessage EMPTY = new EndChatMessage("rpm.chat.real_peaceful_mode.empty", Speaker.NPC);

	public EndChatMessage(String messageKey, Speaker speaker) {
		super(messageKey, speaker);
	}

	@Override @Nullable
	public AbstractChatMessage getNext() {
		return null;
	}

	@Override @Nullable
	public List<ChatSelection> getSelections() {
		return null;
	}

	@Override
	public IChatMessageType type() {
		return ChatMessageTypes.END;
	}
}
