package com.hexagram2021.real_peaceful_mode.common.entity.boss;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.TinyFireballEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.register.RPMSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class HuskPharaoh extends PathfinderMob implements RangedAttackMob, Enemy {
	private static final EntityDataAccessor<Boolean> DATA_STONE = SynchedEntityData.defineId(HuskPharaoh.class, EntityDataSerializers.BOOLEAN);

	private static final float TRIGGER_MISSION_TOTAL_DAMAGE = 120.0F;

	private float totalDamage = 0.0F;

	public HuskPharaoh(EntityType<? extends HuskPharaoh> entityType, Level level) {
		super(entityType, level);
		this.xpReward = XP_REWARD_BOSS;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(2, new MagnetizeTargetGoal(20));
		this.goalSelector.addGoal(4, new RangedFireballAttackGoal(1.0D, 20, 32.0F));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_STONE, false);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.FOLLOW_RANGE, 32.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.125F)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.ARMOR, 5.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
				.add(Attributes.MAX_HEALTH, 160.0D);
	}

	@Override
	public void performRangedAttack(LivingEntity target, float power) {
		double x = target.getX() - this.getX();
		double z = target.getZ() - this.getZ();
		double y = target.getY() - this.getY(1.0D / 3.0D);
		double r = Math.sqrt(x * x + z * z);
		double r2 = Math.sqrt(x * x + y * y + z * z);
		TinyFireballEntity fireball = new TinyFireballEntity(this.level(), this, x / r2, y / r2, z / r2);
		fireball.setPos(this.getX() + x / r, this.getY(1.0D / 3.0D), this.getZ() + z / r);
		this.level().addFreshEntity(fireball);
		this.playSound(SoundEvents.FIRECHARGE_USE);
	}

	private int checkNearbyPlayers = 100;

	@Override
	public void tick() {
		if(this.isNoAi()) {
			if(--this.checkNearbyPlayers <= 0) {
				this.checkNearbyPlayers = 100;
				if (this.isStone() && this.level() instanceof ServerLevel serverLevel) {
					serverLevel.players().stream().filter(player -> player.closerThan(this, 6.0D)).findAny().ifPresent(player -> MissionHelper.triggerMissionForPlayer(
							new ResourceLocation(MODID, "husk3"), SummonBlockEntity.SummonMissionType.RECEIVE,
							player, this, player1 -> {
								this.setIsStone(false);
								this.heal(100.0F);
								serverLevel.setWeatherParameters(12000, 0, false, false);
							}
					));
				}
			}
		}
		if(this.isStone()) {
			if(!this.level().getLevelData().isRaining()) {
				this.setIsStone(false);
			}
		} else if(this.level().getLevelData().isRaining()) {
			this.setIsStone(true);
		}
		super.tick();
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (!this.level().isClientSide && this.isAlive() && this.tickCount % 100 == 0) {
			this.heal(1.0F);
		}
	}

	private static final ResourceLocation FIGHT_MISSION = new ResourceLocation(MODID, "husk1");
	private static final ResourceLocation WEAKEN_MISSION = new ResourceLocation(MODID, "husk2");
	@Override
	public boolean hurt(DamageSource damageSource, float v) {
		if(this.isStone()) {
			return (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC) || damageSource.is(DamageTypes.GENERIC_KILL)) &&
					super.hurt(damageSource, v);
		}
		Entity entity = damageSource.getEntity();
		if(entity instanceof IMonsterHero hero) {
			if(!IMonsterHero.completeMission(hero.getPlayerMissions(), WEAKEN_MISSION)) {
				this.heal(10.0F);
				if(v > 0) {
					this.totalDamage += v * 100.0F / (TRIGGER_MISSION_TOTAL_DAMAGE * 2.0F - this.totalDamage);
					if(this.totalDamage >= TRIGGER_MISSION_TOTAL_DAMAGE) {
						this.totalDamage = 0.0F;
						this.setIsStone(true);
						if(this.level() instanceof ServerLevel serverLevel) {
							serverLevel.setWeatherParameters(0, 12000, true, false);
						}
						MissionHelper.triggerMissionForPlayer(
								FIGHT_MISSION, SummonBlockEntity.SummonMissionType.FINISH,
								(ServerPlayer)entity, this, player -> {}
						);
					}
				}
				return super.hurt(damageSource, v / 5.0F);
			}
			return super.hurt(damageSource, v);
		}

		return (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC) || damageSource.is(DamageTypes.GENERIC_KILL)) &&
				super.hurt(damageSource, v);
	}

	@Override
	public void die(DamageSource damageSource) {
		if (damageSource.getEntity() instanceof IMonsterHero hero && !IMonsterHero.completeMission(hero.getPlayerMissions(), WEAKEN_MISSION)) {
			this.setHealth(100.0F);
			return;
		}
		super.die(damageSource);
	}

	@Override
	public void checkDespawn() {
	}

	@Override
	protected float getSoundVolume() {
		return 2.0F;
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return RPMSounds.HUSK_PHARAOH_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return RPMSounds.HUSK_PHARAOH_DEATH;
	}

	@Override
	public boolean canAttack(LivingEntity livingEntity) {
		return !this.isStone() && super.canAttack(livingEntity);
	}

	private static final String TAG_IS_STONE = "IsStone";
	private static final String TAG_TOTAL_DAMAGE = "TotalDamage";

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putBoolean(TAG_IS_STONE, this.isStone());
		nbt.putFloat(TAG_TOTAL_DAMAGE, this.getTotalDamage());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if(nbt.contains(TAG_IS_STONE, Tag.TAG_BYTE)) {
			this.setIsStone(nbt.getBoolean(TAG_IS_STONE));
		}
		if(nbt.contains(TAG_TOTAL_DAMAGE, Tag.TAG_FLOAT)) {
			this.setTotalDamage(nbt.getFloat(TAG_TOTAL_DAMAGE));
		}
	}

	public boolean isStone() {
		return this.getEntityData().get(DATA_STONE);
	}

	public void setIsStone(boolean value) {
		this.getEntityData().set(DATA_STONE, value);
		this.setNoAi(value);
		if(value) {
			this.setTarget(null);
		}
	}

	public float getTotalDamage() {
		return this.totalDamage;
	}

	public void setTotalDamage(float totalDamage) {
		this.totalDamage = totalDamage;
	}

	class MagnetizeTargetGoal extends Goal {
		private final int attackIntervalMin;
		private int attackTime = -1;

		public MagnetizeTargetGoal(int attackIntervalMin) {
			this.attackIntervalMin = attackIntervalMin;
		}

		@Override
		public boolean canUse() {
			LivingEntity target = HuskPharaoh.this.getTarget();
			return ((target instanceof IMonsterHero hero && IMonsterHero.underMission(hero.getPlayerMissions(), FIGHT_MISSION)) || target instanceof Mob) &&
					!target.closerThan(HuskPharaoh.this, 16.0D);
		}

		@Override
		public boolean canContinueToUse() {
			return HuskPharaoh.this.getTarget() != null;
		}

		@Override
		public void stop() {
			super.stop();
			this.attackTime = -1;
		}

		@Override
		public void tick() {
			LivingEntity target = HuskPharaoh.this.getTarget();
			if(target != null) {
				--this.attackTime;
				if(this.attackTime <= 0) {
					Vec3 diff = HuskPharaoh.this.position().subtract(target.position());
					double mag = Math.sqrt(diff.length()) / 4.0D;
					target.move(MoverType.SELF, diff.normalize().multiply(mag, 1.0D, mag));
					this.attackTime = this.attackIntervalMin;
				}
			}
		}
	}

	class RangedFireballAttackGoal extends Goal {
		private final double speedModifier;
		private final int attackIntervalMin;
		private final float attackRadiusSqr;
		private int attackTime = -1;

		public RangedFireballAttackGoal(double speedModifier, int attackInterval, float attackRadius) {
			this.speedModifier = speedModifier;
			this.attackIntervalMin = attackInterval;
			this.attackRadiusSqr = attackRadius * attackRadius;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return HuskPharaoh.this.getTarget() != null && this.isHoldingScepter();
		}

		protected boolean isHoldingScepter() {
			return HuskPharaoh.this.isHolding(itemStack -> itemStack.is(RPMItems.Weapons.PHARAOH_SCEPTER.get()));
		}

		@Override
		public boolean canContinueToUse() {
			return (this.canUse() || !HuskPharaoh.this.getNavigation().isDone()) && this.isHoldingScepter();
		}

		@Override
		public void start() {
			super.start();
			HuskPharaoh.this.setAggressive(true);
		}

		@Override
		public void stop() {
			super.stop();
			HuskPharaoh.this.setAggressive(false);
			this.attackTime = -1;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity target = HuskPharaoh.this.getTarget();
			if (target != null) {
				double distanceSqr = HuskPharaoh.this.distanceToSqr(target.getX(), target.getY(), target.getZ());

				if (!(distanceSqr > (double)this.attackRadiusSqr)) {
					HuskPharaoh.this.getNavigation().stop();
				} else {
					HuskPharaoh.this.getNavigation().moveTo(target, this.speedModifier);
				}

				HuskPharaoh.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

				--this.attackTime;

				if (HuskPharaoh.this.getSensing().hasLineOfSight(target) && this.attackTime <= 0) {
					HuskPharaoh.this.performRangedAttack(target, 0);
					this.attackTime = this.attackIntervalMin;
				}
			}
		}
	}
}
