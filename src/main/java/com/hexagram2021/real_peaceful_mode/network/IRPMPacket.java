package com.hexagram2021.real_peaceful_mode.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface IRPMPacket {
	void handle(IPayloadContext context);
}
