package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface IChatMessageType {
	Map<ResourceLocation, IChatMessageType> CHAT_MESSAGE_TYPES = Maps.newHashMap();
	Map<IChatMessageType, ResourceLocation> CHAT_MESSAGE_IDS = Maps.newIdentityHashMap();

	static void registerChatMessageType(ResourceLocation id, IChatMessageType chatMessageType) {
		CHAT_MESSAGE_TYPES.put(id, chatMessageType);
		CHAT_MESSAGE_IDS.put(chatMessageType, id);
	}

	Codec<IChatMessageType> REGISTRY_CODEC = new Codec<>() {
		@Override
		public <R> DataResult<Pair<IChatMessageType, R>> decode(DynamicOps<R> ops, R input) {
			return ResourceLocation.CODEC.decode(ops, input).flatMap(pair -> {
				if (!CHAT_MESSAGE_TYPES.containsKey(pair.getFirst())) {
					return DataResult.error(() -> "Unexpected chat message type: %s".formatted(pair.getFirst()));
				}
				return DataResult.success(pair.mapFirst(CHAT_MESSAGE_TYPES::get));
			});
		}

		@Override
		public <R> DataResult<R> encode(IChatMessageType input, DynamicOps<R> ops, R prefix) {
			ResourceLocation id = CHAT_MESSAGE_IDS.get(input);
			if (id == null) {
				return DataResult.error(() -> "Unknown chat message type: %s".formatted(input));
			}
			R key = ops.createString(id.toString());
			return ops.mergeToPrimitive(prefix, key);
		}
	};
	StreamCodec<ByteBuf, IChatMessageType> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public IChatMessageType decode(ByteBuf buf) {
			ResourceLocation typeId = ResourceLocation.STREAM_CODEC.decode(buf);
			IChatMessageType type = CHAT_MESSAGE_TYPES.get(typeId);
			if (type == null) {
				throw new IllegalArgumentException("Unexpected chat message type: %s".formatted(typeId));
			}
			return type;
		}

		@Override
		public void encode(ByteBuf buf, IChatMessageType input) {
			ResourceLocation typeId = CHAT_MESSAGE_IDS.get(input);
			if (typeId == null) {
				throw new IllegalArgumentException("Unknown chat message type: %s".formatted(input));
			}
			ResourceLocation.STREAM_CODEC.encode(buf, typeId);
		}
	};

	MapCodec<? extends AbstractChatMessage> codec();
	StreamCodec<ByteBuf, ? extends AbstractChatMessage> streamCodec();
}
