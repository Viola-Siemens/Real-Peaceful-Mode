package com.hexagram2021.real_peaceful_mode.network;

import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedMission;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedMissionInstance;
import com.hexagram2021.real_peaceful_mode.common.crafting.menu.MissionMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;

public class ClientboundMissionMessagePacket implements IRPMPacket {
	private final MessagedMission mission;
	private final int containerId;

	public ClientboundMissionMessagePacket(MessagedMission mission, int containerId) {
		this.mission = mission;
		this.containerId = containerId;
	}

	public ClientboundMissionMessagePacket(FriendlyByteBuf buf) {
		this.mission = new MessagedMissionInstance(Objects.requireNonNull(buf.readNbt()));
		this.containerId = buf.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeNbt(this.mission.createTag());
		buf.writeInt(this.containerId);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		RPMLogger.debug(this.mission.createTag());
		//RPMLogger.debug(this.containerId);
		//RPMLogger.debug(player == null);
		if(player != null) {
			AbstractContainerMenu menu = player.containerMenu;
			if(menu.containerId == this.containerId && menu instanceof MissionMessageMenu missionMessageMenu) {
				missionMessageMenu.setMission(mission);
			}
		}
	}
}
