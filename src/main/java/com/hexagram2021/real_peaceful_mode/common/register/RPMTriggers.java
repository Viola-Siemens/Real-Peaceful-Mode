package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.util.triggers.MissionFinishTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class RPMTriggers {
	public static final MissionFinishTrigger MISSION_FINISH = CriteriaTriggers.register(new MissionFinishTrigger());

	public static void init() {

	}
}
