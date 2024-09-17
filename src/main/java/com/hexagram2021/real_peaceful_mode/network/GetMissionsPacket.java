package com.hexagram2021.real_peaceful_mode.network;

import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.IPlayerListWithMissions;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.Mission;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.PlayerMissions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public record GetMissionsPacket(List<Mission> activeMissions, List<Mission> finishedMissions) implements CustomPacketPayload, IRPMBidirectionalPacket {
	public static final CustomPacketPayload.Type<GetMissionsPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "get_missions"));
	public static final StreamCodec<RegistryFriendlyByteBuf, GetMissionsPacket> STREAM_CODEC = StreamCodec.composite(
			Mission.STREAM_CODEC.apply(ByteBufCodecs.list()), GetMissionsPacket::activeMissions,
			Mission.STREAM_CODEC.apply(ByteBufCodecs.list()), GetMissionsPacket::finishedMissions,
			GetMissionsPacket::new
	);
	public static final DirectionalPayloadHandler<GetMissionsPacket> HANDLER = IRPMBidirectionalPacket.getDirectionalPayloadHandler();

	public GetMissionsPacket() {
		this(List.of(), List.of());
	}

	@Override
	public void handleClient(IPayloadContext context) {
		ScreenManager.openMissionListScreen(this.activeMissions, this.finishedMissions);
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void handleServer(IPayloadContext context) {
		if(context instanceof ServerPayloadContext serverPayloadContext) {
			ServerPlayer sender = serverPayloadContext.player();
			PlayerMissions playerMissions = ((IPlayerListWithMissions) Objects.requireNonNull(sender.getServer()).getPlayerList()).rpm$getPlayerMissions(sender);
			List<Mission> activeMissions = playerMissions.getActiveMissions()
					.stream().map(id -> ForgeEventHandler.getMissionManager().getMission(id))
					.filter(Optional::isPresent).map(Optional::get)
					.toList();
			List<Mission> finishedMissions = playerMissions.getFinishedMissions()
					.stream().map(id -> ForgeEventHandler.getMissionManager().getMission(id))
					.filter(Optional::isPresent).map(Optional::get)
					.toList();
			GetMissionsPacket packet = new GetMissionsPacket(activeMissions, finishedMissions);
			PacketDistributor.sendToPlayer(sender, packet);
		}
	}

	@Override
	public Type<GetMissionsPacket> type() {
		return TYPE;
	}
}
