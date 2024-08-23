package com.hexagram2021.real_peaceful_mode.common.manager.mission;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import com.hexagram2021.real_peaceful_mode.common.manager.MissionLoadCondition;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;

import java.util.*;
import java.util.stream.Stream;

public class MissionManager extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();

	private Map<ResourceLocation, Mission> missionsByName = ImmutableMap.of();
	private Set<EntityType<?>> friendlyMonsters = ImmutableSet.of();

	public MissionManager() {
		super(GSON, "rpm/missions");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> missions, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		ImmutableMap.Builder<ResourceLocation, Mission> builder = ImmutableMap.builder();
		ImmutableSet.Builder<EntityType<?>> friendlyMonstersBuilder = ImmutableSet.builder();
		for(Map.Entry<ResourceLocation, JsonElement> entry: missions.entrySet()) {
			ResourceLocation id = entry.getKey();
			if (id.getPath().startsWith("_")) {
				continue;
			}

			try {
				if (entry.getValue().isJsonObject() && !MissionLoadCondition.processConditions(entry.getValue().getAsJsonObject())) {
					RPMLogger.debug("Skipping loading mission %s as it's conditions were not met".formatted(id));
					continue;
				}
				JsonObject jsonObject = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
				Mission mission = Mission.fromJson(friendlyMonstersBuilder, id, jsonObject);
				builder.put(id, mission);
			} catch (IllegalArgumentException | JsonParseException exception) {
				RPMLogger.error("Parsing error loading mission %s.".formatted(id), exception);
			}
		}
		this.missionsByName = builder.build();
		this.friendlyMonsters = friendlyMonstersBuilder.build();
		RPMLogger.info("Loaded %d missions of %d monsters.".formatted(this.missionsByName.size(), this.friendlyMonsters.size()));
	}

	public Optional<Mission> getMission(ResourceLocation id) {
		return Optional.ofNullable(this.missionsByName.get(id));
	}

	public Stream<ResourceLocation> getAllMissionIds() {
		return this.missionsByName.keySet().stream();
	}

	public Collection<Mission> getAllMissions() {
		return this.missionsByName.values();
	}

	public Stream<EntityType<?>> getAllFriendlyMonsters() {
		return this.friendlyMonsters.stream();
	}
}
