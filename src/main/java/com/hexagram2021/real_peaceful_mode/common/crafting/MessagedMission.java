package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.common.manager.mission.MissionMessage;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;

public interface MessagedMission {
	Player player();

	@Nullable
	LivingEntity npc();

	List<MissionMessage> messages();
	
	String TAG_NPC = "npc";
	String TAG_MESSAGE_LIST = "messages";
	default CompoundTag createTag() {
		CompoundTag ret = new CompoundTag();
		LivingEntity npc = this.npc();
		if(npc != null) {
			ret.putInt(TAG_NPC, npc.getId());
		}
		ret.put(TAG_MESSAGE_LIST, MissionMessage.LIST_CODEC.encode(this.messages(), NbtOps.INSTANCE, new ListTag()).getOrThrow(false, RPMLogger::error));
		return ret;
	}
}
