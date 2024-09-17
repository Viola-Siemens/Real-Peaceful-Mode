package com.hexagram2021.real_peaceful_mode.network;

import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedChatInstance;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public record ClientboundChatMessagePacket(MessagedChatInstance chat, int containerId) implements CustomPacketPayload, IRPMPacket {
	public static final Type<ClientboundChatMessagePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "chat_message"));
	public static final StreamCodec<ByteBuf, ClientboundChatMessagePacket> STREAM_CODEC = StreamCodec.composite(
			MessagedChatInstance.STREAM_CODEC, ClientboundChatMessagePacket::chat,
			ByteBufCodecs.INT, ClientboundChatMessagePacket::containerId,
			ClientboundChatMessagePacket::new
	);

	@Override
	public void handle(IPayloadContext context) {
		ScreenManager.openChatMessageScreen(this.chat, this.containerId);
	}

	@Override
	public Type<ClientboundChatMessagePacket> type() {
		return TYPE;
	}
}
