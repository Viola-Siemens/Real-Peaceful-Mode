package com.hexagram2021.real_peaceful_mode.common.mission;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class MissionManager extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();

	private Map<ResourceLocation, Mission> missionsByName = ImmutableMap.of();

	public MissionManager() {
		super(GSON, "rpm/missions");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> missions, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		ImmutableMap.Builder<ResourceLocation, Mission> builder = ImmutableMap.builder();
		for(Map.Entry<ResourceLocation, JsonElement> entry: missions.entrySet()) {
			ResourceLocation id = entry.getKey();
			if (id.getPath().startsWith("_")) {
				continue;
			}

			try {
				if (entry.getValue().isJsonObject() && !processConditions(entry.getValue().getAsJsonObject())) {
					RPMLogger.debug("Skipping loading recipe %s as it's conditions were not met".formatted(id));
					continue;
				}
				JsonObject jsonObject = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
				Mission mission = Mission.fromJson(id, jsonObject);
				builder.put(id, mission);
			} catch (IllegalArgumentException | JsonParseException exception) {
				RPMLogger.error("Parsing error loading mission %s.".formatted(id));
				RPMLogger.error(exception);
			}
		}
		this.missionsByName = builder.build();
	}

	public record Mission(ResourceLocation id, List<Message> messages, List<Message> messagesAfter, List<ResourceLocation> formers, EntityType<?> reward) {
		public record Message(String messageKey, EntityType<?> entityType) {
		}

		private static Mission fromJson(ResourceLocation id, JsonObject json) {
			List<Mission.Message> messages = Lists.newArrayList();
			List<Mission.Message> messagesAfter = Lists.newArrayList();
			JsonArray messageArray = GsonHelper.getAsJsonArray(json, "messages");
			JsonArray messageAfterArray = GsonHelper.getAsJsonArray(json, "messagesAfter");
			getMessages(messages, messageArray);
			getMessages(messagesAfter, messageAfterArray);
			JsonArray requires = GsonHelper.getAsJsonArray(json, "requires");
			List<ResourceLocation> formers = Lists.newArrayList();
			for(JsonElement element: requires) {
				String otherId = GsonHelper.convertToString(element, "elements of requires");
				ResourceLocation former = new ResourceLocation(otherId);
				formers.add(former);
			}
			ResourceLocation reward = new ResourceLocation(GsonHelper.getAsString(json, "reward", "minecraft:player"));
			EntityType<?> rewardEntityType = ForgeRegistries.ENTITY_TYPES.getValue(reward);
			return new Mission(id, messages, messagesAfter, formers, rewardEntityType == null ? EntityType.PLAYER : rewardEntityType);
		}

		public void finish(IMonsterHero hero) {
			if(!this.reward.equals(EntityType.PLAYER)) {
				hero.setHero(this.reward);
			}
		}
	}

	private static final String CONDITIONS_FIELD = "conditions";
	private static boolean processConditions(JsonObject json) {
		return !json.has(CONDITIONS_FIELD) || MissionLoadCondition.fromJson(json.get(CONDITIONS_FIELD)).test();
	}

	private static void getMessages(List<Mission.Message> messages, JsonArray messageArray) {
		for(JsonElement element: messageArray) {
			if(element.isJsonObject()) {
				JsonObject json = element.getAsJsonObject();
				String entityTypeId = GsonHelper.getAsString(json, "entity_type");
				EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityTypeId));
				if(entityType == null) {
					throw new IllegalArgumentException("No entity type named \"%s\"!".formatted(entityTypeId));
				}
				messages.add(new Mission.Message(GsonHelper.getAsString(json, "key"), entityType));
			} else if(element.isJsonPrimitive()) {
				String message = element.getAsString();
				messages.add(new Mission.Message(message, EntityType.PLAYER));
			} else {
				throw new IllegalArgumentException("Field \"messages\" must be an array of strings and json objects!");
			}
		}
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
}
