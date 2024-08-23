package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

@FunctionalInterface
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

	Codec<? extends AbstractMessage> codec();
}
