package com.hexagram2021.real_peaceful_mode.server.commands;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import com.hexagram2021.real_peaceful_mode.common.mission.PlayerMissions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class RPMCommands {
	private static final String MISSION_ID_ARGUMENT = "mission_id";
	private static final String SHOW_DIALOG_ARGUMENT = "show_dialog";
	private static final String NPC_ARGUMENT = "npc";

	private static final SuggestionProvider<CommandSourceStack> SUGGEST_MISSION_IDS = (context, builder) ->
			SharedSuggestionProvider.suggestResource(ForgeEventHandler.getMissionManager().getAllMissionIds(), builder);

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
						Commands.literal("list").requires(stack -> stack.hasPermission(0))
								.executes(context -> showMissionList(context.getSource().getPlayerOrException()))
				)
		);
	}

	private static final DynamicCommandExceptionType ENTITY_NOT_HERO = new DynamicCommandExceptionType(
			name -> Component.translatable("commands.real_peaceful_mode.mission.failed.not_hero", name)
	);
	private static final DynamicCommandExceptionType MISSION_NOT_EXIST = new DynamicCommandExceptionType(
			name -> Component.translatable("commands.real_peaceful_mode.mission.failed.not_exist", name)
	);
	private static final Dynamic2CommandExceptionType MISSION_ALREADY_GRANT = new Dynamic2CommandExceptionType(
			(name, missionName) -> Component.translatable("commands.real_peaceful_mode.mission.failed.already_grant", name, missionName)
	);

	private static int grant(ServerPlayer player, SummonBlockEntity.SummonMissionType type, ResourceLocation missionId, boolean dialog, @Nullable Entity npc) throws CommandSyntaxException {
		if(player instanceof IMonsterHero hero) {
			MissionManager.Mission mission = ForgeEventHandler.getMissionManager().getMission(missionId).orElseThrow(() -> MISSION_NOT_EXIST.create(missionId.toString()));
			Component missionName = Component.translatable(PlayerMissions.getMissionDescriptionId(mission));
			if(!MissionHelper.checkMission(hero, type, mission)) {
				throw MISSION_ALREADY_GRANT.create(player.getCustomName(), missionName);
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
		throw ENTITY_NOT_HERO.create(player.getCustomName());
	}

	private static int showMissionList(ServerPlayer player) throws CommandSyntaxException {
		if(player instanceof IMonsterHero hero) {
			PlayerMissions playerMissions = hero.getPlayerMissions();
			ForgeEventHandler.getMissionManager().getAllMissions().forEach(mission -> {
				Component missionName = Component.translatable(PlayerMissions.getMissionDescriptionId(mission));
				ResourceLocation id = mission.id();
				Component component;
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
				player.sendSystemMessage(Component.translatable("commands.real_peaceful_mode.mission.list.row", id.toString(), missionName, component));
			});
			return Command.SINGLE_SUCCESS;
		}
		throw ENTITY_NOT_HERO.create(player.getCustomName());
	}
}
