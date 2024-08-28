package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnightEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.goal.MonsterDanceGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mixin(Zombie.class)
public abstract class ZombieEntityMixin extends Monster implements IFriendlyMonster {
	@Unique
	private static final String TAG_FIGHT_FOR_PLAYER = "RPM_FightForPlayer";
	@Unique
	private boolean rpm$fightForPlayer = false;
	@Unique
	@Nullable
	private Goal rpm$attackDarkZombieKnightSelector = null;

	@Unique
	@Nullable
	private BiFunction<ServerPlayer, ItemStack, Boolean> rpm$npcInteractAction = null;
	@Unique
	@Nullable
	private Consumer<Mob> rpm$npcTickAction = null;

	protected ZombieEntityMixin(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@SuppressWarnings("WrongEntityDataParameterClass")
	@Inject(method = "<clinit>", at = @At(value = "TAIL"))
	private static void rpm$defineEntityDataAccessor(CallbackInfo ci) {
		Data.DATA_ZOMBIE_DANCE = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
	}

	@Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
	public void rpm$defineDanceData(CallbackInfo ci) {
		this.entityData.define(Data.DATA_ZOMBIE_DANCE, false);
	}

	@Inject(method = "addBehaviourGoals", at = @At(value = "TAIL"))
	public void rpm$addRPMExtraGoals(CallbackInfo ci) {
		this.goalSelector.addGoal(1, new MonsterDanceGoal<>(this));
	}

	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void rpm$getRPMZombieAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
		this.rpm$fightForPlayer = nbt.contains(TAG_FIGHT_FOR_PLAYER, Tag.TAG_BYTE) && nbt.getBoolean(TAG_FIGHT_FOR_PLAYER);
		if(this.rpm$fightForPlayer) {
			if(this.rpm$attackDarkZombieKnightSelector == null) {
				this.rpm$attackDarkZombieKnightSelector = new NearestAttackableTargetGoal<>(this, DarkZombieKnightEntity.class, false);
			}
			this.targetSelector.addGoal(2, this.rpm$attackDarkZombieKnightSelector);
		} else {
			if(this.rpm$attackDarkZombieKnightSelector != null) {
				this.targetSelector.removeGoal(this.rpm$attackDarkZombieKnightSelector);
				this.rpm$attackDarkZombieKnightSelector = null;
			}
		}
		this.rpm$setDance(nbt.contains(TAG_DANCING, Tag.TAG_BYTE) && nbt.getBoolean(TAG_DANCING));
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void rpm$addRPMZombieAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
		if(this.rpm$fightForPlayer) {
			nbt.putBoolean(TAG_FIGHT_FOR_PLAYER, true);
		}
		nbt.putBoolean(TAG_DANCING, this.rpm$isDancing());
	}

	@Override
	public boolean rpm$preventAttack(@Nullable LivingEntity target) {
		if(this.rpm$fightForPlayer && target instanceof Player) {
			return true;
		}
		return IFriendlyMonster.preventAttack(this.level(), this.getType(), target);
	}

	@Override
	public boolean rpm$isDancing() {
		return this.getEntityData().get(Data.DATA_ZOMBIE_DANCE);
	}

	@Override
	public void rpm$setDance(boolean dancing) {
		this.getEntityData().set(Data.DATA_ZOMBIE_DANCE, dancing);
	}


	@Override @Nullable
	public BiFunction<ServerPlayer, ItemStack, Boolean> rpm$getRandomEventNpcAction() {
		return this.rpm$npcInteractAction;
	}

	@Override
	public void rpm$setRandomEventNpcAction(@Nullable BiFunction<ServerPlayer, ItemStack, Boolean> action) {
		this.rpm$npcInteractAction = action;
	}

	@Override @Nullable
	public Consumer<Mob> rpm$getNpcExtraTickAction() {
		return this.rpm$npcTickAction;
	}

	@Override
	public void rpm$setNpcExtraTickAction(@Nullable Consumer<Mob> action) {
		this.rpm$npcTickAction = action;
	}
}
