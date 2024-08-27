package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.MissionMessage;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;

public class MessagedMissionInstance implements MessagedMission {
	private final Player player;
	@Nullable
	private final LivingEntity npc;
	private final List<MissionMessage> messages;

	public MessagedMissionInstance(Player player, @Nullable LivingEntity npc, List<MissionMessage> messages) {
		this.player = player;
		this.npc = npc;
		this.messages = messages;
	}

	public MessagedMissionInstance(CompoundTag nbt) {
		this.player = ScreenManager.getLocalPlayer();
		LivingEntity npc = null;
		if(nbt.contains(TAG_NPC, Tag.TAG_INT)) {
			int npcId = nbt.getInt(TAG_NPC);
			npc = (LivingEntity) this.player.level().getEntity(npcId);
		}
		this.npc = npc;
		ListTag list = nbt.getList(TAG_MESSAGE_LIST, Tag.TAG_COMPOUND);
		this.messages = MissionMessage.LIST_CODEC.parse(NbtOps.INSTANCE, list).getOrThrow(false, RPMLogger::error);
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
