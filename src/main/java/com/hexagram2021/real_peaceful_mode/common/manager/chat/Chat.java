package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.google.gson.JsonObject;
import com.hexagram2021.real_peaceful_mode.common.manager.Former;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public record Chat(ResourceLocation id, EntityType<?> entityType, List<Former> formers, AbstractChatMessage message) {
	static Chat fromJson(ResourceLocation id, JsonObject json) {
		ResourceLocation entityType = new ResourceLocation(GsonHelper.getAsString(json, "entity_type"));
		EntityType<?> chatEntityType = ForgeRegistries.ENTITY_TYPES.getValue(entityType);
		if(chatEntityType == null) {
			throw new IllegalArgumentException("Unknown entity type: %s".formatted(entityType));
		}
		List<Former> formers = Former.LIST_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "requires")).getOrThrow(false, RPMLogger::error);
		AbstractChatMessage message = AbstractChatMessage.REGISTRY_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "message")).getOrThrow(false, RPMLogger::error);
		return new Chat(id, chatEntityType, formers, message);
	}
}
