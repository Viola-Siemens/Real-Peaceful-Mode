package com.hexagram2021.real_peaceful_mode.common.entity.boss;

import com.hexagram2021.real_peaceful_mode.common.entity.misc.TinyFireballEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.register.RPMSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
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

import java.util.EnumSet;

public class HuskPharaoh extends PathfinderMob implements RangedAttackMob, Enemy {
	public HuskPharaoh(EntityType<? extends HuskPharaoh> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.addBehaviourGoals();
	}

	protected void addBehaviourGoals() {
		this.goalSelector.addGoal(4, new RangedFireballAttackGoal(1.0D, 10, 32.0F));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.FOLLOW_RANGE, 16.0D)
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
