package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.mission.Mission;
import com.hexagram2021.real_peaceful_mode.common.mission.Speaker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;

public class MessagedMissionInstance implements MessagedMission {
	private final Player player;
	@Nullable
	private final LivingEntity npc;
	private final List<Mission.Message> messages;

	public MessagedMissionInstance(Player player, @Nullable LivingEntity npc, List<Mission.Message> messages) {
		this.player = player;
		this.npc = npc;
		this.messages = messages;
	}

	public MessagedMissionInstance(CompoundTag nbt) {
		this.player = ScreenManager.getLocalPlayer();
		LivingEntity npc = null;
		if(nbt.contains(NPC_ID, Tag.TAG_INT)) {
			int npcId = nbt.getInt(NPC_ID);
			npc = (LivingEntity) this.player.level().getEntity(npcId);
		}
		this.npc = npc;
		ListTag list = nbt.getList(MESSAGE_LIST, Tag.TAG_COMPOUND);
		ImmutableList.Builder<Mission.Message> builder = ImmutableList.builder();
		list.forEach(tag -> {
			CompoundTag compoundTag = (CompoundTag)tag;
			String key = compoundTag.getString(MESSAGE_KEY);
			Speaker speaker = Speaker.values()[compoundTag.getByte(MESSAGE_SPEAKER)];
			builder.add(new Mission.Message(key, speaker));
		});
		this.messages = builder.build();
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
	public List<Mission.Message> messages() {
		return this.messages;
	}
}
