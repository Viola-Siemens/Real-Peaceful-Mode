package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.common.entity.goal.ZombieKnightAttackGoal;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.register.RPMSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class DarkZombieKnight extends Monster {
	private static final EntityDataAccessor<Boolean> DATA_ATTACK_PLAYER_AFTER_TYRANT_DEATH = SynchedEntityData.defineId(DarkZombieKnight.class, EntityDataSerializers.BOOLEAN);

	public DarkZombieKnight(EntityType<? extends DarkZombieKnight> entityType, Level level) {
		super(entityType, level);
		this.setBuster(this.getRandom().nextInt(3) != 0);
		this.xpReward = XP_REWARD_LARGE;
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(DATA_ATTACK_PLAYER_AFTER_TYRANT_DEATH, false);
	}

	public void setBuster(boolean buster) {
		this.getEntityData().set(DATA_ATTACK_PLAYER_AFTER_TYRANT_DEATH, buster);
	}

	public boolean isBuster() {
		return this.getEntityData().get(DATA_ATTACK_PLAYER_AFTER_TYRANT_DEATH);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(2, new ZombieKnightAttackGoal(this));
		this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, player -> player instanceof IMonsterHero hero && hero.isHero(EntityType.ZOMBIE) && !this.isBuster(), 12.0F, 1.0D, 1.2D, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
		this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, () -> true));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, player -> !(player instanceof IMonsterHero hero) || !hero.isHero(EntityType.ZOMBIE) || this.isBuster()));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.ARMOR, 3.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
	}

	public void aiStep() {
		if (this.isAlive()) {
			boolean flag = this.isSunBurnTick();
			if (flag) {
				ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
				if (!itemstack.isEmpty()) {
					if (itemstack.isDamageableItem()) {
						itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
						if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
							this.broadcastBreakEvent(EquipmentSlot.HEAD);
							this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
						}
					}

					flag = false;
				}

				if (flag) {
					this.setSecondsOnFire(8);
				}
			}
		}

		super.aiStep();
	}
	
	@Override
	public double getMyRidingOffset() {
		return -0.45D;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return RPMSounds.DARK_ZOMBIE_KNIGHT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return RPMSounds.DARK_ZOMBIE_KNIGHT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return RPMSounds.DARK_ZOMBIE_KNIGHT_DEATH;
	}

	protected SoundEvent getStepSound() {
		return SoundEvents.ZOMBIE_STEP;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(this.getStepSound(), 0.1F, 1.0F);
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEAD;
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance) {
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(RPMItems.Weapons.IRON_PIKE));
	}
	
	private static final String TAG_IS_BUSTER = "buster";
	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putBoolean(TAG_IS_BUSTER, this.isBuster());
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if(nbt.contains(TAG_IS_BUSTER, Tag.TAG_BYTE)) {
			this.setBuster(nbt.getBoolean(TAG_IS_BUSTER));
		} else {
			this.setBuster(this.getRandom().nextInt(3) != 0);
		}
	}
	
	@SuppressWarnings("OverrideOnly")
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyInstance,
										MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
		spawnGroupData = super.finalizeSpawn(level, difficultyInstance, spawnType, spawnGroupData, tag);
		this.setCanPickUpLoot(true);
		RandomSource randomsource = level.getRandom();
		this.populateDefaultEquipmentSlots(randomsource, difficultyInstance);
		this.populateDefaultEquipmentEnchantments(randomsource, difficultyInstance);
		return spawnGroupData;
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource damageSource, int looting, boolean hitByPlayer) {
		super.dropCustomDeathLoot(damageSource, looting, hitByPlayer);
		Entity entity = damageSource.getEntity();
		if (entity instanceof Creeper creeper) {
			if (creeper.canDropMobsSkull()) {
				creeper.increaseDroppedSkulls();
				this.spawnAtLocation(RPMBlocks.Decoration.DARK_ZOMBIE_KNIGHT_SKULL);
			}
		}
	}
}
