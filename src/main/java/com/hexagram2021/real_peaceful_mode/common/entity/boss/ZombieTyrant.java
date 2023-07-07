package com.hexagram2021.real_peaceful_mode.common.entity.boss;

import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import com.hexagram2021.real_peaceful_mode.common.register.RPMSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class ZombieTyrant extends Mob implements Enemy {
	private static final EntityDataAccessor<Boolean> DATA_IS_SPELL = SynchedEntityData.defineId(ZombieTyrant.class, EntityDataSerializers.BOOLEAN);
	
	public ZombieTyrant(EntityType<? extends ZombieTyrant> entityType, Level level) {
		super(entityType, level);
	}

	public ZombieTyrant(Level level) {
		this(RPMEntities.ZOMBIE_TYRANT, level);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(4, new ZombieTyrant.SummonKnightsGoal());
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.addBehaviourGoals();
	}

	protected void addBehaviourGoals() {
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.125F).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.ARMOR, 5.0D).add(Attributes.MAX_HEALTH, 120.0D);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(DATA_IS_SPELL, false);
	}

	private void setSpelling(boolean spelling) {
		this.getEntityData().set(DATA_IS_SPELL, spelling);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
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
	protected SoundEvent getDeathSound() {
		return RPMSounds.ZOMBIE_TYRANT_DEATH;
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

			ZombieTyrant.this.setSpelling(true);
		}

		@Override
		public void tick() {
			--this.warmUpTickCount;
			if(this.warmUpTickCount <= 0) {
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
				ZombieTyrant.this.setSpelling(false);
			}
		}
		
		protected int getWarmupTime() {
			return 20;
		}
		
		protected int getCastingInterval() {
			return 200;
		}

		protected SoundEvent getSpellSound() {
			return RPMSounds.ZOMBIE_TYRANT_SPELL;
		}
	}
}
