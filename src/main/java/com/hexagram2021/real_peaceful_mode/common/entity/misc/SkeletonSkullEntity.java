package com.hexagram2021.real_peaceful_mode.common.entity.misc;

import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SkeletonSkullEntity extends AbstractHurtingProjectile {

    public SkeletonSkullEntity(EntityType<? extends SkeletonSkullEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SkeletonSkullEntity(Level level, LivingEntity owner, double vecX, double vecY, double vecZ) {
        super(RPMEntities.SKELETON_SKULL, owner, vecX, vecY, vecZ, level);
    }

    public boolean isOnFire() {
        return false;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!this.level().isClientSide) {
            Entity attackTarget = entityHitResult.getEntity();
            Entity owner = this.getOwner();
            DamageSource damageSource = owner instanceof Player player ?
                    this.damageSources().playerAttack(player) :
                    owner instanceof LivingEntity livingEntity ? this.damageSources().mobAttack(livingEntity) : this.damageSources().magic();
            attackTarget.hurt(damageSource, 3.0F);
        }
    }

    protected void onHit(HitResult p_37628_) {
        super.onHit(p_37628_);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }
}
