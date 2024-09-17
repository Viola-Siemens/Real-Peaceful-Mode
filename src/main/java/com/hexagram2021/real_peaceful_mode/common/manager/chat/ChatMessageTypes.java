package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class ChatMessageTypes {
	public static final IChatMessageType PLAIN = register("plain", new IChatMessageType() {
		@Override
		public MapCodec<? extends AbstractChatMessage> codec() {
			return PlainChatMessage.CODEC;
		}
		@Override
		public StreamCodec<ByteBuf, ? extends AbstractChatMessage> streamCodec() {
			return PlainChatMessage.STREAM_CODEC;
		}
	});
	public static final IChatMessageType SELECTABLE = register("selectable", new IChatMessageType() {
		@Override
		public MapCodec<? extends AbstractChatMessage> codec() {
			return SelectableChatMessage.CODEC;
		}
		@Override
		public StreamCodec<ByteBuf, ? extends AbstractChatMessage> streamCodec() {
			return SelectableChatMessage.STREAM_CODEC;
		}
	});
	public static final IChatMessageType END = register("end",new IChatMessageType() {
		@Override
		public MapCodec<? extends AbstractChatMessage> codec() {
			return EndChatMessage.CODEC;
		}
		@Override
		public StreamCodec<ByteBuf, ? extends AbstractChatMessage> streamCodec() {
			return EndChatMessage.STREAM_CODEC;
		}
	});
	public static final IChatMessageType MATERIAL_COLLECTION_START = register("material_collection_start",new IChatMessageType() {
		@Override
		public MapCodec<? extends AbstractChatMessage> codec() {
			return MaterialCollectionStartChatMessage.CODEC;
		}
		@Override
		public StreamCodec<ByteBuf, ? extends AbstractChatMessage> streamCodec() {
			return MaterialCollectionStartChatMessage.STREAM_CODEC;
		}
	});
	public static final IChatMessageType MATERIAL_COLLECTION_END = register("material_collection_end",new IChatMessageType() {
		@Override
		public MapCodec<? extends AbstractChatMessage> codec() {
			return MaterialCollectionEndChatMessage.CODEC;
		}
		@Override
		public StreamCodec<ByteBuf, ? extends AbstractChatMessage> streamCodec() {
			return MaterialCollectionEndChatMessage.STREAM_CODEC;
		}
	});

	public static void init() {
	}

	private static IChatMessageType register(String name, IChatMessageType type) {
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, name);
		IChatMessageType.registerChatMessageType(id, type);
		return type;
	}

	private ChatMessageTypes() {
	}
}
