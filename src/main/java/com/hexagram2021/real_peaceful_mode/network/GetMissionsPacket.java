package com.hexagram2021.real_peaceful_mode.network;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.RealPeacefulMode;
import com.hexagram2021.real_peaceful_mode.client.screens.MissionListScreen;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.mission.IPlayerListWithMissions;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import com.hexagram2021.real_peaceful_mode.common.mission.PlayerMissions;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public class GetMissionsPacket implements IRPMPacket {
	private final PacketType type;
	private final List<MissionManager.Mission> activeMissions;
	private final List<MissionManager.Mission> finishedMissions;

	public GetMissionsPacket() {
		this.type = PacketType.REQUEST;
		this.activeMissions = List.of();
		this.finishedMissions = List.of();
	}
	public GetMissionsPacket(List<MissionManager.Mission> activeMissions, List<MissionManager.Mission> finishedMissions) {
		this.type = PacketType.RESPONSE;
		this.activeMissions = activeMissions;
		this.finishedMissions = finishedMissions;
	}

	public GetMissionsPacket(FriendlyByteBuf buf) {
		this.type = buf.readEnum(PacketType.class);
		this.activeMissions = buf.readCollection(Lists::newArrayListWithCapacity, readerBuf -> {
			ResourceLocation id = readerBuf.readResourceLocation();
			EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(readerBuf.readResourceLocation());
			if(entityType == null) {
				entityType = EntityType.PLAYER;
			}
			ResourceLocation loot = readerBuf.readResourceLocation();
			return new MissionManager.Mission(id, List.of(), List.of(), List.of(), entityType, loot);
		});
		this.finishedMissions = buf.readCollection(Lists::newArrayListWithCapacity, readerBuf -> {
			ResourceLocation id = readerBuf.readResourceLocation();
			EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(readerBuf.readResourceLocation());
			if(entityType == null) {
				entityType = EntityType.PLAYER;
			}
			ResourceLocation loot = readerBuf.readResourceLocation();
			return new MissionManager.Mission(id, List.of(), List.of(), List.of(), entityType, loot);
		});
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeEnum(this.type);
		buf.writeCollection(this.activeMissions, (writerBuf, mission) -> {
			writerBuf.writeResourceLocation(mission.id());
			writerBuf.writeResourceLocation(getRegistryName(mission.reward()));
			writerBuf.writeResourceLocation(mission.rewardLootTable());
		});
		buf.writeCollection(this.finishedMissions, (writerBuf, mission) -> {
			writerBuf.writeResourceLocation(mission.id());
			writerBuf.writeResourceLocation(getRegistryName(mission.reward()));
			writerBuf.writeResourceLocation(mission.rewardLootTable());
		});
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer sender = context.getSender();
		assert (sender == null) ^ (this.type == PacketType.REQUEST);
		context.enqueueWork(() -> {
			if(sender == null) {
				Minecraft.getInstance().setScreen(new MissionListScreen(this.activeMissions, this.finishedMissions));
			} else {
				PlayerMissions playerMissions = ((IPlayerListWithMissions) Objects.requireNonNull(sender.getServer()).getPlayerList()).getPlayerMissions(sender);
				List<MissionManager.Mission> activeMissions = playerMissions.activeMissions()
						.stream().map(id -> ForgeEventHandler.getMissionManager().getMission(id))
						.filter(Optional::isPresent).map(Optional::get)
						.toList();
				List<MissionManager.Mission> finishedMissions = playerMissions.finishedMissions()
						.stream().map(id -> ForgeEventHandler.getMissionManager().getMission(id))
						.filter(Optional::isPresent).map(Optional::get)
						.toList();
				GetMissionsPacket packet = new GetMissionsPacket(activeMissions, finishedMissions);
				RealPeacefulMode.packetHandler.send(PacketDistributor.PLAYER.with(() -> sender), packet);
			}
		});
	}
}
