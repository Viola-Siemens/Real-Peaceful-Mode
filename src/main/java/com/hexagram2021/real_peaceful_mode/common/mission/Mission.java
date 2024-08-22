package com.hexagram2021.real_peaceful_mode.common.mission;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.register.RPMTriggers;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Function;

public record Mission(ResourceLocation id,
					  List<Message> messages, List<Message> messagesAfter,
					  List<Former> formers,
					  EntityType<?> reward, ResourceLocation rewardLootTable,
					  boolean lootBefore, boolean isRandomEvent) {
	public record Message(String messageKey, Speaker speaker) {
		public static final Codec<Message> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("key").forGetter(Message::messageKey),
				Speaker.CODEC.optionalFieldOf("speaker", Speaker.PLAYER).forGetter(Message::speaker)
		).apply(instance, Message::new));
		public static final Codec<Message> CODEC = Codec.either(DIRECT_CODEC, Codec.STRING)
				.xmap(either -> either.map(Function.identity(), key -> new Message(key, Speaker.PLAYER)), Either::left);
		public static final Codec<List<Message>> LIST_CODEC = CODEC.listOf();
	}

	static Mission fromJson(ImmutableSet.Builder<EntityType<?>> friendlyMonstersBuilder, ResourceLocation id, JsonObject json) {
		List<Message> messages = Message.LIST_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "messages")).getOrThrow(false, RPMLogger::error);
		List<Message> messagesAfter = Message.LIST_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "messagesAfter")).getOrThrow(false, RPMLogger::error);
		List<Former> formers = Former.LIST_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "requires")).getOrThrow(false, RPMLogger::error);
		ResourceLocation reward = new ResourceLocation(GsonHelper.getAsString(json, "reward", "minecraft:player"));
		EntityType<?> rewardEntityType = ForgeRegistries.ENTITY_TYPES.getValue(reward);
		if (rewardEntityType == null) {
			rewardEntityType = EntityType.PLAYER;
		} else if (rewardEntityType != EntityType.PLAYER) {
			friendlyMonstersBuilder.add(rewardEntityType);
		}
		ResourceLocation rewardLootTable = new ResourceLocation(GsonHelper.getAsString(json, "loot_table", BuiltInLootTables.EMPTY.toString()));
		boolean lootBefore = GsonHelper.getAsBoolean(json, "loot_before", false);
		boolean isRandomEvent = GsonHelper.getAsBoolean(json, "random_event", false);
		return new Mission(id, messages, messagesAfter, formers, rewardEntityType, rewardLootTable, lootBefore, isRandomEvent);
	}

	public void tryGetLoot(ServerPlayer player, LootDataManager lootTables, boolean finished) {
		if (this.lootBefore != finished) {
			if (!this.rewardLootTable.equals(BuiltInLootTables.EMPTY)) {
				LootTable lootTable = lootTables.getLootTable(this.rewardLootTable);
				lootTable.getRandomItems(new LootParams.Builder((ServerLevel) player.level()).create(LootContextParamSets.EMPTY), itemStack -> player.level().addFreshEntity(
						new ItemEntity(player.level(), player.getX(), player.getY() + 0.5D, player.getZ(), itemStack)
				));
			}
		}
	}

	public void finish(ServerPlayer player, LootDataManager lootTables) {
		if (!this.reward.equals(EntityType.PLAYER)) {
			if (!((IMonsterHero) player).rpm$isHero(this.reward)) {
				player.sendSystemMessage(Component.translatable(
						"message.real_peaceful_mode.reward_monster",
						player.getDisplayName(),
						Component.translatable(this.reward.getDescriptionId()).withStyle(ChatFormatting.GREEN)
				));
				((IMonsterHero) player).rpm$setHero(this.reward);
			}
		}
		this.tryGetLoot(player, lootTables, true);
		RPMTriggers.MISSION_FINISH.trigger(player, this.reward.equals(EntityType.PLAYER) ? null : this.reward);
	}
}
