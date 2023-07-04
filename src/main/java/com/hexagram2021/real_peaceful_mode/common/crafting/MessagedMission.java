package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;

public interface MessagedMission {
	Player player();

	@Nullable
	LivingEntity npc();

	List<MissionManager.Mission.Message> messages();
	
	String NPC_ID = "npc";
	String MESSAGE_LIST = "messages";
	String MESSAGE_KEY = "key";
	String MESSAGE_SPEAKER = "speaker";
	default CompoundTag createTag() {
		CompoundTag ret = new CompoundTag();
		LivingEntity npc = this.npc();
		if(npc != null) {
			ret.putInt(NPC_ID, npc.getId());
		}
		ListTag list = new ListTag();
		this.messages().forEach(message -> {
			CompoundTag tag = new CompoundTag();
			tag.putString(MESSAGE_KEY, message.messageKey());
			tag.putByte(MESSAGE_SPEAKER, (byte)message.speaker().ordinal());
			list.add(tag);
		});
		ret.put(MESSAGE_LIST, list);
		return ret;
	}
}
