package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.google.gson.JsonObject;
import com.hexagram2021.real_peaceful_mode.common.manager.Former;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public record Chat(ResourceLocation id, EntityType<?> entityType, List<Former> formers, AbstractChatMessage message) {
	static Chat fromJson(ResourceLocation id, JsonObject json) {
		ResourceLocation entityType = ResourceLocation.parse(GsonHelper.getAsString(json, "entity_type"));
		EntityType<?> chatEntityType = BuiltInRegistries.ENTITY_TYPE.get(entityType);
		List<Former> formers = Former.LIST_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "requires")).getOrThrow(IllegalStateException::new);
		AbstractChatMessage message = AbstractChatMessage.REGISTRY_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "message")).getOrThrow(IllegalStateException::new);
		return new Chat(id, chatEntityType, formers, message);
	}
}
