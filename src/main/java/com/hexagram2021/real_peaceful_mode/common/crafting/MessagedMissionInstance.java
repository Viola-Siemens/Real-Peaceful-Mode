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
public class MessagedMissionInstance implements MessagedMission {
	final Player player;
	@Nullable LivingEntity npc;
	final Optional<Integer> npcId;
	final List<MissionMessage> messages;

	public static final StreamCodec<ByteBuf, MessagedMissionInstance> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ByteBufCodecs.INT), instance -> Optional.ofNullable(instance.npc).map(LivingEntity::getId),
			MissionMessage.LIST_STREAM_CODEC, MessagedMissionInstance::messages,
			MessagedMissionInstance::new
	);

	public MessagedMissionInstance(Optional<Integer> npcId, List<MissionMessage> messages) {
		this(ScreenManager.getLocalPlayer(), npcId, messages);
	}
	public MessagedMissionInstance(Player player, Optional<Integer> npcId, List<MissionMessage> messages) {
		this(player, npcId.map(id -> (LivingEntity) player.level().getEntity(id)).orElse(null), npcId, messages);
	}
	public MessagedMissionInstance(Player player, @Nullable LivingEntity npc, Optional<Integer> npcId, List<MissionMessage> messages) {
		this.player = player;
		this.npc = npc;
		this.npcId = npcId;
		this.messages = messages;
	}

	@Override
	public Player player() {
		return this.player;
	}

	@Override @Nullable
	public LivingEntity npc() {
		if(this.npc == null) {
			this.npc = this.npcId.map(id -> (LivingEntity) this.player.level().getEntity(id)).orElse(null);
		}
		return this.npc;
	}

	@Override
	public List<MissionMessage> messages() {
		return this.messages;
	}
}
