package com.hexagram2021.real_peaceful_mode.common.mission;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.network.FriendlyByteBuf;
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

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public class MissionManager extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();

	private Map<ResourceLocation, Mission> missionsByName = ImmutableMap.of();
	private Map<ResourceLocation, Mission> missionsByAdvancement = ImmutableMap.of();

	public MissionManager() {
		super(GSON, "rpm/missions");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> missions, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		ImmutableMap.Builder<ResourceLocation, Mission> builder = ImmutableMap.builder();
		ImmutableMap.Builder<ResourceLocation, Mission> builderByAdvancements = ImmutableMap.builder();
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
				builderByAdvancements.put(mission.advancementId, mission);
			} catch (IllegalArgumentException | JsonParseException exception) {
				RPMLogger.error("Parsing error loading mission %s.".formatted(id));
				RPMLogger.error(exception);
			}
		}
		this.missionsByName = builder.build();
		this.missionsByAdvancement = builderByAdvancements.build();
	}

	public record Mission(ResourceLocation id, ResourceLocation advancementId, List<Message> messages, List<ResourceLocation> formers) {
		public record Message(String messageKey, EntityType<?> entityType) {
			public static Message fromNetwork(FriendlyByteBuf buf) {
				String key = buf.readUtf();
				ResourceLocation entityTypeId = buf.readResourceLocation();
				EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityTypeId);
				if(entityType == null) {
					throw new IllegalArgumentException("No entity type named \"%s\"!".formatted(entityTypeId));
				}
				return new Message(key, entityType);
			}

			public void toNetwork(FriendlyByteBuf buf) {
				buf.writeUtf(this.messageKey);
				buf.writeResourceLocation(getRegistryName(this.entityType));
			}
		}

		public static Mission fromNetwork(FriendlyByteBuf buf) {
			ResourceLocation id = buf.readResourceLocation();
			ResourceLocation advancementId = buf.readResourceLocation();
			List<Message> messages = buf.readCollection(Lists::newArrayListWithCapacity, Message::fromNetwork);
			List<ResourceLocation> formers = buf.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readResourceLocation);

			return new Mission(id, advancementId, messages, formers);
		}

		public void toNetwork(FriendlyByteBuf buf) {
			buf.writeResourceLocation(this.id);
			buf.writeResourceLocation(this.advancementId);
			buf.writeCollection(this.messages, (writeBuf, message) -> message.toNetwork(writeBuf));
			buf.writeCollection(this.formers, FriendlyByteBuf::writeResourceLocation);
		}

		private static Mission fromJson(ResourceLocation id, JsonObject json) {
			if(json.has("advancement_id")) {
				ResourceLocation advId = new ResourceLocation(json.get("advancement_id").getAsString());
				List<Mission.Message> messages = Lists.newArrayList();
				JsonArray messageArray = GsonHelper.getAsJsonArray(json, "messages");
				for(JsonElement element: messageArray) {
					if(element.isJsonObject()) {
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
				JsonArray requires = GsonHelper.getAsJsonArray(json, "requires");
				List<ResourceLocation> formers = Lists.newArrayList();
				for(JsonElement element: requires) {
					String otherId = GsonHelper.convertToString(element, "elements of requires");
					ResourceLocation former = new ResourceLocation(otherId);
					formers.add(former);
				}
				return new Mission(id, advId, messages, formers);
			}
			throw new IllegalArgumentException("Field \"advancement_id\" is not present!");
		}
	}

	private static final String CONDITIONS_FIELD = "conditions";
	private static boolean processConditions(JsonObject json) {
		return !json.has(CONDITIONS_FIELD) && MissionLoadCondition.fromJson(json.get(CONDITIONS_FIELD)).test();
	}

	public Optional<Mission> getMission(ResourceLocation id) {
		return Optional.ofNullable(this.missionsByName.get(id));
	}

	public Optional<Mission> getMissionByAdvancementId(ResourceLocation id) {
		return Optional.ofNullable(this.missionsByAdvancement.get(id));
	}

	public Stream<ResourceLocation> getAllMissionIds() {
		return this.missionsByName.keySet().stream();
	}

	public Collection<Mission> getAllMissions() {
		return this.missionsByName.values();
	}
}
