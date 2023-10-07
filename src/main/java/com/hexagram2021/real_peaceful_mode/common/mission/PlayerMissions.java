package com.hexagram2021.real_peaceful_mode.common.mission;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.RealPeacefulMode;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedMissionInstance;
import com.hexagram2021.real_peaceful_mode.common.crafting.menu.MissionMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.hexagram2021.real_peaceful_mode.network.ClientboundMissionMessagePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;

public class PlayerMissions {
	private ServerPlayer player;
	
	private final List<ResourceLocation> activeMissions;
	private final List<ResourceLocation> finishedMissions;
	
	public PlayerMissions(ServerPlayer player) {
		this.player = player;
		this.activeMissions = Lists.newArrayList();
		this.finishedMissions = Lists.newArrayList();
	}
	
	public List<ResourceLocation> getActiveMissions() {
		return this.activeMissions;
	}
	
	public List<ResourceLocation> getFinishedMissions() {
		return this.finishedMissions;
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
	
	public void setPlayer(ServerPlayer player) {
		this.player = player;
	}

	public void replaceWith(PlayerMissions other) {
		this.activeMissions.clear();
		this.finishedMissions.clear();

		this.activeMissions.addAll(other.activeMissions);
		this.finishedMissions.addAll(other.finishedMissions);
	}

	public void receiveNewMission(MissionManager.Mission mission, @Nullable LivingEntity npc, Consumer<ServerPlayer> toDoExtra) {
		if (this.player instanceof FakePlayer) {
			return;
		}

		mission.formers().stream().filter(id -> !IMonsterHero.missionDisabled(id) && !IMonsterHero.completeMission(this, id)).findAny().ifPresentOrElse(
				id -> RPMLogger.debug("Ignore receive mission %s for not finishing mission %s.".formatted(mission.id(), id)),
				() -> {
					MessagedMissionInstance instance = new MessagedMissionInstance(this.player, npc, mission.messages());
					OptionalInt id = this.player.openMenu(new SimpleMenuProvider((counter, inventory, player) ->
							new MissionMessageMenu(counter, instance, () -> {
								this.player.sendSystemMessage(Component.translatable(
										"message.real_peaceful_mode.receive_mission",
										ComponentUtils.wrapInSquareBrackets(
												Component.translatable(getMissionDescriptionId(mission))
														.withStyle(ChatFormatting.GREEN)
														.withStyle((style -> style.withHoverEvent(
																new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(getMissionInformationId(mission)))
														)))
										)
								));
								this.activeMissions.add(mission.id());
								mission.tryGetLoot(this.player, Objects.requireNonNull(this.player.getServer()).getLootData(), false);
								toDoExtra.accept(this.player);
							}), Component.translatable("title.real_peaceful_mode.menu.mission")
					));
					if(id.isPresent()) {
						RealPeacefulMode.packetHandler.send(
								PacketDistributor.PLAYER.with(() -> this.player),
								new ClientboundMissionMessagePacket(instance, id.getAsInt())
						);
					}
				}
		);
	}

	public void finishMission(MissionManager.Mission mission, @Nullable LivingEntity npc, Consumer<ServerPlayer> toDoExtra) {
		if (this.player instanceof FakePlayer) {
			return;
		}

		MessagedMissionInstance instance = new MessagedMissionInstance(this.player, npc, mission.messagesAfter());
		OptionalInt id = this.player.openMenu(new SimpleMenuProvider((counter, inventory, player) ->
				new MissionMessageMenu(counter, instance, () -> {
					this.player.sendSystemMessage(Component.translatable(
							"message.real_peaceful_mode.finish_mission",
							ComponentUtils.wrapInSquareBrackets(
									Component.translatable(getMissionDescriptionId(mission))
											.withStyle(ChatFormatting.GREEN)
											.withStyle((style -> style.withHoverEvent(
													new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable(getMissionInformationId(mission)))
											)))
							)
					));
					this.activeMissions.remove(mission.id());
					if(!mission.isRandomEvent()) {
						this.finishedMissions.add(mission.id());
					}
					mission.finish(this.player, Objects.requireNonNull(this.player.getServer()).getLootData());
					toDoExtra.accept(this.player);
				}), Component.translatable("title.real_peaceful_mode.menu.mission")
		));
		if(id.isPresent()) {
			RealPeacefulMode.packetHandler.send(
					PacketDistributor.PLAYER.with(() -> this.player),
					new ClientboundMissionMessagePacket(instance, id.getAsInt())
			);
		}
	}

	public void removeMission(ResourceLocation id) {
		if (this.player instanceof FakePlayer) {
			return;
		}

		this.activeMissions.remove(id);
	}

	public static String getMissionDescriptionId(MissionManager.Mission mission) {
		ResourceLocation id = mission.id();
		return "mission.%s.%s.name".formatted(id.getNamespace(), id.getPath());
	}

	public static String getMissionInformationId(MissionManager.Mission mission) {
		ResourceLocation id = mission.id();
		return "mission.%s.%s.description".formatted(id.getNamespace(), id.getPath());
	}
}
