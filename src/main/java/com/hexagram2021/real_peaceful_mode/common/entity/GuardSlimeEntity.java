package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.api.IMissionProvider;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.api.MissionType;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.IMissionStack;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMapDecorationTypes;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;
import java.util.Objects;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class GuardSlimeEntity extends Slime implements IMissionProvider {
	private static final EntityDataAccessor<Boolean> HAS_ARMOR = SynchedEntityData.defineId(GuardSlimeEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_SICK = SynchedEntityData.defineId(GuardSlimeEntity.class, EntityDataSerializers.BOOLEAN);

	public GuardSlimeEntity(EntityType<? extends GuardSlimeEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(HAS_ARMOR, true);
		this.entityData.define(IS_SICK, false);
	}

	private static final String TAG_HAS_ARMOR = "HasArmor";
	private static final String TAG_IS_SICK = "IsSick";
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putBoolean(TAG_HAS_ARMOR, this.hasArmor());
		nbt.putBoolean(TAG_IS_SICK, this.isSick());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if(nbt.contains(TAG_HAS_ARMOR, Tag.TAG_BYTE)) {
			this.setHasArmor(nbt.getBoolean(TAG_HAS_ARMOR));
		} else {
			this.setHasArmor(true);
		}
		if(nbt.contains(TAG_IS_SICK, Tag.TAG_BYTE)) {
			this.setSick(nbt.getBoolean(TAG_IS_SICK));
		} else {
			this.setSick(false);
		}
	}

	private int checkNearbyPlayers = 100;
	private static final ItemStack SLIME2_TRIGGER_ITEM = new ItemStack(RPMItems.Materials.SLIME_COLLOID);

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
	public boolean canAttack(LivingEntity target) {
		return this.hasArmor() && !(target instanceof IMonsterHero hero && hero.rpm$isHero(EntityType.SLIME)) && super.canAttack(target);
	}

	@Override
	protected void dealDamage(LivingEntity target) {
		if(target instanceof IMonsterHero hero && hero.rpm$isHero(EntityType.SLIME)) {
			return;
		}
		super.dealDamage(target);
	}
	@Override
	protected boolean isDealsDamage() {
		return this.hasArmor() && super.isDealsDamage();
	}

	public boolean hasArmor() {
		return this.getEntityData().get(HAS_ARMOR);
	}
	public void setHasArmor(boolean hasArmor) {
		this.getEntityData().set(HAS_ARMOR, hasArmor);
		Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(hasArmor ? 4.0D : 0.0D);
	}

	public boolean isSick() {
		return this.getEntityData().get(IS_SICK);
	}
	public void setSick(boolean isSick) {
		this.getEntityData().set(IS_SICK, isSick);
		if(isSick) {
			this.goalSelector.removeAllGoals(goal -> true);
			this.targetSelector.removeAllGoals(goal -> true);
			this.setPose(Pose.SLEEPING);
		} else {
			this.setPose(Pose.STANDING);
		}
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.ARMOR, 4.0D);
	}

	public static boolean checkSpawnRules(EntityType<? extends GuardSlimeEntity> entityType, ServerLevelAccessor level, MobSpawnType spawnType,
										  BlockPos blockPos, RandomSource random) {
		return Monster.isDarkEnoughToSpawn(level, blockPos, random) && checkMobSpawnRules(entityType, level, spawnType, blockPos, random);
	}

	@Override @Nullable
	public GuardSlimeMissions getTriggerableMission() {
		if(!this.hasArmor()) {
			if (this.hasPose(Pose.SLEEPING)) {
				return GuardSlimeMissions.SAVE_ME;
			}
			if(this.isNoAi()) {
				return GuardSlimeMissions.SEEK_HELP;
			}
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
											MapItem.renderBiomePreviewMap(serverLevel, mapItem);
											MapItemSavedData.addTargetDecoration(mapItem, blockPos.offset(10, 0, 14), "+", RPMMapDecorationTypes.SLIME_MAZE);
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
										IMonsterHero.underMission(hero.rpm$getPlayerMissions(), this.missionId) &&
										player.getInventory().contains(SLIME2_TRIGGER_ITEM)
						).findAny().map(player -> {
							Slime slime = EntityType.SLIME.create(outer.level());
							if(slime != null) {
								slime.goalSelector.removeAllGoals(goal -> true);
								slime.targetSelector.removeAllGoals(goal -> true);
								slime.goalSelector.addGoal(2, new SlimeMissionTriggerGoal(slime));
								slime.setSize(2, true);
								slime.moveTo(outer.position());
								outer.level().addFreshEntity(slime);
								outer.discard();
								MissionHelper.triggerMissionForPlayer(
										this.missionId, this.type,
										player, slime, player1 -> {
										}
								);
								return true;
							}
							return false;
						}).orElse(false);
			}
		},
		QUARREL("slime3", MissionType.FINISH) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, GuardSlimeEntity outer) {
				return this.tryTriggerSimple(serverLevel, outer, () -> {});
			}

			@Override
			boolean tryTriggerSimple(ServerLevel serverLevel, LivingEntity outer, Runnable extra) {
				return serverLevel.players().stream()
						.filter(
								player -> player.closerThan(outer, 6.0D) &&
										player instanceof IMonsterHero hero &&
										IMonsterHero.underMission(hero.rpm$getPlayerMissions(), this.missionId)
						).findAny().map(player -> {
							extra.run();
							MissionHelper.triggerMissionForPlayer(
									this.missionId, this.type,
									player, outer, player1 -> {
									}
							);
							return true;
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

		boolean tryTriggerSimple(ServerLevel serverLevel, LivingEntity outer, Runnable extra) {
			return false;
		}

		@Override
		public ResourceLocation missionId() {
			return missionId;
		}

		@Override
		public MissionType type() {
			return type;
		}
	}

	static class SlimeMissionTriggerGoal extends Goal {
		private final Slime slime;
		private int nextTriggerTime = 100;

		SlimeMissionTriggerGoal(Slime slime) {
			this.slime = slime;
		}

		@Override
		public boolean canUse() {
			return true;
		}

		@Override
		public void tick() {
			if(this.slime.level() instanceof ServerLevel serverLevel && --this.nextTriggerTime <= 0) {
				this.nextTriggerTime = this.adjustedTickDelay(100);
				GuardSlimeMissions.QUARREL.tryTriggerSimple(serverLevel, this.slime, () -> this.slime.goalSelector.removeGoal(this));
			}
		}
	}
}
