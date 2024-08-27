package com.hexagram2021.real_peaceful_mode.network;

import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedChat;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedChatInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;

public class ClientboundChatMessagePacket implements IRPMPacket {
	private final MessagedChat chat;
	private final int containerId;

	public ClientboundChatMessagePacket(MessagedChat chat, int containerId) {
		this.chat = chat;
		this.containerId = containerId;
	}

	public ClientboundChatMessagePacket(FriendlyByteBuf buf) {
		this.chat = new MessagedChatInstance(Objects.requireNonNull(buf.readNbt()));
		this.containerId = buf.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(this.chat.createTag());
		buf.writeInt(this.containerId);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ScreenManager.openChatMessageScreen(this.chat, this.containerId);
	}
}
