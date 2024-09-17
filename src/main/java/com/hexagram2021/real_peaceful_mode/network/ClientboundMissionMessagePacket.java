package com.hexagram2021.real_peaceful_mode.network;

import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedMissionInstance;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public record ClientboundMissionMessagePacket(MessagedMissionInstance mission, int containerId) implements CustomPacketPayload, IRPMPacket {
	public static final CustomPacketPayload.Type<ClientboundMissionMessagePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "mission_message"));
	public static final StreamCodec<ByteBuf, ClientboundMissionMessagePacket> STREAM_CODEC = StreamCodec.composite(
			MessagedMissionInstance.STREAM_CODEC, ClientboundMissionMessagePacket::mission,
			ByteBufCodecs.INT, ClientboundMissionMessagePacket::containerId,
			ClientboundMissionMessagePacket::new
	);

	@Override
	public void handle(IPayloadContext context) {
		ScreenManager.openMissionMessageScreen(this.mission, this.containerId);
	}

	@Override
	public Type<ClientboundMissionMessagePacket> type() {
		return TYPE;
	}
}
