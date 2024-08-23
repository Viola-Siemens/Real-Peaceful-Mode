package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class ChatMessageTypes {
	public static final IChatMessageType PLAIN = register("plain", () -> PlainMessage.CODEC);
	public static final IChatMessageType SELECTABLE = register("selectable", () -> SelectableMessage.CODEC);
	public static final IChatMessageType END = register("end", () -> EndMessage.CODEC);

	public static void init() {
	}

	private static IChatMessageType register(String name, IChatMessageType type) {
		ResourceLocation id = new ResourceLocation(MODID, name);
		IChatMessageType.registerChatMessageType(id, type);
		return type;
	}

	private ChatMessageTypes() {
	}
}
