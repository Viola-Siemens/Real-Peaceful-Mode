package com.hexagram2021.real_peaceful_mode.common.entity.boss;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.SkeletonSkullEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.register.RPMSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class SkeletonKing extends PathfinderMob implements NeutralMob, RangedAttackMob {
	private int remainingPersistentAngerTime;

	@Nullable
	private UUID persistentAngerTarget;

	private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

	public SkeletonKing(EntityType<? extends SkeletonKing> entityType, Level level) {
		super(entityType, level);
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.addBehaviourGoals();
	}

	protected void addBehaviourGoals() {
		this.targetSelector.addGoal(1, new RangedSkullAttackGoal(1.0D, 10, 16.0F));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
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
		return RPMSounds.SKELETON_KING_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return RPMSounds.SKELETON_KING_DEATH;
	}

	@Override
	public int getRemainingPersistentAngerTime() {
		return this.remainingPersistentAngerTime;
	}

	@Override
	public void setRemainingPersistentAngerTime(int time) {
		this.remainingPersistentAngerTime = time;
	}

	@Override @Nullable
	public UUID getPersistentAngerTarget() {
		return this.persistentAngerTarget;
	}

	@Override
	public void setPersistentAngerTarget(@Nullable UUID target) {
		this.persistentAngerTarget = target;
	}

	@Override
	public void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
	}

	@Override
	public void aiStep() {
		super.aiStep();

		if (!this.level().isClientSide) {
			this.updatePersistentAnger((ServerLevel)this.level(), true);
		}
	}

	@Override
	public boolean hurt(DamageSource damageSource, float v) {
		Entity entity = damageSource.getEntity();
		if(entity instanceof IMonsterHero hero && hero.isHero(EntityType.SKELETON)) {
			return false;
		}

		return super.hurt(damageSource, v);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemInHand = player.getItemInHand(hand);
		if(player instanceof ServerPlayer serverPlayer) {
			if (itemInHand.is(RPMItems.Materials.CRYSTAL_SKULL.get())) {
				MissionHelper.triggerMissionForPlayer(
						new ResourceLocation(MODID, "skeleton2"), SummonBlockEntity.SummonMissionType.FINISH, serverPlayer,
						this, player1 -> player1.getItemInHand(hand).shrink(1)
				);
			}
			if (itemInHand.is(Items.SOUL_SOIL) && itemInHand.getCount() >= 64) {
				MissionHelper.triggerMissionForPlayer(
						new ResourceLocation(MODID, "skeleton3"), SummonBlockEntity.SummonMissionType.FINISH, serverPlayer,
						this, player1 -> {
							player1.getItemInHand(hand).shrink(64);
							this.getMainHandItem().setCount(0);
						}
				);
			}
			return InteractionResult.PASS;
		}
		return (itemInHand.is(RPMItems.Materials.CRYSTAL_SKULL.get()) || (itemInHand.is(Items.SOUL_SOIL) && itemInHand.getCount() >= 64)) ?
				InteractionResult.SUCCESS : InteractionResult.PASS;
	}

	@Override
	public void performRangedAttack(LivingEntity target, float power) {
		if(this.getMainHandItem().is(RPMItems.Weapons.SKELETON_SCEPTER.get())) {
			double x = target.getX() - this.getX();
			double z = target.getZ() - this.getZ();
			double y = target.getY() - this.getY(1.0D / 3.0D);
			double r = Math.sqrt(x * x + z * z);
			double r2 = Math.sqrt(x * x + y * y + z * z);
			SkeletonSkullEntity skeletonSkullEntity = new SkeletonSkullEntity(this.level(), this, x / r2, y / r2, z / r2);
			skeletonSkullEntity.setPos(this.getX() + x / r, this.getY(1.0D / 3.0D), this.getZ() + z / r);
			this.level().addFreshEntity(skeletonSkullEntity);
		}
	}

	class RangedSkullAttackGoal extends Goal {
		private final double speedModifier;
		private final int attackIntervalMin;
		private final float attackRadiusSqr;
		private int attackTime = -1;

		public RangedSkullAttackGoal(double speedModifier, int attackInterval, float attackRadius) {
			this.speedModifier = speedModifier;
			this.attackIntervalMin = attackInterval;
			this.attackRadiusSqr = attackRadius * attackRadius;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return SkeletonKing.this.getTarget() != null && this.isHoldingScepter();
		}

		protected boolean isHoldingScepter() {
			return SkeletonKing.this.isHolding(itemStack -> itemStack.is(RPMItems.Weapons.SKELETON_SCEPTER.get()));
		}

		@Override
		public boolean canContinueToUse() {
			return (this.canUse() || !SkeletonKing.this.getNavigation().isDone()) && this.isHoldingScepter();
		}

		@Override
		public void start() {
			super.start();
			SkeletonKing.this.setAggressive(true);
		}

		@Override
		public void stop() {
			super.stop();
			SkeletonKing.this.setAggressive(false);
			this.attackTime = -1;
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		public void tick() {
			LivingEntity target = SkeletonKing.this.getTarget();
			if (target != null) {
				double distanceSqr = SkeletonKing.this.distanceToSqr(target.getX(), target.getY(), target.getZ());

				if (!(distanceSqr > (double)this.attackRadiusSqr)) {
					SkeletonKing.this.getNavigation().stop();
				} else {
					SkeletonKing.this.getNavigation().moveTo(target, this.speedModifier);
				}

				SkeletonKing.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

				--this.attackTime;

				if (SkeletonKing.this.getSensing().hasLineOfSight(target) && this.attackTime <= 0) {
					SkeletonKing.this.performRangedAttack(target, 0);
					this.attackTime = this.attackIntervalMin;
				}
			}
		}
	}
}
