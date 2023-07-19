package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.register.RPMStructureKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class PinkCreeperEntity extends PathfinderMob {
	@Nullable
	private UUID likedPlayer;

	private int receiveMissionTick = -1;

	public PinkCreeperEntity(EntityType<? extends PinkCreeperEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new PinkCreeperEntity.GoAwayFromLikedPlayer());
		this.goalSelector.addGoal(3, new PinkCreeperEntity.FollowLikedPlayerGoal());
		this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.8D));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 50.0D);
	}

	@Override
	public boolean hurt(DamageSource damageSource, float v) {
		Entity entity = damageSource.getEntity();
		if (entity instanceof Player player && player.getUUID().equals(this.likedPlayer)) {
			return false;
		}
		return super.hurt(damageSource, v);
	}

	@Override
	public void checkDespawn() {

	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putInt("ReceiveMissionTick", this.receiveMissionTick);
		if(this.likedPlayer != null) {
			nbt.putUUID("LikedPlayer", this.likedPlayer);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if(nbt.contains("ReceiveMissionTick", Tag.TAG_INT)) {
			this.receiveMissionTick = nbt.getInt("ReceiveMissionTick");
		} else {
			this.receiveMissionTick = -1;
		}
		if(nbt.contains("LikedPlayer", Tag.TAG_INT_ARRAY)) {
			this.likedPlayer = nbt.getUUID("LikedPlayer");
		}
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		return InteractionResult.PASS;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_32309_) {
		return SoundEvents.CREEPER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.CREEPER_DEATH;
	}

	private int checkNearbyPlayers = 100;
	private static final ItemStack CREEPER3_TRIGGER_ITEM = new ItemStack(RPMItems.Materials.EXPLOSION_BLOCKER);

	@Override
	public void tick() {
		if(this.isNoAi()) {
			if(--this.checkNearbyPlayers <= 0) {
				this.checkNearbyPlayers = 100;
				if (this.level() instanceof ServerLevel serverLevel) {
						serverLevel.players().stream().filter(player -> player.closerThan(this, 8.0D)).findAny().ifPresent(player -> MissionHelper.triggerMissionForPlayer(
								new ResourceLocation(MODID, "creeper2"), SummonBlockEntity.SummonMissionType.RECEIVE,
								player, this, player1 -> {
									this.setNoAi(false);
									this.setLikedPlayer(player1.getUUID());
								}
						));
				}
			}
		} else if(this.likedPlayer != null) {
			if(--this.checkNearbyPlayers <= 0) {
				this.checkNearbyPlayers = 100;
				if (this.level() instanceof ServerLevel serverLevel) {
					if(this.receiveMissionTick == -1) {
						BlockPos blockPos = this.blockPosition();
						if(serverLevel.structureManager().getStructureWithPieceAt(blockPos, RPMStructureKeys.CREEPER_TOWN).isValid()) {
							MissionHelper.triggerMissionForPlayers(
									new ResourceLocation(MODID, "creeper2"), SummonBlockEntity.SummonMissionType.FINISH,
									serverLevel, player -> player.closerThan(this, 16.0D), this, player -> this.setLikedPlayer(null)
							);
							this.restrictTo(blockPos, 16);
						}
					} else if(this.receiveMissionTick == -2) {
						Player player = serverLevel.getPlayerByUUID(this.likedPlayer);
						if(player != null && player.closerThan(this, 8.0D)) {
							MissionHelper.triggerMissionForPlayers(
									new ResourceLocation(MODID, "creeper3"), SummonBlockEntity.SummonMissionType.FINISH,
									serverLevel, player1 -> player1.closerThan(this, 16.0D), this, player1 -> this.setLikedPlayer(null)
							);
							this.restrictTo(player.blockPosition(), 16);
						}
					}
				}
			}
		} else {
			if(--this.checkNearbyPlayers <= 0) {
				this.checkNearbyPlayers = 100;
				if (this.level() instanceof ServerLevel serverLevel) {
					serverLevel.players().stream().filter(player -> player.closerThan(this, 8.0D) && player.getInventory().contains(CREEPER3_TRIGGER_ITEM)).findAny().ifPresent(player -> MissionHelper.triggerMissionForPlayer(
							new ResourceLocation(MODID, "creeper3"), SummonBlockEntity.SummonMissionType.RECEIVE,
							player, this, player1 -> {
								this.setNoAi(false);
								this.setLikedPlayer(player1.getUUID());
								this.receiveMissionTick = this.tickCount;
							}
					));
				}
			}
		}
		super.tick();
	}

	public void setLikedPlayer(@Nullable UUID player) {
		this.likedPlayer = player;
	}

	class GoAwayFromLikedPlayer extends Goal {
		@Nullable
		private Player player;
		private int timeToRecalculatePath;

		@Override
		public boolean canUse() {
			if(PinkCreeperEntity.this.receiveMissionTick < 0 || PinkCreeperEntity.this.likedPlayer == null) {
				return false;
			}
			this.player = PinkCreeperEntity.this.level().getPlayerByUUID(PinkCreeperEntity.this.likedPlayer);
			return this.player != null;
		}

		@Override
		public boolean canContinueToUse() {
			if(PinkCreeperEntity.this.receiveMissionTick < 0 || PinkCreeperEntity.this.likedPlayer == null) {
				return false;
			}
			return this.player != null;
		}

		@Override
		public void start() {
			this.timeToRecalculatePath = 0;
		}

		@Override
		public void stop() {
			this.player = null;
		}

		@Override
		public void tick() {
			if (this.player != null && --this.timeToRecalculatePath <= 0) {
				if(PinkCreeperEntity.this.distanceToSqr(this.player) > 64.0D) {
					if (PinkCreeperEntity.this.tickCount - PinkCreeperEntity.this.receiveMissionTick >= 400) {
						PinkCreeperEntity.this.receiveMissionTick = -2;
						this.timeToRecalculatePath = this.adjustedTickDelay(20);
					}
					return;
				}
				this.timeToRecalculatePath = this.adjustedTickDelay(20);
				Vec3 creeperPos = PinkCreeperEntity.this.position();
				Vec3 playerPos = this.player.position();
				Vec3 targetPos = creeperPos.subtract(playerPos).multiply(1.0D, 0.0D, 1.0D).normalize().multiply(16.0D, 0.0D, 16.0D).add(creeperPos);
				PinkCreeperEntity.this.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.0D);
			}
		}
	}

	class FollowLikedPlayerGoal extends Goal {
		@Nullable
		private Player player;
		private int timeToRecalculatePath;

		@Override
		public boolean canUse() {
			if(PinkCreeperEntity.this.receiveMissionTick > 0 || PinkCreeperEntity.this.likedPlayer == null) {
				return false;
			}
			this.player = PinkCreeperEntity.this.level().getPlayerByUUID(PinkCreeperEntity.this.likedPlayer);
			if(this.player == null) {
				return false;
			}
			double distance = PinkCreeperEntity.this.distanceToSqr(this.player);
			return distance > 4.0D;
		}

		@Override
		public boolean canContinueToUse() {
			if(PinkCreeperEntity.this.receiveMissionTick > 0 || PinkCreeperEntity.this.likedPlayer == null) {
				return false;
			}
			if(this.player == null || !this.player.isAlive()) {
				return false;
			}
			double distance = PinkCreeperEntity.this.distanceToSqr(this.player);
			return distance > 4.0D && distance < 200.0D;
		}

		@Override
		public void start() {
			this.timeToRecalculatePath = 0;
		}

		@Override
		public void stop() {
			this.player = null;
		}

		@Override
		public void tick() {
			if (this.player != null && --this.timeToRecalculatePath <= 0) {
				this.timeToRecalculatePath = this.adjustedTickDelay(10);
				PinkCreeperEntity.this.getNavigation().moveTo(this.player, 1.25D);
			}
		}
	}
}
