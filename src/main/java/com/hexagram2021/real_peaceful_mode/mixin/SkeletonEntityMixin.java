package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.IRightArmDetachable;
import com.hexagram2021.real_peaceful_mode.common.entity.goal.MonsterDanceGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mixin(Skeleton.class)
public abstract class SkeletonEntityMixin extends Monster implements IFriendlyMonster, IRightArmDetachable {
	private static final String TAG_MISSING_ARM = "RPM_MissingArm";

	@Nullable
	private BiFunction<ServerPlayer, ItemStack, Boolean> npcInteractAction = null;
	@Nullable
	private Consumer<Mob> npcTickAction = null;

	protected SkeletonEntityMixin(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
	}

	@SuppressWarnings("WrongEntityDataParameterClass")
	@Inject(method = "<clinit>", at = @At(value = "TAIL"))
	private static void defineEntityDataAccessor(CallbackInfo ci) {
		Data.DATA_SKELETON_DANCE = SynchedEntityData.defineId(Skeleton.class, EntityDataSerializers.BOOLEAN);
		Data.DATA_SKELETON_RIGHT_ARM_DETACHED = SynchedEntityData.defineId(Skeleton.class, EntityDataSerializers.BOOLEAN);
	}

	@Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
	public void defineDanceData(CallbackInfo ci) {
		this.entityData.define(Data.DATA_SKELETON_DANCE, false);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void addRPMExtraGoals(EntityType<? extends Skeleton> entityType, Level level, CallbackInfo ci) {
		if (level != null && !level.isClientSide) {
			this.goalSelector.addGoal(1, new MonsterDanceGoal<>(this));
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	public void getRPMSkeletonAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
		this.setDance(nbt.contains(TAG_DANCING, Tag.TAG_BYTE) && nbt.getBoolean(TAG_DANCING));
		this.setRightArmDetached(nbt.contains(TAG_MISSING_ARM, Tag.TAG_BYTE) && nbt.getBoolean(TAG_MISSING_ARM));
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	public void addRPMSkeletonAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
		nbt.putBoolean(TAG_DANCING, this.isDancing());
		nbt.putBoolean(TAG_MISSING_ARM, this.isRightArmDetached());
	}

	@Override
	public boolean preventAttack(@Nullable LivingEntity target) {
		return IFriendlyMonster.preventAttack(this.level(), this.getType(), target);
	}

	@Override
	public boolean isRightArmDetached() {
		return this.entityData.get(Data.DATA_SKELETON_RIGHT_ARM_DETACHED);
	}

	@Override
	public void setRightArmDetached(boolean detached) {
		this.entityData.set(Data.DATA_SKELETON_RIGHT_ARM_DETACHED, detached);
	}

	@Override
	public boolean isDancing() {
		return this.getEntityData().get(Data.DATA_SKELETON_DANCE);
	}

	@Override
	public void setDance(boolean dancing) {
		this.getEntityData().set(Data.DATA_SKELETON_DANCE, dancing);
	}


	@Override @Nullable
	public BiFunction<ServerPlayer, ItemStack, Boolean> getRandomEventNpcAction() {
		return this.npcInteractAction;
	}

	@Override
	public void setRandomEventNpcAction(@Nullable BiFunction<ServerPlayer, ItemStack, Boolean> action) {
		this.npcInteractAction = action;
	}

	@Override @Nullable
	public Consumer<Mob> getNpcExtraTickAction() {
		return this.npcTickAction;
	}

	@Override
	public void setNpcExtraTickAction(@Nullable Consumer<Mob> action) {
		this.npcTickAction = action;
	}
}
