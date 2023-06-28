package com.hexagram2021.real_peaceful_mode.common.mission;

import com.google.common.collect.Lists;

import java.util.List;

public record PlayerMissions(List<MissionManager.Mission> activeMissions, List<MissionManager.Mission> finishedMissions) {
	public PlayerMissions() {
		this(Lists.newArrayList(), Lists.newArrayList());
	}

	public void finishMission() {

	}
}
