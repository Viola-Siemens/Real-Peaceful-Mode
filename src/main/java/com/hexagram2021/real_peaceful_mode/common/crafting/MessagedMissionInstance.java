package com.hexagram2021.real_peaceful_mode.common.crafting;

import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;

public record MessagedMissionInstance(Player player, @Nullable LivingEntity npc, List<MissionManager.Mission.Message> messages) implements MessagedMission {
}
