package com.hexagram2021.real_peaceful_mode.common.block.entity;

import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import com.hexagram2021.real_peaceful_mode.common.mission.PlayerMissions;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class ContinuousSummonBlockEntity extends BlockEntity {
	public static final String TAG_SUMMON_ENTITY = "summon";
	public static final String TAG_ACTIVE_MISSION = "mission";
	public static final String TAG_DISTANCE = "distance";
	public static final String TAG_INTERVAL = "interval";

	@Nullable
	private CompoundTag summonTag;

	@Nullable
	private MissionManager.Mission mission;

	private int distance = 16;

	private int lastCheckTick = 100;

	private int interval = 200;

	public ContinuousSummonBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(RPMBlockEntities.CONTINUOUS_SUMMON_BLOCK.get(), blockPos, blockState);
	}

	public ContinuousSummonBlockEntity(BlockPos blockPos, BlockState blockState,
									   @Nullable CompoundTag summonTag, @Nullable MissionManager.Mission mission, int interval, int distance) {
		this(blockPos, blockState);
		this.summonTag = summonTag;
		this.mission = mission;
		this.distance = distance;
		this.interval = interval;
	}

	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, ContinuousSummonBlockEntity blockEntity) {
		if(blockEntity.lastCheckTick > 0) {
			blockEntity.lastCheckTick -= 1;
			return;
		}
		blockEntity.lastCheckTick = blockEntity.interval;
		if(level instanceof ServerLevel serverLevel && (blockEntity.mission != null || blockEntity.summonTag != null)) {
			List<? extends ServerPlayer> nearbyPlayers = serverLevel.players().stream()
					.filter(player -> player.position().closerThan(blockPos.getCenter(), blockEntity.distance) && !player.getAbilities().instabuild && checkMission((IMonsterHero)player, blockEntity.mission))
					.toList();
			if (!nearbyPlayers.isEmpty()) {
				blockEntity.summon(serverLevel);
				nearbyPlayers.stream().filter(player -> checkBlockReplace((IMonsterHero)player, blockEntity.mission)).findAny()
						.ifPresent(player -> serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL));
			}
		}
	}

	private void summon(ServerLevel level) {
		if(this.summonTag == null) {
			return;
		}
		CompoundTag compoundtag = this.summonTag.copy();
		if(!this.summonTag.contains("id", Tag.TAG_STRING)) {
			return;
		}
		EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(this.summonTag.getString("id")));
		if(entityType == null || level.getEntities(entityType, entity -> entity.position().closerThan(this.getBlockPos().getCenter(), 16.0D)).size() > 16) {
			return;
		}
		Entity ret = EntityType.loadEntityRecursive(compoundtag, level, entity -> {
			entity.moveTo(this.getBlockPos().getCenter());
			return entity;
		});
		if(ret instanceof LivingEntity livingEntity) {
			if(livingEntity instanceof Mob mob) {
				ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(this.getBlockPos()), MobSpawnType.MOB_SUMMONED, null, null);
			}
			if(level.tryAddFreshEntityWithPassengers(ret)) {
				return;
			}
		}
		if(ret != null) {
			ret.discard();
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if(this.summonTag != null) {
			nbt.put(TAG_SUMMON_ENTITY, this.summonTag.copy());
		}
		if(this.mission != null) {
			nbt.putString(TAG_ACTIVE_MISSION, this.mission.id().toString());
		}
		nbt.putInt(TAG_DISTANCE, this.distance);
		nbt.putInt(TAG_INTERVAL, this.interval);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if(nbt.contains(TAG_SUMMON_ENTITY, Tag.TAG_COMPOUND)) {
			this.summonTag = nbt.getCompound(TAG_SUMMON_ENTITY).copy();
		}
		if(nbt.contains(TAG_ACTIVE_MISSION, Tag.TAG_STRING)) {
			this.mission = ForgeEventHandler.getMissionManager().getMission(new ResourceLocation(nbt.getString(TAG_ACTIVE_MISSION))).orElse(null);
		}
		if(nbt.contains(TAG_DISTANCE, Tag.TAG_INT)) {
			this.distance = nbt.getInt(TAG_DISTANCE);
		}
		this.interval = nbt.getInt(TAG_INTERVAL);
	}

	private static boolean checkMission(IMonsterHero hero, @Nullable MissionManager.Mission mission) {
		PlayerMissions playerMissions = hero.getPlayerMissions();
		if(mission == null) {
			return false;
		}
		ResourceLocation missionId = mission.id();
		return IMonsterHero.underMission(playerMissions, missionId);
	}

	private static boolean checkBlockReplace(IMonsterHero hero, @Nullable MissionManager.Mission mission) {
		PlayerMissions playerMissions = hero.getPlayerMissions();
		if(mission == null) {
			return false;
		}
		ResourceLocation missionId = mission.id();
		return IMonsterHero.completeMission(playerMissions, missionId);
	}
}

//{summon: {id: "zombie"}, id: "real_peaceful_mode:summon_block", mission_type: "receive", mission: "real_peaceful_mode:zombie1"}
//{id: "real_peaceful_mode:summon_block", mission_type: "finish", mission: "real_peaceful_mode:zombie1"}
