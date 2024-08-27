package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.common.manager.chat.AbstractChatMessage;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface MessagedChat {
	Player player();

	LivingEntity npc();

	AbstractChatMessage message();

	String TAG_NPC = "npc";
	String TAG_MESSAGE = "message";
	default CompoundTag createTag() {
		CompoundTag ret = new CompoundTag();
		ret.putInt(TAG_NPC, this.npc().getId());
		ret.put(TAG_MESSAGE, AbstractChatMessage.REGISTRY_CODEC.encode(this.message(), NbtOps.INSTANCE, new CompoundTag()).getOrThrow(false, RPMLogger::error));
		return ret;
	}
}
