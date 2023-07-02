package com.hexagram2021.real_peaceful_mode.common.mission;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedMissionInstance;
import com.hexagram2021.real_peaceful_mode.common.crafting.menus.MissionMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.phys.AABB;
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

	public void receiveNewMission(MissionManager.Mission mission) {
		if (this.player instanceof FakePlayer) {
			return;
		}

		//TODO: 可以重写为靠近方块触发任务
		mission.formers().stream().filter(id -> !this.finishedMissions.contains(id)).findAny().ifPresentOrElse(
				id -> RPMLogger.debug("Ignore receive mission %s for not finishing mission %s.".formatted(mission.id(), id)),
				() -> this.player.openMenu(new SimpleMenuProvider((counter, inventory, player) ->
						new MissionMessageMenu(counter, new MessagedMissionInstance(
								player, npc, mission.messages()
						), () -> {
							this.player.sendSystemMessage(Component.translatable("message.real_peaceful_mode.receive_mission", Component.translatable(getMissionDescriptionId(mission))));
							this.activeMissions().add(mission.id());
						})
				))
		);
	}

	public void finishMission(MissionManager.Mission mission) {
		if (this.player instanceof FakePlayer) {
			return;
		}

		this.player.openMenu(new SimpleMenuProvider((counter, inventory, player) ->
				new MissionMessageMenu(counter, new MessagedMissionInstance(
						player, npc, mission.messagesAfter()
				), () -> {
					this.player.sendSystemMessage(Component.translatable("message.real_peaceful_mode.finish_mission", Component.translatable(getMissionDescriptionId(mission))));
					this.activeMissions().remove(mission.id());
					this.finishedMissions().add(mission.id());
				})
		)
	}

	public static String getMissionDescriptionId(MissionManager.Mission mission) {
		ResourceLocation id = mission.id();
		return "message.%s.mission.name.%s".formatted(id.getNamespace(), id.getPath())
	}

	public finishMission(MissionManager.Mission mission) {

	}

	@SuppressWarnings("unchecked")
	private void registerAcceptListeners(MissionManager.Mission mission) {
		if(!this.finishedMissions.contains(mission.id())) {
			CriterionTriggerInstance triggerInstance = mission.accept().getTrigger();
			if (triggerInstance != null) {
				IMissionCriterionTrigger<CriterionTriggerInstance> trigger = (IMissionCriterionTrigger<CriterionTriggerInstance>)CriteriaTriggers.getCriterion(triggerInstance.getCriterion());
				if (trigger != null) {
					trigger.addPlayerAcceptListener(this, new IMissionCriterionTrigger.Listener<>(triggerInstance, mission));
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
					trigger.removePlayerAcceptListener(this, new IMissionCriterionTrigger.Listener<>(triggerInstance, mission));
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
					trigger.addPlayerFinishListener(this, new IMissionCriterionTrigger.Listener<>(triggerInstance, mission));
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
					trigger.removePlayerFinishListener(this, new IMissionCriterionTrigger.Listener<>(triggerInstance, mission));
				}
			}
		}
	}
}
