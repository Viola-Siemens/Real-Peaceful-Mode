package com.hexagram2021.real_peaceful_mode.server.commands;

import com.google.common.collect.ImmutableSet;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.api.RandomEventSpawnerHelper;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import com.hexagram2021.real_peaceful_mode.common.mission.PlayerMissions;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public class RPMCommands {
	private static final String MISSION_ID_ARGUMENT = "mission_id";
	private static final String SHOW_DIALOG_ARGUMENT = "show_dialog";
	private static final String NPC_ARGUMENT = "npc";
	private static final String PLAYER_ARGUMENT = "player";

	private static final SuggestionProvider<CommandSourceStack> SUGGEST_MISSION_IDS = (context, builder) ->
			SharedSuggestionProvider.suggestResource(ForgeEventHandler.getMissionManager().getAllMissionIds(), builder);
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_EVENT_MISSION_IDS = (context, builder) ->
			SharedSuggestionProvider.suggestResource(
					ForgeEventHandler.getMissionManager().getAllMissions().stream().filter(MissionManager.Mission::isRandomEvent).map(MissionManager.Mission::id),
					builder
			);
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_FRIENDLY_MONSTER_IDS = (context, builder) ->
			SharedSuggestionProvider.suggestResource(
					ForgeEventHandler.getMissionManager().getAllFriendlyMonsters().map(RegistryHelper::getRegistryName),
					builder
			);

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("rpm").then(
				Commands.literal("mission").then(
						Commands.literal("receive").requires(stack -> stack.hasPermission(2)).then(
								Commands.argument(MISSION_ID_ARGUMENT, ResourceLocationArgument.id()).suggests(SUGGEST_MISSION_IDS)
										.executes(context -> grant(
												context.getSource().getPlayerOrException(),
												SummonBlockEntity.SummonMissionType.RECEIVE,
												ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT),
												false, null
										))
										.then(
												Commands.argument(SHOW_DIALOG_ARGUMENT, BoolArgumentType.bool())
														.executes(context -> grant(
																context.getSource().getPlayerOrException(),
																SummonBlockEntity.SummonMissionType.RECEIVE,
																ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT),
																BoolArgumentType.getBool(context, SHOW_DIALOG_ARGUMENT), null
														))
										)
										.then(
												Commands.argument(NPC_ARGUMENT, EntityArgument.entity())
														.executes(context -> grant(
																context.getSource().getPlayerOrException(),
																SummonBlockEntity.SummonMissionType.RECEIVE,
																ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT),
																true, EntityArgument.getEntity(context, NPC_ARGUMENT)
														))
										)
						)
				).then(
						Commands.literal("finish").requires(stack -> stack.hasPermission(2)).then(
								Commands.argument(MISSION_ID_ARGUMENT, ResourceLocationArgument.id()).suggests(SUGGEST_MISSION_IDS)
										.executes(context -> grant(
												context.getSource().getPlayerOrException(),
												SummonBlockEntity.SummonMissionType.FINISH,
												ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT),
												false, null
										))
										.then(
												Commands.argument(SHOW_DIALOG_ARGUMENT, BoolArgumentType.bool())
														.executes(context -> grant(
																context.getSource().getPlayerOrException(),
																SummonBlockEntity.SummonMissionType.FINISH,
																ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT),
																BoolArgumentType.getBool(context, SHOW_DIALOG_ARGUMENT), null
														))
										)
										.then(
												Commands.argument(NPC_ARGUMENT, EntityArgument.entity())
														.executes(context -> grant(
																context.getSource().getPlayerOrException(),
																SummonBlockEntity.SummonMissionType.FINISH,
																ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT),
																true, EntityArgument.getEntity(context, NPC_ARGUMENT)
														))
										)
						)
				).then(
						Commands.literal("list").requires(stack -> stack.hasPermission(1))
								.executes(context -> showMissionList(context.getSource().getPlayerOrException(), 0))
								.then(Commands.literal("all").executes(context -> showMissionList(context.getSource().getPlayerOrException(), 0)))
								.then(Commands.literal("hideEvents").executes(context -> showMissionList(context.getSource().getPlayerOrException(), 1)))
								.then(Commands.literal("eventsOnly").executes(context -> showMissionList(context.getSource().getPlayerOrException(), 2)))
				).then(
						Commands.literal("disable").requires(stack -> stack.hasPermission(4)).then(
								Commands.argument(MISSION_ID_ARGUMENT, ResourceLocationArgument.id()).suggests(SUGGEST_MISSION_IDS)
										.executes(context -> disableMission(context.getSource().getPlayer(), ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT)))
						)
				).then(
						Commands.literal("enable").requires(stack -> stack.hasPermission(4)).then(
								Commands.argument(MISSION_ID_ARGUMENT, ResourceLocationArgument.id()).suggests(SUGGEST_MISSION_IDS)
										.executes(context -> enableMission(context.getSource().getPlayer(), ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT)))
						)
				)
		).then(
				Commands.literal("event").then(
						Commands.literal("spawn").requires(stack -> stack.hasPermission(2)).then(
								Commands.argument(MISSION_ID_ARGUMENT, ResourceLocationArgument.id()).suggests(SUGGEST_EVENT_MISSION_IDS)
										.executes(context -> spawnEvent(
												context.getSource().getPlayerOrException(),
												context.getSource().getPlayerOrException(),
												ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT)
										))
										.then(
												Commands.argument(PLAYER_ARGUMENT, EntityArgument.player()).executes(context -> spawnEvent(
														context.getSource().getPlayer(),
														EntityArgument.getPlayer(context, PLAYER_ARGUMENT),
														ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT)
												))
										)
						)
				).then(
						Commands.literal("disable").requires(stack -> stack.hasPermission(4)).then(
								Commands.argument(MISSION_ID_ARGUMENT, ResourceLocationArgument.id()).suggests(SUGGEST_FRIENDLY_MONSTER_IDS)
										.executes(context -> disableEvents(
												context.getSource().getPlayer(),
												ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT)
										))
						)
				).then(
						Commands.literal("enable").requires(stack -> stack.hasPermission(4)).then(
								Commands.argument(MISSION_ID_ARGUMENT, ResourceLocationArgument.id()).suggests(SUGGEST_FRIENDLY_MONSTER_IDS)
										.executes(context -> enableEvents(
												context.getSource().getPlayer(),
												ResourceLocationArgument.getId(context, MISSION_ID_ARGUMENT)
										))
						)
				)
		);
	}

	private static final DynamicCommandExceptionType ENTITY_NOT_HERO = new DynamicCommandExceptionType(
			name -> Component.translatable("commands.real_peaceful_mode.mission.failed.not_hero", name)
	);
	private static final DynamicCommandExceptionType MISSION_NOT_EXIST = new DynamicCommandExceptionType(
			name -> Component.translatable("commands.real_peaceful_mode.mission.failed.not_exist", name)
	);
	private static final DynamicCommandExceptionType RANDOM_EVENT_NOT_EXIST = new DynamicCommandExceptionType(
			name -> Component.translatable("commands.real_peaceful_mode.event.failed.not_exist", name)
	);
	private static final Dynamic2CommandExceptionType MISSION_ALREADY_GRANT = new Dynamic2CommandExceptionType(
			(name, missionName) -> Component.translatable("commands.real_peaceful_mode.mission.failed.already_grant", name, missionName)
	);
	private static final DynamicCommandExceptionType MISSION_ALREADY_DISABLED = new DynamicCommandExceptionType(
			missionName -> Component.translatable("commands.real_peaceful_mode.mission.failed.already_disabled", missionName)
	);
	private static final DynamicCommandExceptionType MISSION_ALREADY_ENABLED = new DynamicCommandExceptionType(
			missionName -> Component.translatable("commands.real_peaceful_mode.mission.failed.already_enabled", missionName)
	);
	private static final DynamicCommandExceptionType UNKNOWN_FRIENDLY_MONSTER = new DynamicCommandExceptionType(
			missionName -> Component.translatable("commands.real_peaceful_mode.mission.failed.unknown_monster", missionName)
	);
	private static final Dynamic2CommandExceptionType FAIL_TO_SPAWN_EVENT = new Dynamic2CommandExceptionType(
			(missionName, name) -> Component.translatable("commands.real_peaceful_mode.event.failed.spawn_event", missionName, name)
	);

	private static int grant(ServerPlayer player, SummonBlockEntity.SummonMissionType type, ResourceLocation missionId, boolean dialog, @Nullable Entity npc) throws CommandSyntaxException {
		if(player instanceof IMonsterHero hero) {
			MissionManager.Mission mission = ForgeEventHandler.getMissionManager().getMission(missionId).orElseThrow(() -> MISSION_NOT_EXIST.create(missionId.toString()));
			Component missionName = Component.translatable(PlayerMissions.getMissionDescriptionId(mission))
					.withStyle(ChatFormatting.GREEN)
					.withStyle((style -> style.withHoverEvent(
							new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(missionId.toString()).withStyle(ChatFormatting.GRAY))
					)));
			if(!MissionHelper.checkMission(hero, type, mission)) {
				throw MISSION_ALREADY_GRANT.create(player.getDisplayName(), missionName);
			}
			if(dialog) {
				MissionHelper.triggerMissionForPlayer(
						missionId, type, player,
						npc instanceof LivingEntity livingEntity ? livingEntity : null, player1 -> {}
				);
			} else {
				switch (type) {
					case RECEIVE -> hero.getPlayerMissions().afterReceiveMission(mission, player1 -> {});
					case FINISH -> hero.getPlayerMissions().afterFinishMission(mission, player1 -> {});
				}
			}
			return Command.SINGLE_SUCCESS;
		}
		throw ENTITY_NOT_HERO.create(player.getDisplayName());
	}

	private static int showMissionList(ServerPlayer player, int showType) throws CommandSyntaxException {
		if(player instanceof IMonsterHero hero) {
			PlayerMissions playerMissions = hero.getPlayerMissions();
			ForgeEventHandler.getMissionManager().getAllMissions().stream().sorted(Comparator.comparing(MissionManager.Mission::id)).forEach(mission -> {
				ResourceLocation id = mission.id();
				Component missionName = Component.translatable(PlayerMissions.getMissionDescriptionId(mission))
						.withStyle(ChatFormatting.GREEN)
						.withStyle((style -> style.withHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(id.toString()).withStyle(ChatFormatting.GRAY))
						)));
				Component component;
				if(mission.isRandomEvent()) {
					if((showType & 1) != 0) {
						return;
					}
				} else {
					if((showType & 2) != 0) {
						return;
					}
				}
				if(IMonsterHero.missionDisabled(id)) {
					component = Component.translatable("commands.real_peaceful_mode.mission.list.disabled");
				} else if(IMonsterHero.completeMission(playerMissions, id)) {
					component = Component.translatable("commands.real_peaceful_mode.mission.list.finished");
				} else if(IMonsterHero.underMission(playerMissions, id)) {
					component = Component.translatable("commands.real_peaceful_mode.mission.list.under");
				} else if(!MissionHelper.checkMission(hero, SummonBlockEntity.SummonMissionType.RECEIVE, mission)) {
					component = Component.translatable("commands.real_peaceful_mode.mission.list.locked");
				} else {
					component = Component.translatable("commands.real_peaceful_mode.mission.list.no");
				}
				player.sendSystemMessage(Component.translatable("commands.real_peaceful_mode.mission.list.row", missionName, component));
			});
			return Command.SINGLE_SUCCESS;
		}
		throw ENTITY_NOT_HERO.create(player.getDisplayName());
	}

	private static int disableMission(@Nullable ServerPlayer player, ResourceLocation missionId) throws CommandSyntaxException {
		MissionManager.Mission mission = ForgeEventHandler.getMissionManager().getMission(missionId).orElseThrow(() -> MISSION_NOT_EXIST.create(missionId.toString()));
		Component missionName = Component.translatable(PlayerMissions.getMissionDescriptionId(mission))
				.withStyle(ChatFormatting.GREEN)
				.withStyle((style -> style.withHoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(missionId.toString()).withStyle(ChatFormatting.GRAY))
				)));
		if(IMonsterHero.missionDisabled(missionId)) {
			throw MISSION_ALREADY_DISABLED.create(missionName);
		}
		RPMCommonConfig.DISABLE_MISSIONS.set(
				ImmutableSet.<String>builder()
						.addAll(RPMCommonConfig.DISABLE_MISSIONS.get())
						.add(missionId.toString())
						.build().asList()
		);

		RPMLogger.info("Successfully disable mission %s.".formatted(missionId));
		if(player != null) {
			player.sendSystemMessage(Component.translatable("commands.real_peaceful_mode.mission.disabled", missionName));
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int enableMission(@Nullable ServerPlayer player, ResourceLocation missionId) throws CommandSyntaxException {
		MissionManager.Mission mission = ForgeEventHandler.getMissionManager().getMission(missionId).orElseThrow(() -> MISSION_NOT_EXIST.create(missionId.toString()));
		Component missionName = Component.translatable(PlayerMissions.getMissionDescriptionId(mission))
				.withStyle(ChatFormatting.GREEN)
				.withStyle((style -> style.withHoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(missionId.toString()).withStyle(ChatFormatting.GRAY))
				)));
		if(!IMonsterHero.missionDisabled(missionId)) {
			throw MISSION_ALREADY_ENABLED.create(missionName);
		}
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		RPMCommonConfig.DISABLE_MISSIONS.get().forEach(id -> {
			if(!id.equals(missionId.toString())) {
				builder.add(id);
			}
		});
		RPMCommonConfig.DISABLE_MISSIONS.set(builder.build().asList());

		RPMLogger.info("Successfully enable mission %s.".formatted(missionId));
		if(player != null) {
			player.sendSystemMessage(Component.translatable("commands.real_peaceful_mode.mission.enabled", missionName));
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int disableEvents(@Nullable ServerPlayer player, ResourceLocation entityType) throws CommandSyntaxException {
		if(ForgeEventHandler.getMissionManager().getAllFriendlyMonsters().noneMatch(e -> entityType.equals(getRegistryName(e)))) {
			throw UNKNOWN_FRIENDLY_MONSTER.create(entityType);
		}
		if(IMonsterHero.eventDisabledFor(entityType)) {
			throw MISSION_ALREADY_DISABLED.create(entityType);
		}
		RPMCommonConfig.DISABLE_EVENTS.set(
				ImmutableSet.<String>builder()
						.addAll(RPMCommonConfig.DISABLE_EVENTS.get())
						.add(entityType.toString())
						.build().asList()
		);

		Component entityName = Component.translatable(Util.makeDescriptionId("entity", entityType));

		RPMLogger.info("Successfully disable entity events for %s.".formatted(entityType));
		if(player != null) {
			player.sendSystemMessage(Component.translatable("commands.real_peaceful_mode.mission.disabled", entityName));
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int enableEvents(@Nullable ServerPlayer player, ResourceLocation entityType) throws CommandSyntaxException {
		if(ForgeEventHandler.getMissionManager().getAllFriendlyMonsters().noneMatch(e -> entityType.equals(getRegistryName(e)))) {
			throw UNKNOWN_FRIENDLY_MONSTER.create(entityType);
		}
		if(!IMonsterHero.eventDisabledFor(entityType)) {
			throw MISSION_ALREADY_ENABLED.create(entityType);
		}

		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		RPMCommonConfig.DISABLE_EVENTS.get().forEach(id -> {
			if(!id.equals(entityType.toString())) {
				builder.add(id);
			}
		});
		RPMCommonConfig.DISABLE_EVENTS.set(builder.build().asList());

		Component entityName = Component.translatable(Util.makeDescriptionId("entity", entityType));

		RPMLogger.info("Successfully enable entity events for %s.".formatted(entityType));
		if(player != null) {
			player.sendSystemMessage(Component.translatable("commands.real_peaceful_mode.mission.enabled", entityName));
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int spawnEvent(@Nullable ServerPlayer player, ServerPlayer luckyDog, ResourceLocation missionId) throws CommandSyntaxException {
		MissionManager.Mission mission = ForgeEventHandler.getMissionManager().getMission(missionId).orElseThrow(() -> MISSION_NOT_EXIST.create(missionId.toString()));
		if(!mission.isRandomEvent()) {
			throw RANDOM_EVENT_NOT_EXIST.create(missionId.toString());
		}
		Component missionName = Component.translatable(PlayerMissions.getMissionDescriptionId(mission))
				.withStyle(ChatFormatting.GREEN)
				.withStyle((style -> style.withHoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(missionId.toString()).withStyle(ChatFormatting.GRAY))
				)));
		if(IMonsterHero.missionDisabled(missionId)) {
			throw MISSION_ALREADY_DISABLED.create(missionName);
		}

		AtomicBoolean success = new AtomicBoolean(false);
		RandomEventSpawnerHelper.getAllRandomEventSpawners().forEach(spawner -> {
			if(!success.get()) {
				success.set(spawner.spawnEventFor(luckyDog.serverLevel(), luckyDog, missionId));
			}
		});
		if(!success.get()) {
			throw FAIL_TO_SPAWN_EVENT.create(missionName, luckyDog.getDisplayName());
		}

		RPMLogger.info("Successfully spawn random event for %s.".formatted(luckyDog.getDisplayName()));
		if(player != null) {
			player.sendSystemMessage(Component.translatable("commands.real_peaceful_mode.event.spawned", missionName));
		}

		return Command.SINGLE_SUCCESS;
	}
}
