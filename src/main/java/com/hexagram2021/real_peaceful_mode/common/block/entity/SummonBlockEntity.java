package com.hexagram2021.real_peaceful_mode.common.block.entity;

import com.google.common.collect.ImmutableMap;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.mission.IPlayerListWithMissions;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;

public class SummonBlockEntity extends BlockEntity {
	public static final String TAG_SUMMON_ENTITY = "summon";
	public static final String TAG_MISSION = "mission";
	public static final String TAG_MISSION_TYPE = "mission_type";

	private static final int CHECK_TICK = 100;

	@Nullable
	private CompoundTag summonTag;

	@Nullable
	private MissionManager.Mission mission;

	private SummonMissionType type = SummonMissionType.RECEIVE;

	private int lastCheckTick = CHECK_TICK;

	private enum SummonMissionType {
		RECEIVE,
		FINISH;

		public static final Map<String, SummonMissionType> TYPE_BY_NAME;

		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		static {
			ImmutableMap.Builder<String, SummonMissionType> builder = ImmutableMap.builder();
			Arrays.stream(values()).forEach(type -> builder.put(type.getSerializedName(), type));
			TYPE_BY_NAME = builder.build();
		}
	}

	public SummonBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(RPMBlockEntities.SUMMON_BLOCK.get(), blockPos, blockState);
	}

	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, SummonBlockEntity blockEntity) {
		if(blockEntity.lastCheckTick > 0) {
			blockEntity.lastCheckTick -= 1;
			return;
		}
		blockEntity.lastCheckTick = CHECK_TICK;
		if(level instanceof ServerLevel serverLevel && blockEntity.mission != null) {
			List<? extends ServerPlayer> nearbyPlayers = serverLevel.players().stream()
					.filter(player -> player.position().closerThan(blockPos.getCenter(), 16.0D) && checkMission((IMonsterHero)player, blockEntity.mission))
					.toList();
			if (!nearbyPlayers.isEmpty()) {
				LivingEntity npc = blockEntity.summon(serverLevel);
				serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), UPDATE_ALL);
				nearbyPlayers.forEach(player -> {
					PlayerMissions playerMissions = ((IPlayerListWithMissions) serverLevel.getServer().getPlayerList()).getPlayerMissions(player);
					switch (blockEntity.type) {
						case RECEIVE -> playerMissions.receiveNewMission(blockEntity.mission, npc);
						case FINISH -> playerMissions.finishMission(blockEntity.mission, npc);
					}
				});
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
			entity.moveTo(this.getBlockPos().getCenter());
			return entity;
		});
		if(ret instanceof LivingEntity livingEntity) {
			if(livingEntity instanceof Mob mob) {
				ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(this.getBlockPos()), MobSpawnType.MOB_SUMMONED, null, null);
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

	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if(this.summonTag != null) {
			nbt.put(TAG_SUMMON_ENTITY, this.summonTag.copy());
		}
		if(this.mission != null) {
			nbt.putString(TAG_MISSION, this.mission.id().toString());
		}
		nbt.putString(TAG_MISSION_TYPE, this.type.getSerializedName());
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if(nbt.contains(TAG_SUMMON_ENTITY, Tag.TAG_COMPOUND)) {
			this.summonTag = nbt.getCompound(TAG_SUMMON_ENTITY).copy();
		}
		if(nbt.contains(TAG_MISSION, Tag.TAG_STRING)) {
			this.mission = ForgeEventHandler.getMissionManager().getMission(new ResourceLocation(nbt.getString(TAG_MISSION))).orElse(null);
		}
		this.type = SummonMissionType.TYPE_BY_NAME.getOrDefault(nbt.getString(TAG_MISSION_TYPE), SummonMissionType.RECEIVE);
	}

	private static boolean checkMission(IMonsterHero hero, MissionManager.Mission mission) {
		PlayerMissions playerMissions = hero.gerPlayerMissions();
		ResourceLocation missionId = mission.id();
		if(playerMissions.finishedMissions().contains(missionId) || playerMissions.activeMissions().contains(missionId)) {
			return false;
		}
		for(ResourceLocation former: mission.formers()) {
			if(!playerMissions.finishedMissions().contains(former)) {
				return false;
			}
		}
		return true;
	}
}
//{summon: {id: "zombie"}, mission: "real_peaceful_mode:zombie1", id: "real_peaceful_mode:summon_block", mission_type: "receive"}
