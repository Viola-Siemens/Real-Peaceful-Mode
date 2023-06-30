package com.hexagram2021.real_peaceful_mode.common.mission;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;

import java.nio.file.Path;
import java.util.List;

public record PlayerMissions(Path playerSavePath, ServerPlayer player, List<ResourceLocation> activeMissions, List<ResourceLocation> finishedMissions) {
	public PlayerMissions(Path playerSavePath, ServerPlayer player) {
		this(playerSavePath, player, Lists.newArrayList(), Lists.newArrayList());
	}

	private static final String PLAYER_MISSIONS = "RealPeacefulModeMissions";
	private static final String ACTIVE_MISSIONS = "activeMissions";
	private static final String FINISHED_MISSIONS = "finishedMissions";
	public void readNBT(CompoundTag nbt) {
		this.activeMissions.clear();
		this.finishedMissions.clear();

		if(nbt.contains(PLAYER_MISSIONS, Tag.TAG_COMPOUND)) {
			CompoundTag missions = nbt.getCompound(PLAYER_MISSIONS);
			ListTag activeMissions = missions.getList(ACTIVE_MISSIONS, Tag.TAG_STRING);
			ListTag finishedMissions = missions.getList(FINISHED_MISSIONS, Tag.TAG_STRING);
			for(Tag tag: activeMissions) {
				this.activeMissions.add(new ResourceLocation(tag.getAsString()));
			}
			for(Tag tag: finishedMissions) {
				this.finishedMissions.add(new ResourceLocation(tag.getAsString()));
			}
		}
	}

	public void writeNBT(CompoundTag nbt) {
		ListTag activeMissions = new ListTag();
		ListTag finishedMissions = new ListTag();
		for(ResourceLocation id: this.activeMissions) {
			activeMissions.add(StringTag.valueOf(id.toString()));
		}
		for(ResourceLocation id: this.finishedMissions) {
			finishedMissions.add(StringTag.valueOf(id.toString()));
		}
		CompoundTag missions = new CompoundTag();
		missions.put(ACTIVE_MISSIONS, activeMissions);
		missions.put(FINISHED_MISSIONS, finishedMissions);

		nbt.put(PLAYER_MISSIONS, missions);
	}

	public void replaceWith(PlayerMissions other) {
		this.activeMissions.clear();
		this.finishedMissions.clear();

		this.activeMissions.addAll(other.activeMissions);
		this.finishedMissions.addAll(other.finishedMissions);
	}

	public void award(MissionManager.Mission mission) {
		if (this.player instanceof FakePlayer) {
			return;
		}

		mission.formers().stream().filter(id -> !this.finishedMissions.contains(id)).findAny().ifPresentOrElse(
				id -> RPMLogger.debug("Ignore award mission %s for not finishing mission %s.".formatted(mission.id(), id)),
				() -> {
					//this.player.openMenu();
					//TODO
				}
		);
	}

	@SuppressWarnings("unchecked")
	private void registerAcceptListeners(MissionManager.Mission mission) {
		if(!this.finishedMissions.contains(mission.id())) {
			CriterionTriggerInstance triggerInstance = mission.accept().getTrigger();
			if (triggerInstance != null) {
				IMissionCriterionTrigger<CriterionTriggerInstance> trigger = (IMissionCriterionTrigger<CriterionTriggerInstance>)CriteriaTriggers.getCriterion(triggerInstance.getCriterion());
				if (trigger != null) {
					trigger.addPlayerListener(this, new IMissionCriterionTrigger.Listener<>(triggerInstance, mission));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void unregisterAcceptListeners(MissionManager.Mission mission) {
		if(!this.finishedMissions.contains(mission.id())) {
			CriterionTriggerInstance triggerInstance = mission.accept().getTrigger();
			if (triggerInstance != null) {
				IMissionCriterionTrigger<CriterionTriggerInstance> trigger = (IMissionCriterionTrigger<CriterionTriggerInstance>)CriteriaTriggers.getCriterion(triggerInstance.getCriterion());
				if (trigger != null) {
					trigger.removePlayerListener(this, new IMissionCriterionTrigger.Listener<>(triggerInstance, mission));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void registerFinishListeners(MissionManager.Mission mission) {
		if(!this.finishedMissions.contains(mission.id())) {
			CriterionTriggerInstance triggerInstance = mission.finish().getTrigger();
			if (triggerInstance != null) {
				IMissionCriterionTrigger<CriterionTriggerInstance> trigger = (IMissionCriterionTrigger<CriterionTriggerInstance>)CriteriaTriggers.getCriterion(triggerInstance.getCriterion());
				if (trigger != null) {
					trigger.addPlayerListener(this, new IMissionCriterionTrigger.Listener<>(triggerInstance, mission));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void unregisterFinishListeners(MissionManager.Mission mission) {
		if(!this.finishedMissions.contains(mission.id())) {
			CriterionTriggerInstance triggerInstance = mission.finish().getTrigger();
			if (triggerInstance != null) {
				IMissionCriterionTrigger<CriterionTriggerInstance> trigger = (IMissionCriterionTrigger<CriterionTriggerInstance>)CriteriaTriggers.getCriterion(triggerInstance.getCriterion());
				if (trigger != null) {
					trigger.removePlayerListener(this, new IMissionCriterionTrigger.Listener<>(triggerInstance, mission));
				}
			}
		}
	}
}
