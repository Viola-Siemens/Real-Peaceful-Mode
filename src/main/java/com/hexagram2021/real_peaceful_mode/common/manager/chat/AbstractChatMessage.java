package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public abstract class AbstractChatMessage implements IChatMessage {
	public static final Codec<AbstractChatMessage> REGISTRY_CODEC = IChatMessageType.REGISTRY_CODEC.dispatch(AbstractChatMessage::type, IChatMessageType::codec);
	public static final StreamCodec<ByteBuf, AbstractChatMessage> REGISTRY_STREAM_CODEC = IChatMessageType.STREAM_CODEC.dispatch(AbstractChatMessage::type, IChatMessageType::streamCodec);

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

	@Nullable
	public BiConsumer<IMonsterHero, LivingEntity> onFinish() {
		return null;
	}

	protected AbstractChatMessage(String messageKey, Speaker speaker) {
		this.messageKey = messageKey;
		this.speaker = speaker;
	}
}
