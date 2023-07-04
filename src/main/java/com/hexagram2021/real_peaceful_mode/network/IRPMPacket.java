package com.hexagram2021.real_peaceful_mode.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface IRPMPacket {
	void write(FriendlyByteBuf buf);
	void handle(NetworkEvent.Context context);

	enum PacketType {
		REQUEST,
		RESPONSE
	}
}
