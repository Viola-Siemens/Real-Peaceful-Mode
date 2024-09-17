package com.hexagram2021.real_peaceful_mode.common.manager.mission;

import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record MissionMessage(String messageKey, Speaker speaker) {
	public static final Codec<MissionMessage> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(MissionMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.PLAYER).forGetter(MissionMessage::speaker)
	).apply(instance, MissionMessage::new));
	public static final Codec<MissionMessage> CODEC = Codec.either(DIRECT_CODEC, Codec.STRING)
			.xmap(either -> either.map(Function.identity(), key -> new MissionMessage(key, Speaker.PLAYER)), Either::left);
	public static final Codec<List<MissionMessage>> LIST_CODEC = CODEC.listOf();

	public static final StreamCodec<ByteBuf, MissionMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, MissionMessage::messageKey,
			ByteBufCodecs.optional(Speaker.STREAM_CODEC).map(optional -> optional.orElse(Speaker.PLAYER), Optional::of), MissionMessage::speaker,
			MissionMessage::new
	);
	public static final StreamCodec<ByteBuf, List<MissionMessage>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
}
