package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MessagedMission {
	Player player();

	@Nullable
	LivingEntity npc();

	List<MissionManager.Mission.Message> messages();
}
