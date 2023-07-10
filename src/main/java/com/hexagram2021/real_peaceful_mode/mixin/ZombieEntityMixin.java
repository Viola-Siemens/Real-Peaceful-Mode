package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Zombie.class)
public abstract class ZombieEntityMixin extends Monster implements IFriendlyMonster {
	private static final String TAG_FIGHT_FOR_PLAYER = "RPM_FightForPlayer";
	private boolean fightForPlayer = false;
	@Nullable
	private Goal attackDarkZombieKnightSelector = null;

	protected ZombieEntityMixin(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void getRPMZombieAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
		this.fightForPlayer = nbt.contains(TAG_FIGHT_FOR_PLAYER, Tag.TAG_BYTE) && nbt.getBoolean(TAG_FIGHT_FOR_PLAYER);
		if(this.fightForPlayer) {
			if(this.attackDarkZombieKnightSelector == null) {
				this.attackDarkZombieKnightSelector = new NearestAttackableTargetGoal<>(this, DarkZombieKnight.class, false);
			}
			this.targetSelector.addGoal(2, this.attackDarkZombieKnightSelector);
		} else {
			if(this.attackDarkZombieKnightSelector != null) {
				this.targetSelector.removeGoal(this.attackDarkZombieKnightSelector);
				this.attackDarkZombieKnightSelector = null;
			}
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void readRPMZombieAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
		if(this.fightForPlayer) {
			nbt.putBoolean(TAG_FIGHT_FOR_PLAYER, true);
		}
	}

	@Override
	public boolean preventAttack(@Nullable LivingEntity target) {
		if(this.fightForPlayer && target instanceof Player) {
			return true;
		}
		return this.level().players().stream().anyMatch(player -> {
			if(player instanceof IMonsterHero hero) {
				return hero.isHero(EntityType.ZOMBIE);
			}
			return false;
		}) && target instanceof AbstractVillager;
	}
}
