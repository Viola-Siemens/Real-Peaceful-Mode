package com.hexagram2021.real_peaceful_mode.network;

import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public record ClientboundChatSelectionPacket(Optional<List<ChatSelection>> chatSelections) implements CustomPacketPayload, IRPMPacket {
	public static final Type<ClientboundChatSelectionPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "chat_selection"));
	public static final StreamCodec<ByteBuf, ClientboundChatSelectionPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ChatSelection.STREAM_CODEC.apply(ByteBufCodecs.list())), ClientboundChatSelectionPacket::chatSelections,
			ClientboundChatSelectionPacket::new
	);

	public ClientboundChatSelectionPacket(@Nullable List<ChatSelection> chatSelections) {
		this(Optional.ofNullable(chatSelections));
	}

	@Override
	public void handle(IPayloadContext context) {
		ScreenManager.updateChatSelections(this.chatSelections.orElse(null));
	}

	@Override
	public Type<ClientboundChatSelectionPacket> type() {
		return TYPE;
	}
}
