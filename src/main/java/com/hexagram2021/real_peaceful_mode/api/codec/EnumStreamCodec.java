package com.hexagram2021.real_peaceful_mode.api.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.function.Function;

public record EnumStreamCodec<B extends ByteBuf, T extends StringRepresentable>(Function<String, T> byName) implements StreamCodec<B, T> {
	@Override
	public T decode(B buf) {
		return this.byName.apply(ByteBufCodecs.STRING_UTF8.decode(buf));
	}

	@Override
	public void encode(B buf, T input) {
		ByteBufCodecs.STRING_UTF8.encode(buf, input.getSerializedName());
	}
}
