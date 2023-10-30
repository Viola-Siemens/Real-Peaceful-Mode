package com.hexagram2021.real_peaceful_mode.common.entity.misc;

import com.hexagram2021.real_peaceful_mode.common.entity.ICrackable;
import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SkeletonSkullEntity extends AbstractHurtingProjectile implements ICrackable {
    private boolean crackable;

    public SkeletonSkullEntity(EntityType<? extends SkeletonSkullEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SkeletonSkullEntity(Level level, LivingEntity owner, double vecX, double vecY, double vecZ) {
        super(RPMEntities.SKELETON_SKULL, owner, vecX, vecY, vecZ, level);
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    private static float getHurtDamage(float targetHealth) {
        if(targetHealth < 60.0F) {
            return 3.5F;
        }if(targetHealth < 80.0F) {
            float diff = targetHealth - 60.0F;
            return 0.0025F * diff * diff + 3.5F;
        }
        if(targetHealth < 400.0F) {
            return targetHealth * 0.05F;
        }
        if(targetHealth < 10000.0F) {
            return 2.0F * Mth.sqrt(targetHealth) - 20.0F;
        }
        return 200.0F - 40000.0F / (targetHealth - 8000.0F);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (!this.level().isClientSide) {
            Entity attackTarget = entityHitResult.getEntity();
            Entity owner = this.getOwner();
            DamageSource damageSource = owner instanceof Player player ?
                    this.damageSources().playerAttack(player) :
                    owner instanceof LivingEntity livingEntity ? this.damageSources().mobAttack(livingEntity) : this.damageSources().magic();
            float targetHealth = attackTarget instanceof LivingEntity livingEntity ? livingEntity.getHealth() : 0.0F;
            float damage = getHurtDamage(targetHealth);
            if(this.isOnFire()) {
                damage += 1.0F;
                attackTarget.setSecondsOnFire(10);
            }
            attackTarget.hurt(damageSource, damage);
            this.level().playSound(null, attackTarget.blockPosition(), SoundEvents.SKELETON_AMBIENT, SoundSource.HOSTILE);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        ICrackable.onHitBlock(blockHitResult, this);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float v) {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void setCrackable(boolean crackable) {
        this.crackable = crackable;
    }

    @Override
    public boolean getCrackable() {
        return this.crackable;
    }
}
