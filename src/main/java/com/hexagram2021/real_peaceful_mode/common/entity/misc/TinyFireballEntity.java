package com.hexagram2021.real_peaceful_mode.common.entity.misc;

import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class TinyFireballEntity extends Fireball {

    public TinyFireballEntity(EntityType<? extends TinyFireballEntity> entityType, Level level) {
        super(entityType, level);
    }

    public TinyFireballEntity(Level level, LivingEntity owner, double vecX, double vecY, double vecZ) {
        super(RPMEntities.TINY_FIREBALL, owner, vecX, vecY, vecZ, level);
    }

    @Override
    public boolean isOnFire() {
        return false;
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
            attackTarget.hurt(damageSource, 2.5F);
            if(attackTarget instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(RPMMobEffects.TRANCE.get(), 600));
                if(livingEntity.isOnFire()) {
                    livingEntity.setRemainingFireTicks(livingEntity.getRemainingFireTicks() + 20);
                }
                if(livingEntity instanceof Mob mob) {
                    mob.setTarget(null);
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult p_37628_) {
        super.onHit(p_37628_);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float value) {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(RPMItems.Weapons.TINY_FLAME) : itemstack;
    }
}
