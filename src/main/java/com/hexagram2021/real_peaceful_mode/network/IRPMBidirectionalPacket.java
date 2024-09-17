package com.hexagram2021.real_peaceful_mode.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IRPMBidirectionalPacket {
	void handleClient(IPayloadContext context);
	void handleServer(IPayloadContext context);

	static <T extends CustomPacketPayload & IRPMBidirectionalPacket> DirectionalPayloadHandler<T> getDirectionalPayloadHandler() {
		return new DirectionalPayloadHandler<>(IRPMBidirectionalPacket::handleClient, IRPMBidirectionalPacket::handleServer);
	}
}
