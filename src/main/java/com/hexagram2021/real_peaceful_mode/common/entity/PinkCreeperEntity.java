package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.api.IMissionProvider;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.api.MissionType;
import com.hexagram2021.real_peaceful_mode.common.mission.IMissionStack;
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
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class PinkCreeperEntity extends PathfinderMob implements IMissionProvider {
	@Nullable
	private UUID likedPlayer;

	/**
	 * -1: default, able to trigger creeper2 r/f, creeper3 r
	 * -2: able to trigger creeper3 f
	 * positive value: stay far away from liked player
	 */
	private int receiveMissionTick = -1;

	public PinkCreeperEntity(EntityType<? extends PinkCreeperEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new PanicGoal(this, 1.0D));
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
		if(entity instanceof Creeper) {
			return super.hurt(damageSource, v / 2.0F);
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

	@Override @Nullable
	protected SoundEvent getAmbientSound() {
		return super.getAmbientSound();
	}

	@Override
	public int getAmbientSoundInterval() {
		return 1200 + this.random.nextInt(2400);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.CREEPER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.CREEPER_DEATH;
	}

	@Override @Nullable
	public PinkCreeperEntity.PinkCreeperMissions getTriggerableMission() {
		if(this.isNoAi()) {
			return PinkCreeperMissions.MEET_FIRST_TIME;
		}
		if(this.likedPlayer == null) {
			return PinkCreeperMissions.MAKE_EXPLOSION_BLOCKER;
		}
		if(this.receiveMissionTick == -1) {
			if(this.getRestrictRadius() < 0) {
				return PinkCreeperMissions.LEAD_ME_TO_TOWN;
			}
		} else if(this.receiveMissionTick == -2) {
			if(this.getRestrictRadius() < 0) {
				return PinkCreeperMissions.COME_BACK_AFTER_DISTRIBUTION;
			}
		}
		return null;
	}

	private int checkNearbyPlayers = 100;
	private static final ItemStack CREEPER3_TRIGGER_ITEM = new ItemStack(RPMItems.Materials.EXPLOSION_BLOCKER);

	@Override
	public void tick() {
		if(--this.checkNearbyPlayers <= 0) {
			this.checkNearbyPlayers = 100;
			if (this.level() instanceof ServerLevel serverLevel) {
				PinkCreeperMissions mission = this.getTriggerableMission();
				if(mission != null) {
					mission.tryTrigger(serverLevel, this);
				}
			}
		}

		super.tick();
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (!this.level().isClientSide && this.isAlive() && this.tickCount % 20 == 0) {
			this.heal(1.0F);
		}
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
				if(PinkCreeperEntity.this.distanceTo(this.player) > 40.0D) {
					if (PinkCreeperEntity.this.tickCount - PinkCreeperEntity.this.receiveMissionTick >= 600) {
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
				if(PinkCreeperEntity.this.distanceTo(this.player) > 16.0D) {

					Vec3 direction = new Vec3(
							this.player.getX() - PinkCreeperEntity.this.getX(),
							this.player.getY() - PinkCreeperEntity.this.getY(),
							this.player.getZ() - PinkCreeperEntity.this.getZ()
					).normalize();
					Vec3 destination = direction.scale(16.0D).add(PinkCreeperEntity.this.getX(), PinkCreeperEntity.this.getY(), PinkCreeperEntity.this.getZ());
					PinkCreeperEntity.this.navigation.moveTo(destination.x, destination.y, destination.z, 1.25D);
				}
				PinkCreeperEntity.this.getNavigation().moveTo(this.player, 1.0D);
			}
		}
	}

	public enum PinkCreeperMissions implements IMissionProvider.TriggerableMission<PinkCreeperEntity>, IMissionStack {
		MEET_FIRST_TIME("creeper2", MissionType.RECEIVE) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, PinkCreeperEntity outer) {
				return serverLevel.players().stream()
						.filter(
								player -> player.closerThan(outer, 6.0D) &&
										player instanceof IMonsterHero hero &&
										!IMonsterHero.completeMission(hero.rpm$getPlayerMissions(), this.missionId)
						).findAny().map(player -> {
							outer.setNoAi(false);
							outer.setLikedPlayer(player.getUUID());
							MissionHelper.triggerMissionForPlayer(
									this.missionId, this.type,
									player, outer, player1 -> {
									}
							);
							return true;
						}).orElse(false);
			}
		},
		LEAD_ME_TO_TOWN("creeper2", MissionType.FINISH) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, PinkCreeperEntity outer) {
				BlockPos blockPos = outer.blockPosition();
				if(serverLevel.structureManager().getStructureWithPieceAt(blockPos, RPMStructureKeys.CREEPER_TOWN).isValid()) {
					MissionHelper.triggerMissionForPlayers(
							this.missionId, this.type,
							serverLevel, player -> player.closerThan(outer, 32.0D), outer, player -> outer.setLikedPlayer(null)
					);
					outer.restrictTo(blockPos, 16);
					return true;
				}
				return false;
			}
		},
		MAKE_EXPLOSION_BLOCKER("creeper3", MissionType.RECEIVE) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, PinkCreeperEntity outer) {
				return serverLevel.players().stream()
						.filter(player -> player.closerThan(outer, 8.0D) && player.getInventory().contains(CREEPER3_TRIGGER_ITEM))
						.findAny().map(player -> {
					if(player instanceof IMonsterHero hero &&
							IMonsterHero.isAtMissionsBetween(hero.rpm$getPlayerMissions(), LEAD_ME_TO_TOWN.missionId, this.missionId)) {
						outer.setNoAi(false);
						outer.setLikedPlayer(player.getUUID());
						outer.clearRestriction();
						MissionHelper.triggerMissionForPlayer(
								this.missionId, MissionType.RECEIVE,
								player, outer, player1 -> outer.receiveMissionTick = outer.tickCount
						);
						return true;
					}
					return false;
				}).orElse(false);
			}
		},
		COME_BACK_AFTER_DISTRIBUTION("creeper3", MissionType.FINISH) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, PinkCreeperEntity outer) {
				assert outer.likedPlayer != null;
				Player liked = serverLevel.getPlayerByUUID(outer.likedPlayer);
				if(liked != null && liked.closerThan(outer, 8.0D)) {
					MissionHelper.triggerMissionForPlayers(
							this.missionId, this.type,
							serverLevel, player -> player.closerThan(outer, 16.0D), outer, player -> outer.setLikedPlayer(null)
					);
					outer.restrictTo(liked.blockPosition(), 16);
					return true;
				}
				return false;
			}
		};

		final ResourceLocation missionId;
		final MissionType type;

		PinkCreeperMissions(String mission, MissionType type) {
			this.missionId = new ResourceLocation(MODID, mission);
			this.type = type;
		}

		@Override
		public String getSerializedName() {
			return this.missionId + "/" + this.type.getSerializedName();
		}

		@Override
		public abstract boolean tryTrigger(ServerLevel serverLevel, PinkCreeperEntity outer);

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
