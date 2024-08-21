package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.api.IMissionProvider;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.api.MissionType;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class HuskWorkmanEntity extends PathfinderMob implements IMissionProvider {
	public HuskWorkmanEntity(EntityType<? extends HuskWorkmanEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(2, new PanicGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.125D).add(Attributes.MAX_HEALTH, 50.0D);
	}

	@Override
	public void checkDespawn() {
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.HUSK_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.HUSK_HURT;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.HUSK_STEP, 0.15F, 1.0F);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.HUSK_DEATH;
	}

	@Override
	public HuskWorkmanMissions getTriggerableMission() {
		if(this.isNoAi()) {
			return HuskWorkmanMissions.MEET_FIRST_TIME;
		}
		return null;
	}

	private int checkNearbyPlayers = 100;

	@Override
	public void tick() {
		if(--this.checkNearbyPlayers <= 0) {
			this.checkNearbyPlayers = 100;
			if (this.level() instanceof ServerLevel serverLevel) {
				HuskWorkmanMissions mission = this.getTriggerableMission();
				if(mission != null) {
					mission.tryTrigger(serverLevel, this);
				}
			}
		}
		super.tick();
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemInHand = player.getItemInHand(hand);
		if(itemInHand.is(RPMItems.Materials.PAC.get())) {
			if(player instanceof ServerPlayer serverPlayer) {
				MissionHelper.triggerMissionForPlayer(
						HuskWorkmanMissions.GET_PAC.missionId(), HuskWorkmanMissions.GET_PAC.type(), serverPlayer,
						this, player1 -> player1.getItemInHand(hand).shrink(1)
				);
				return InteractionResult.CONSUME;
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (!this.level().isClientSide && this.isAlive() && this.tickCount % 20 == 0) {
			this.heal(1.0F);
		}
	}

	public enum HuskWorkmanMissions implements IMissionProvider.TriggerableMission<HuskWorkmanEntity> {
		MEET_FIRST_TIME("husk2", MissionType.RECEIVE) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, HuskWorkmanEntity outer) {
				return serverLevel.players().stream().filter(player -> player.closerThan(outer, 6.0D)).findAny().map(player -> {
					if(player instanceof IMonsterHero hero && !IMonsterHero.completeMission(hero.getPlayerMissions(), this.missionId)) {
						outer.setNoAi(false);
						MissionHelper.triggerMissionForPlayer(
								this.missionId, this.type,
								player, outer, player1 -> {}
						);
						return true;
					}
					return false;
				}).orElse(false);
			}
		},
		GET_PAC("husk2", MissionType.FINISH) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, HuskWorkmanEntity outer) {
				return false;
			}
		};

		final ResourceLocation missionId;
		final MissionType type;

		HuskWorkmanMissions(String mission, MissionType type) {
			this.missionId = new ResourceLocation(MODID, mission);
			this.type = type;
		}

		public ResourceLocation missionId() {
			return missionId;
		}

		public MissionType type() {
			return type;
		}

		@Override
		public String getSerializedName() {
			return this.missionId + "/" + this.type.getSerializedName();
		}

		@Override
		public abstract boolean tryTrigger(ServerLevel serverLevel, HuskWorkmanEntity outer);
	}
}
