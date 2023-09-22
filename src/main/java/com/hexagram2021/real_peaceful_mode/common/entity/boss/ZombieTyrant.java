package com.hexagram2021.real_peaceful_mode.common.entity.boss;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import com.hexagram2021.real_peaceful_mode.common.register.RPMSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class ZombieTyrant extends Mob implements Enemy {
	private static final EntityDataAccessor<Integer> DATA_SPELL_COUNTER = SynchedEntityData.defineId(ZombieTyrant.class, EntityDataSerializers.INT);
	
	public ZombieTyrant(EntityType<? extends ZombieTyrant> entityType, Level level) {
		super(entityType, level);
		this.xpReward = XP_REWARD_BOSS;
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(4, new ZombieTyrant.SummonKnightsGoal());
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.FOLLOW_RANGE, 10.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.125F)
				.add(Attributes.ATTACK_DAMAGE, 1.0D)
				.add(Attributes.ARMOR, 5.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
				.add(Attributes.MAX_HEALTH, 160.0D);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(DATA_SPELL_COUNTER, 0);
	}

	public int getSpelling() {
		return this.getEntityData().get(DATA_SPELL_COUNTER);
	}

	private void setSpelling(int tick) {
		this.getEntityData().set(DATA_SPELL_COUNTER, tick);
	}

	@Override
	public void tick() {
		super.tick();
		if(this.getSpelling() > 0) {
			this.setSpelling(this.getSpelling() - 1);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
	}

	private static final ResourceLocation WEAKEN_MISSION = new ResourceLocation(MODID, "zombie2");
	@Override
	public boolean hurt(DamageSource damageSource, float v) {
		Entity entity = damageSource.getEntity();
		if(entity instanceof IMonsterHero hero) {
			if(!IMonsterHero.completeMission(hero.getPlayerMissions(), WEAKEN_MISSION)) {
				this.heal(20.0F);
				return super.hurt(damageSource, v / 5.0F);
			}
			return super.hurt(damageSource, v);
		}

		return (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) || damageSource.is(DamageTypes.GENERIC) || damageSource.is(DamageTypes.GENERIC_KILL)) &&
				super.hurt(damageSource, v);
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
		return RPMSounds.ZOMBIE_TYRANT_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return RPMSounds.ZOMBIE_TYRANT_DEATH;
	}
	
	@Override
	public void die(DamageSource damageSource) {
		if(damageSource.getEntity() instanceof IMonsterHero hero && !IMonsterHero.completeMission(hero.getPlayerMissions(), WEAKEN_MISSION)) {
			this.heal(100.0F);
			return;
		}
		if(this.level() instanceof ServerLevel serverLevel) {
			MissionHelper.triggerMissionForPlayers(
					new ResourceLocation(MODID, "zombie3"), SummonBlockEntity.SummonMissionType.FINISH, serverLevel,
					player -> player.closerThan(this, 32.0D), this, player -> {}
			);
			this.level().getEntitiesOfClass(DarkZombieKnight.class, this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE).forEach(knight -> knight.setTarget(null));
			this.level().getEntitiesOfClass(Zombie.class, this.getBoundingBox().inflate(32.0D), EntitySelector.ENTITY_STILL_ALIVE)
					.forEach(zombie -> {
						if(zombie instanceof IFriendlyMonster monster) {
							monster.setDance(true);
						}
					});
		}
		super.die(damageSource);
	}

	class SummonKnightsGoal extends Goal {
		private int nextAttackTickCount;
		private int warmUpTickCount;

		@Override
		public boolean canUse() {
			LivingEntity target = ZombieTyrant.this.getTarget();
			return target != null && target.isAlive() && ZombieTyrant.this.tickCount >= this.nextAttackTickCount;
		}

		@Override
		public boolean canContinueToUse() {
			LivingEntity target = ZombieTyrant.this.getTarget();
			return target != null && target.isAlive() && this.warmUpTickCount > 0;
		}
		
		@Override
		public void start() {
			this.warmUpTickCount = this.adjustedTickDelay(this.getWarmupTime());
			this.nextAttackTickCount = ZombieTyrant.this.tickCount + this.getCastingInterval();
			ZombieTyrant.this.playSound(this.getSpellSound(), 1.0F, 1.0F);

			ZombieTyrant.this.setSpelling(40);
		}

		@Override
		public void tick() {
			--this.warmUpTickCount;
			if(this.warmUpTickCount <= 0 && ZombieTyrant.this.isAlive()) {
				int r = ZombieTyrant.this.random.nextInt(3) + 2;
				ServerLevel level = (ServerLevel) ZombieTyrant.this.level();
				for(int i = 0; i < r; ++i) {
					double phi = ZombieTyrant.this.random.nextDouble() * Math.PI * 2;
					double dx = Math.cos(phi);
					double dz = Math.sin(phi);
					Vec3 position = ZombieTyrant.this.position().add(dx, 0.5, dz);
					DarkZombieKnight knight = RPMEntities.DARK_ZOMBIE_KNIGHT.create(level);
					if(knight != null) {
						knight.moveTo(position);
						knight.setTarget(ZombieTyrant.this.getTarget());
						ForgeEventFactory.onFinalizeSpawn(knight, level, level.getCurrentDifficultyAt(ZombieTyrant.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
						level.addFreshEntityWithPassengers(knight);
					}
				}
				LivingEntity livingEntity = ZombieTyrant.this.getTarget();
				if(livingEntity != null) {
					livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400, 1), ZombieTyrant.this);
				}
			}
		}
		
		protected int getWarmupTime() {
			return 20;
		}
		
		protected int getCastingInterval() {
			return 280;
		}

		protected SoundEvent getSpellSound() {
			return RPMSounds.ZOMBIE_TYRANT_SPELL;
		}
	}
}
