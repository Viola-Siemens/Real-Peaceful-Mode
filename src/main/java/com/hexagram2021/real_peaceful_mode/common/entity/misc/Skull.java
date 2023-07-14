package com.hexagram2021.real_peaceful_mode.common.entity.misc;

import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class Skull extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<Boolean> DATA_DANGEROUS = SynchedEntityData.defineId(Skull.class, EntityDataSerializers.BOOLEAN);

    public Skull(EntityType<? extends Skull> entityType, Level level) {
        super(entityType, level);
    }

    public Skull(Level level, LivingEntity owner, double vecX, double vecY, double vecZ) {
        super(RPMEntities.SKULL, owner, vecX, vecY, vecZ, level);
    }

    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }

    public boolean isOnFire() {
        return false;
    }

    public float getBlockExplosionResistance(Explosion p_37619_, BlockGetter p_37620_, BlockPos p_37621_, BlockState p_37622_, FluidState p_37623_, float p_37624_) {
        return 1F;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!this.level().isClientSide) {
            Entity attackTarget = entityHitResult.getEntity();
            Entity owner = this.getOwner();
            DamageSource damageSource = owner instanceof Player ? this.damageSources().playerAttack((Player) owner) : this.damageSources().magic();
            attackTarget.hurt(damageSource, 8.0F);
        }
    }

    protected void onHit(HitResult p_37628_) {
        super.onHit(p_37628_);
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, Level.ExplosionInteraction.NONE);
            this.discard();
        }

    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, false);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean p_37630_) {
        this.entityData.set(DATA_DANGEROUS, p_37630_);
    }

    protected boolean shouldBurn() {
        return false;
    }
}
