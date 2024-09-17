package com.hexagram2021.real_peaceful_mode.common.manager.mission;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.Former;
import com.hexagram2021.real_peaceful_mode.common.register.RPMTriggers;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.function.Function;

public record Mission(ResourceLocation id,
					  List<MissionMessage> messages, List<MissionMessage> messagesAfter,
					  List<Former> formers,
					  EntityType<?> reward, ResourceKey<LootTable> rewardLootTable,
					  boolean lootBefore, boolean isRandomEvent) {
	public static final StreamCodec<RegistryFriendlyByteBuf, Mission> STREAM_CODEC = new StreamCodec<>() {
		private final StreamCodec<RegistryFriendlyByteBuf, EntityType<?>> ENTITY_TYPE_STREAM_CODEC = ByteBufCodecs.registry(Registries.ENTITY_TYPE);
		private final StreamCodec<ByteBuf, ResourceKey<LootTable>> LOOT_TABLE_KEY_STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(rewardLootTableID -> ResourceKey.create(Registries.LOOT_TABLE, rewardLootTableID), ResourceKey::location);

		@Override
		public Mission decode(RegistryFriendlyByteBuf buf) {
			ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
			EntityType<?> reward = ENTITY_TYPE_STREAM_CODEC.decode(buf);
			ResourceKey<LootTable> rewardLootTable = LOOT_TABLE_KEY_STREAM_CODEC.decode(buf);
			boolean lootBefore = ByteBufCodecs.BOOL.decode(buf);
			boolean isRandomEvent = ByteBufCodecs.BOOL.decode(buf);
			return new Mission(id, List.of(), List.of(), List.of(), reward, rewardLootTable, lootBefore, isRandomEvent);
		}
		@Override
		public void encode(RegistryFriendlyByteBuf buf, Mission input) {
			ResourceLocation.STREAM_CODEC.encode(buf, input.id());
			ENTITY_TYPE_STREAM_CODEC.encode(buf, input.reward());
			LOOT_TABLE_KEY_STREAM_CODEC.encode(buf, input.rewardLootTable());
			ByteBufCodecs.BOOL.encode(buf, input.lootBefore());
			ByteBufCodecs.BOOL.encode(buf, input.isRandomEvent());
		}
	};

	static Mission fromJson(ImmutableSet.Builder<EntityType<?>> friendlyMonstersBuilder, ResourceLocation id, JsonObject json) {
		List<MissionMessage> messages = MissionMessage.LIST_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "messages")).getOrThrow(IllegalStateException::new);
		List<MissionMessage> messagesAfter = MissionMessage.LIST_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "messagesAfter")).getOrThrow(IllegalStateException::new);
		List<Former> formers = Former.LIST_CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonArray(json, "requires")).getOrThrow(IllegalStateException::new);
		ResourceLocation reward = ResourceLocation.parse(GsonHelper.getAsString(json, "reward", "minecraft:player"));
		EntityType<?> rewardEntityType = BuiltInRegistries.ENTITY_TYPE.get(reward);
		if (rewardEntityType != EntityType.PLAYER) {
			friendlyMonstersBuilder.add(rewardEntityType);
		}
		ResourceLocation rewardLootTable = ResourceLocation.parse(GsonHelper.getAsString(json, "loot_table", BuiltInLootTables.EMPTY.location().toString()));
		boolean lootBefore = GsonHelper.getAsBoolean(json, "loot_before", false);
		boolean isRandomEvent = GsonHelper.getAsBoolean(json, "random_event", false);
		return new Mission(id, messages, messagesAfter, formers, rewardEntityType, ResourceKey.create(Registries.LOOT_TABLE, rewardLootTable), lootBefore, isRandomEvent);
	}

	public void tryGetLoot(ServerPlayer player, Function<ResourceKey<LootTable>, LootTable> lootTableGetter, boolean finished) {
		if (this.lootBefore != finished) {
			if (!this.rewardLootTable.equals(BuiltInLootTables.EMPTY)) {
				LootTable lootTable = lootTableGetter.apply(this.rewardLootTable);
				lootTable.getRandomItems(new LootParams.Builder(player.serverLevel()).create(LootContextParamSets.EMPTY), itemStack -> player.level().addFreshEntity(
						new ItemEntity(player.level(), player.getX(), player.getY() + 0.5D, player.getZ(), itemStack)
				));
			}
		}
	}

	public void finish(ServerPlayer player, Function<ResourceKey<LootTable>, LootTable> lootTableGetter) {
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
		this.tryGetLoot(player, lootTableGetter, true);
		RPMTriggers.MISSION_FINISH.get().trigger(player, this.reward.equals(EntityType.PLAYER) ? null : this.reward);
	}
}
