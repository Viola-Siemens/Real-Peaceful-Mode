package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.MissionMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record MessagedMissionInstance(Player player, @Nullable LivingEntity npc, List<MissionMessage> messages) implements MessagedMission {
	public static final StreamCodec<ByteBuf, MessagedMissionInstance> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ByteBufCodecs.INT), instance -> Optional.ofNullable(instance.npc).map(LivingEntity::getId),
			MissionMessage.LIST_STREAM_CODEC, MessagedMissionInstance::messages,
			MessagedMissionInstance::new
	);

	public MessagedMissionInstance(Optional<Integer> npcId, List<MissionMessage> messages) {
		this(ScreenManager.getLocalPlayer(), npcId, messages);
	}
	public MessagedMissionInstance(Player player, Optional<Integer> npcId, List<MissionMessage> messages) {
		this(player, npcId.map(id -> (LivingEntity) player.level().getEntity(id)).orElse(null), messages);
	}

	@Override
	public Player player() {
		return this.player;
	}

	@Override @Nullable
	public LivingEntity npc() {
		return this.npc;
	}

	@Override
	public List<MissionMessage> messages() {
		return this.messages;
	}
}
