package com.hexagram2021.real_peaceful_mode.common.block.entity;

import com.google.common.collect.Maps;
import com.hexagram2021.real_peaceful_mode.api.IMissionProvider;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.api.MissionType;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.IMissionStack;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.IPlayerListWithMissions;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.Mission;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;

public class SummonBlockEntity extends BlockEntity implements IMissionProvider {
	public static final String TAG_SUMMON_ENTITY = "summon";
	public static final String TAG_MISSION = "mission";
	public static final String TAG_EXTRA_CONDITION = "extra_condition";
	public static final String TAG_EXTRA_WORK = "extra_work";
	public static final String TAG_MISSION_TYPE = "mission_type";
	public static final String TAG_DISTANCE = "distance";

	private static final int CHECK_TICK = 40;

	@Nullable
	private CompoundTag summonTag;

	@Nullable
	private String extraCondition;
	@Nullable
	private String extraWork;

	@Nullable
	private SummonBlockMission triggerableMission;

	private int distance = 16;

	private int lastCheckTick = CHECK_TICK;

	public SummonBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(RPMBlockEntities.SUMMON_BLOCK.get(), blockPos, blockState);
	}

	public SummonBlockEntity(BlockPos blockPos, BlockState blockState,
							 @Nullable CompoundTag summonTag, @Nullable Mission mission, MissionType type, int distance) {
		this(blockPos, blockState);
		this.summonTag = summonTag;
		if(mission != null) {
			this.triggerableMission = new SummonBlockMission(mission, type);
		}
		this.distance = distance;
	}

	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, SummonBlockEntity blockEntity) {
		if(blockEntity.lastCheckTick > 0) {
			blockEntity.lastCheckTick -= 1;
			return;
		}
		blockEntity.lastCheckTick = CHECK_TICK;
		if(level instanceof ServerLevel serverLevel && blockEntity.checkExtraCondition(serverLevel)) {
			SummonBlockMission mission = blockEntity.getTriggerableMission();
			List<ServerPlayer> nearbyPlayers;
			if(mission != null) {
				nearbyPlayers = serverLevel.players().stream()
						.filter(
								player -> player.position().closerThan(blockPos.getCenter(), blockEntity.distance) &&
										!player.getAbilities().instabuild &&
										MissionHelper.checkMission((IMonsterHero) player, mission.type, mission.mission)
						).toList();
			} else if(blockEntity.summonTag != null) {
				nearbyPlayers = serverLevel.players().stream()
						.filter(
								player -> player.position().closerThan(blockPos.getCenter(), blockEntity.distance) &&
										!player.getAbilities().instabuild
						).toList();
			} else {
				return;
			}
			if (!nearbyPlayers.isEmpty()) {
				LivingEntity npc = blockEntity.summon(serverLevel);
				serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), UPDATE_ALL);
				if(mission != null) {
					MissionHelper.triggerMissionForPlayers(
							mission.mission, mission.type, nearbyPlayers,
							(IPlayerListWithMissions) serverLevel.getServer().getPlayerList(), npc, p -> blockEntity.applyExtraWork(serverLevel)
					);
				}
			}
		}
	}

	@Nullable
	private LivingEntity summon(ServerLevel level) {
		if(this.summonTag == null) {
			return null;
		}
		CompoundTag compoundtag = this.summonTag.copy();
		Entity ret = EntityType.loadEntityRecursive(compoundtag, level, entity -> {
			entity.moveTo(this.getBlockPos().getCenter().subtract(0.0D, 0.375D, 0.0D));
			return entity;
		});
		if(ret instanceof LivingEntity livingEntity) {
			if(livingEntity instanceof Mob mob && compoundtag.size() <= 1) {
				EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(this.getBlockPos()), MobSpawnType.MOB_SUMMONED, null);
			}
			if(level.tryAddFreshEntityWithPassengers(ret)) {
				return livingEntity;
			}
		}
		if(ret != null) {
			ret.discard();
		}
		return null;
	}

	private static final Map<String, BiFunction<ServerLevel, BlockPos, Boolean>> EXTRA_CONDITIONS = Maps.newHashMap();
	public static void registerExtraCondition(String conditionName, BiFunction<ServerLevel, BlockPos, Boolean> condition) {
		EXTRA_CONDITIONS.put(conditionName, condition);
	}
	private boolean checkExtraCondition(ServerLevel level) {
		if(this.extraCondition == null) {
			return true;
		}
		return EXTRA_CONDITIONS.getOrDefault(this.extraCondition, (l, p) -> false).apply(level, this.worldPosition);
	}

	private static final Map<String, BiConsumer<ServerLevel, BlockPos>> EXTRA_WORKS = Maps.newHashMap();
	public static void registerExtraWork(String conditionName, BiConsumer<ServerLevel, BlockPos> work) {
		EXTRA_WORKS.put(conditionName, work);
	}
	private void applyExtraWork(ServerLevel level) {
		if(this.extraWork != null) {
			EXTRA_WORKS.getOrDefault(this.extraWork, (l, p) -> {}).accept(level, this.worldPosition);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		if(this.summonTag != null) {
			nbt.put(TAG_SUMMON_ENTITY, this.summonTag.copy());
		}
		if(this.triggerableMission != null) {
			nbt.putString(TAG_MISSION, this.triggerableMission.mission.id().toString());
			nbt.putString(TAG_MISSION_TYPE, this.triggerableMission.type.getSerializedName());
		}
		if(this.extraCondition != null) {
			nbt.putString(TAG_EXTRA_CONDITION, this.extraCondition);
		}
		if(this.extraWork != null) {
			nbt.putString(TAG_EXTRA_WORK, this.extraWork);
		}
		nbt.putInt(TAG_DISTANCE, this.distance);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		if(nbt.contains(TAG_SUMMON_ENTITY, Tag.TAG_COMPOUND)) {
			this.summonTag = nbt.getCompound(TAG_SUMMON_ENTITY).copy();
		}

		Mission mission = null;
		if(nbt.contains(TAG_MISSION, Tag.TAG_STRING)) {
			mission = ForgeEventHandler.getMissionManager().getMission(ResourceLocation.parse(nbt.getString(TAG_MISSION))).orElse(null);
		}
		if(mission != null) {
			this.triggerableMission = new SummonBlockMission(
					mission,
					MissionType.BY_NAME.getOrDefault(nbt.getString(TAG_MISSION_TYPE), MissionType.RECEIVE)
			);
		}

		if(nbt.contains(TAG_EXTRA_CONDITION, Tag.TAG_STRING)) {
			this.extraCondition = nbt.getString(TAG_EXTRA_CONDITION);
		}
		if(nbt.contains(TAG_EXTRA_WORK, Tag.TAG_STRING)) {
			this.extraWork = nbt.getString(TAG_EXTRA_WORK);
		}
		if(nbt.contains(TAG_DISTANCE, Tag.TAG_INT)) {
			this.distance = nbt.getInt(TAG_DISTANCE);
		}
	}

	@Override @Nullable
	public SummonBlockMission getTriggerableMission() {
		return this.triggerableMission;
	}

	public void setTriggerableMission(@Nullable Mission mission, MissionType missionType) {
		if(mission == null) {
			this.triggerableMission = null;
		} else {
			this.triggerableMission = new SummonBlockMission(mission, missionType);
		}
	}

	public int getDistance() {
		return this.distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Nullable
	public CompoundTag getSummonTag() {
		return this.summonTag;
	}
	public void setSummonTag(@Nullable CompoundTag tag) {
		this.summonTag = tag;
	}

	@Nullable
	public String getExtraCondition() {
		return this.extraCondition;
	}
	public void setExtraCondition(@Nullable String extraCondition) {
		this.extraCondition = extraCondition;
	}

	@Nullable
	public String getExtraWork() {
		return this.extraWork;
	}
	public void setExtraWork(@Nullable String extraWork) {
		this.extraWork = extraWork;
	}

	public static class SummonBlockMission implements IMissionProvider.TriggerableMission<SummonBlockEntity>, IMissionStack {
		final Mission mission;
		final MissionType type;

		SummonBlockMission(Mission mission, MissionType type) {
			this.mission = mission;
			this.type = type;
		}

		@Override
		public boolean tryTrigger(ServerLevel serverLevel, SummonBlockEntity outer) {
			return false;
		}

		@Override
		public String getSerializedName() {
			return this.mission.id() + "/" + this.type.getSerializedName();
		}

		@Override
		public ResourceLocation missionId() {
			return this.mission.id();
		}

		@Override
		public MissionType type() {
			return this.type;
		}
	}
}
//{summon: {id: "zombie"}, id: "real_peaceful_mode:summon_block", mission_type: "receive", mission: "real_peaceful_mode:zombie1"}
//{id: "real_peaceful_mode:summon_block", mission_type: "finish", mission: "real_peaceful_mode:zombie1"}
