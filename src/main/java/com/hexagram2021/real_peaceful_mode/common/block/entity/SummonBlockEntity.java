package com.hexagram2021.real_peaceful_mode.common.block.entity;

import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class SummonBlockEntity extends BlockEntity {
	public static final String TAG_SUMMON_ENTITY = "summon";

	@Nullable
	private CompoundTag summonTag;

	@Nullable
	private MissionManager.Mission mission;

	private SummonMissionType type;

	private enum SummonMissionType {
		RECEIVE,
		FINISH;

		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	public SummonBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(RPMBlockEntities.SUMMON_BLOCK.get(), blockPos, blockState);
	}

	public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, SummonBlockEntity blockEntity) {
		if(level instanceof ServerLevel serverLevel && blockEntity.mission != null) {
			List<? extends ServerPlayer> nearbyPlayers = serverLevel.players().stream()
					.filter(player -> player.position().closerThan(blockPos.getCenter(), 16.0D))
					.toList();
			if (!nearbyPlayers.isEmpty()) {
				LivingEntity npc = blockEntity.summon(serverLevel);
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
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if(nbt.contains(TAG_SUMMON_ENTITY, Tag.TAG_COMPOUND)) {
			this.summonTag = nbt.getCompound(TAG_SUMMON_ENTITY).copy();
		}
	}
}
