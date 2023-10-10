package com.hexagram2021.real_peaceful_mode.common.mission;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.register.RPMTriggers;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;

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
				if (entry.getValue().isJsonObject() && !processConditions(entry.getValue().getAsJsonObject())) {
					RPMLogger.debug("Skipping loading mission %s as it's conditions were not met".formatted(id));
					continue;
				}
				JsonObject jsonObject = GsonHelper.convertToJsonObject(entry.getValue(), "top element");
				Mission mission = Mission.fromJson(friendlyMonstersBuilder, id, jsonObject);
				builder.put(id, mission);
			} catch (IllegalArgumentException | JsonParseException exception) {
				RPMLogger.error("Parsing error loading mission %s.".formatted(id));
				RPMLogger.error(exception);
			}
		}
		this.missionsByName = builder.build();
		this.friendlyMonsters = friendlyMonstersBuilder.build();
	}

	public record Mission(ResourceLocation id,
						  List<Message> messages, List<Message> messagesAfter,
						  List<ResourceLocation> formers,
						  EntityType<?> reward, ResourceLocation rewardLootTable,
						  boolean lootBefore, boolean isRandomEvent) {
		public record Message(String messageKey, Speaker speaker) {
			public enum Speaker {
				PLAYER,
				NPC
			}
		}

		private static Mission fromJson(ImmutableSet.Builder<EntityType<?>> friendlyMonstersBuilder, ResourceLocation id, JsonObject json) {
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
			if(rewardEntityType == null) {
				rewardEntityType = EntityType.PLAYER;
			} else if(rewardEntityType != EntityType.PLAYER) {
				friendlyMonstersBuilder.add(rewardEntityType);
			}
			ResourceLocation rewardLootTable = new ResourceLocation(GsonHelper.getAsString(json, "loot_table", BuiltInLootTables.EMPTY.toString()));
			boolean lootBefore = GsonHelper.getAsBoolean(json, "loot_before", false);
			boolean isRandomEvent = GsonHelper.getAsBoolean(json, "random_event", false);
			return new Mission(id, messages, messagesAfter, formers, rewardEntityType, rewardLootTable, lootBefore, isRandomEvent);
		}

		public void tryGetLoot(ServerPlayer player, LootDataManager lootTables, boolean finished) {
			if(this.lootBefore != finished) {
				if (!this.rewardLootTable.equals(BuiltInLootTables.EMPTY)) {
					LootTable lootTable = lootTables.getLootTable(this.rewardLootTable);
					lootTable.getRandomItems(new LootParams.Builder((ServerLevel) player.level()).create(LootContextParamSets.EMPTY), itemStack -> player.level().addFreshEntity(
							new ItemEntity(player.level(), player.getX(), player.getY() + 0.5D, player.getZ(), itemStack)
					));
				}
			}
		}

		public void finish(ServerPlayer player, LootDataManager lootTables) {
			if(!this.reward.equals(EntityType.PLAYER)) {
				if(!((IMonsterHero)player).isHero(this.reward)) {
					player.sendSystemMessage(Component.translatable(
							"message.real_peaceful_mode.reward_monster",
							player.getDisplayName(),
							Component.translatable(this.reward.getDescriptionId()).withStyle(ChatFormatting.GREEN)
					));
					((IMonsterHero) player).setHero(this.reward);
				}
			}
			this.tryGetLoot(player, lootTables, true);
			RPMTriggers.MISSION_FINISH.trigger(player, this.reward.equals(EntityType.PLAYER) ? null : this.reward);
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
				String speakerType = GsonHelper.getAsString(json, "speaker");
				Mission.Message.Speaker speaker = switch(speakerType) {
					case "player" -> Mission.Message.Speaker.PLAYER;
					case "npc" -> Mission.Message.Speaker.NPC;
					default -> throw new IllegalArgumentException("No speaker named \"%s\"!".formatted(speakerType));
				};
				messages.add(new Mission.Message(GsonHelper.getAsString(json, "key"), speaker));
			} else if(element.isJsonPrimitive()) {
				String message = element.getAsString();
				messages.add(new Mission.Message(message, Mission.Message.Speaker.PLAYER));
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

	public Stream<EntityType<?>> getAllFriendlyMonsters() {
		return this.friendlyMonsters.stream();
	}
}
