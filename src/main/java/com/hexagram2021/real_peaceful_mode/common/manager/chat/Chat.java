package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.google.gson.JsonObject;
import com.hexagram2021.real_peaceful_mode.common.manager.Former;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;

public record Chat(ResourceLocation id, EntityType<?> entityType, List<Former> formers, boolean triggerAnyway, AbstractChatMessage message) {
	static Chat fromJson(ResourceLocation id, JsonObject json) {
		ResourceLocation entityType = ResourceLocation.parse(GsonHelper.getAsString(json, "entity_type"));
		EntityType<?> chatEntityType = BuiltInRegistries.ENTITY_TYPE.get(entityType);
		List<Former> formers = Former.LIST_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "requires")).getOrThrow(IllegalStateException::new);
		boolean triggerAnyway = GsonHelper.getAsBoolean(json, "trigger_anyway", true);
		AbstractChatMessage message = AbstractChatMessage.REGISTRY_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "message")).getOrThrow(IllegalStateException::new);
		return new Chat(id, chatEntityType, formers, triggerAnyway, message);
	}

	public boolean hasAvailableSelections(ServerPlayer player, LivingEntity npc) {
		return hasAvailableSelections(this.message, player, npc);
	}

	private static boolean hasAvailableSelections(@Nullable AbstractChatMessage chat, ServerPlayer player, LivingEntity npc) {
		if(chat == null) {
			return true;
		}
		List<ChatSelection> chatSelections = chat.getSelections();
		if(chatSelections != null) {
			return chatSelections.stream().anyMatch(selection -> hasAvailableSelections(selection.getNext(), player, npc) && selection.canShowFor(player, npc));
		}
		return hasAvailableSelections(chat.getNext(), player, npc);
	}
}
