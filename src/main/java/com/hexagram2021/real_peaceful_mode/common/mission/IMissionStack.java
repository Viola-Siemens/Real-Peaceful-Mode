package com.hexagram2021.real_peaceful_mode.common.mission;

import com.hexagram2021.real_peaceful_mode.api.MissionType;
import net.minecraft.resources.ResourceLocation;

public interface IMissionStack {
	ResourceLocation missionId();
	MissionType type();
}
