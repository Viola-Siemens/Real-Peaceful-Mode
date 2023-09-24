package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
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

public class HuskWorkmanEntity extends PathfinderMob {
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

	private int checkNearbyPlayers = 100;

	@Override
	public void tick() {
		if(this.isNoAi()) {
			if(--this.checkNearbyPlayers <= 0) {
				this.checkNearbyPlayers = 100;
				if (this.level() instanceof ServerLevel serverLevel) {
						serverLevel.players().stream().filter(player -> player.closerThan(this, 6.0D)).findAny().ifPresent(player -> MissionHelper.triggerMissionForPlayer(
								new ResourceLocation(MODID, "husk2"), SummonBlockEntity.SummonMissionType.RECEIVE,
								player, this, player1 -> this.setNoAi(false)
						));
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
						new ResourceLocation(MODID, "husk2"), SummonBlockEntity.SummonMissionType.FINISH, serverPlayer,
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
}
