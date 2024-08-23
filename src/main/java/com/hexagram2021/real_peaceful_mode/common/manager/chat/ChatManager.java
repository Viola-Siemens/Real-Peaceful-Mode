package com.hexagram2021.real_peaceful_mode.common.manager.chat;

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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class ChatManager extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();

	private Map<ResourceLocation, Chat> chatByName = ImmutableMap.of();
	private Set<EntityType<?>> chatableEntities = ImmutableSet.of();

	public ChatManager() {
		super(GSON, "rpm/chats");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> chats, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		ImmutableMap.Builder<ResourceLocation, Chat> builder = ImmutableMap.builder();
		ImmutableSet.Builder<EntityType<?>> chatableEntitiesBuilder = ImmutableSet.builder();
		for(Map.Entry<ResourceLocation, JsonElement> entry: chats.entrySet()) {
			ResourceLocation id = entry.getKey();
			if (id.getPath().startsWith("_")) {
				continue;
			}

			try {
				if (entry.getValue().isJsonObject() && !MissionLoadCondition.processConditions(entry.getValue().getAsJsonObject())) {
					RPMLogger.debug("Skipping loading chat %s as it's conditions were not met".formatted(id));
					continue;
				}
				JsonObject jsonObject = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
				Chat chat = Chat.fromJson(chatableEntitiesBuilder, id, jsonObject);
				builder.put(id, chat);
			} catch (IllegalArgumentException | JsonParseException exception) {
				RPMLogger.error("Parsing error loading chat %s.".formatted(id), exception);
			}
		}
		this.chatByName = builder.build();
		this.chatableEntities = chatableEntitiesBuilder.build();
		RPMLogger.info("Loaded %d chats of %d entities.".formatted(this.chatByName.size(), this.chatableEntities.size()));
	}

	public Optional<Chat> getChat(ResourceLocation id) {
		return Optional.ofNullable(this.chatByName.get(id));
	}

	public Stream<ResourceLocation> getAllChatIds() {
		return this.chatByName.keySet().stream();
	}

	public Collection<Chat> getAllChats() {
		return this.chatByName.values();
	}

	public Stream<EntityType<?>> getAllChatableMobs() {
		return this.chatableEntities.stream();
	}
}
