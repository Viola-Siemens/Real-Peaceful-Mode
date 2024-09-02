package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.api.IMissionProvider;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.api.MissionType;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.IMissionStack;
import com.hexagram2021.real_peaceful_mode.common.register.RPMStructureTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.Objects;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class GuardSlimeEntity extends Slime implements IMissionProvider {
	private static final EntityDataAccessor<Boolean> HAS_ARMOR = SynchedEntityData.defineId(GuardSlimeEntity.class, EntityDataSerializers.BOOLEAN);

	public GuardSlimeEntity(EntityType<? extends GuardSlimeEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HAS_ARMOR, true);
	}

	private static final String TAG_HAS_ARMOR = "HasArmor";
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putBoolean(TAG_HAS_ARMOR, this.hasArmor());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if(nbt.contains(TAG_HAS_ARMOR, Tag.TAG_BYTE)) {
			this.setHasArmor(nbt.getBoolean(TAG_HAS_ARMOR));
		} else {
			this.setHasArmor(true);
		}
	}

	private int checkNearbyPlayers = 100;

	@Override
	public void tick() {
		if(--this.checkNearbyPlayers <= 0) {
			this.checkNearbyPlayers = 100;
			if (this.level() instanceof ServerLevel serverLevel) {
				GuardSlimeMissions mission = this.getTriggerableMission();
				if(mission != null) {
					mission.tryTrigger(serverLevel, this);
				}
			}
		}

		super.tick();
	}

	@Override
	public void setSize(int size, boolean newSpawn) {
		super.setSize(size, newSpawn);
		int i = Mth.clamp(size, 1, 127);
		Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(i * i * 2);
		Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.25D + 0.1D * i);
	}

	@Override
	protected float getAttackDamage() {
		return super.getAttackDamage() + 1.0F;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	@Override
	public boolean canAttack(LivingEntity livingEntity) {
		return this.hasArmor() && !(livingEntity instanceof IMonsterHero hero && hero.rpm$isHero(EntityType.SLIME)) && super.canAttack(livingEntity);
	}

	public boolean hasArmor() {
		return this.getEntityData().get(HAS_ARMOR);
	}
	public void setHasArmor(boolean hasArmor) {
		this.getEntityData().set(HAS_ARMOR, hasArmor);
		Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(hasArmor ? 4.0D : 0.0D);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.ARMOR, 4.0D);
	}

	public static boolean checkSpawnRules(EntityType<? extends GuardSlimeEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType,
										  BlockPos blockPos, RandomSource random) {
		return level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, blockPos, random) && checkMobSpawnRules(entityType, level, spawnType, blockPos, random);
	}

	@Override @Nullable
	public GuardSlimeMissions getTriggerableMission() {
		if(this.isNoAi()) {
			if(this.hasPose(Pose.SLEEPING)) {
				return GuardSlimeMissions.SAVE_ME;
			}
			return GuardSlimeMissions.SEEK_HELP;
		}
		return null;
	}

	public enum GuardSlimeMissions implements IMissionProvider.TriggerableMission<GuardSlimeEntity>, IMissionStack {
		SEEK_HELP("slime1", MissionType.RECEIVE) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, GuardSlimeEntity outer) {
				return serverLevel.players().stream()
						.filter(
								player -> player.closerThan(outer, 6.0D) &&
										player instanceof IMonsterHero hero &&
										!IMonsterHero.completeMission(hero.rpm$getPlayerMissions(), this.missionId)
						).findAny().map(player -> {
							outer.setNoAi(false);
							MissionHelper.triggerMissionForPlayer(
									this.missionId, this.type,
									player, outer, player1 -> {
										BlockPos blockPos = serverLevel.findNearestMapStructure(RPMStructureTags.ON_SLIME_EXPLORER_MAPS, outer.blockPosition(), 100, true);
										ItemStack mapItem;
										if(blockPos == null) {
											mapItem = new ItemStack(Items.MAP);
										} else {
											mapItem = MapItem.create(outer.level(), blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
										}
										mapItem.setHoverName(Component.translatable("filled_map.real_peaceful_mode.slime_maze"));
										outer.spawnAtLocation(mapItem);
										outer.discard();
									}
							);
							return true;
						}).orElse(false);
			}
		},
		SAVE_ME("slime2", MissionType.FINISH) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, GuardSlimeEntity outer) {
				return serverLevel.players().stream()
						.filter(
								player -> player.closerThan(outer, 6.0D) &&
										player instanceof IMonsterHero hero &&
										IMonsterHero.underMission(hero.rpm$getPlayerMissions(), this.missionId)
						).findAny().map(player -> {
							Slime slime = EntityType.SLIME.create(outer.level());
							if(slime != null) {
								slime.targetSelector.removeAllGoals(goal -> goal instanceof NearestAttackableTargetGoal);
								slime.setSize(1, true);
								slime.moveTo(outer.position());
								outer.discard();
								outer.level().addFreshEntity(slime);
								MissionHelper.triggerMissionForPlayer(
										this.missionId, this.type,
										player, outer, player1 -> {
										}
								);
								return true;
							}
							return false;
						}).orElse(false);
			}
		};

		final ResourceLocation missionId;
		final MissionType type;

		GuardSlimeMissions(String mission, MissionType type) {
			this.missionId = new ResourceLocation(MODID, mission);
			this.type = type;
		}

		@Override
		public String getSerializedName() {
			return this.missionId + "/" + this.type.getSerializedName();
		}

		@Override
		public abstract boolean tryTrigger(ServerLevel serverLevel, GuardSlimeEntity outer);

		@Override
		public ResourceLocation missionId() {
			return missionId;
		}

		@Override
		public MissionType type() {
			return type;
		}
	}
}
