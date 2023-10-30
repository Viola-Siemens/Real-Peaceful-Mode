package com.hexagram2021.real_peaceful_mode.common.entity.misc;

import com.hexagram2021.real_peaceful_mode.common.entity.ICrackable;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class TinyFireballEntity extends Fireball implements ICrackable {
    private boolean crackable;

    public TinyFireballEntity(EntityType<? extends TinyFireballEntity> entityType, Level level) {
        super(entityType, level);
    }

    public TinyFireballEntity(Level level, double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
        super(RPMEntities.TINY_FIREBALL, posX, posY, posZ, vecX, vecY, vecZ, level);
    }

    public TinyFireballEntity(Level level, LivingEntity owner, double vecX, double vecY, double vecZ) {
        super(RPMEntities.TINY_FIREBALL, owner, vecX, vecY, vecZ, level);
    }

    @Override
    public boolean displayFireAnimation() {
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
            float damage = 3.0F;
            if(this.isOnFire()) {
                damage += 1.0F;
                attackTarget.setSecondsOnFire(10);
            }
            attackTarget.hurt(damageSource, damage);
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Nullable
    public static BlockState getSiltedBlockState(BlockState origin) {
        BlockState newBlock = null;
        if (origin.is(Blocks.SANDSTONE)) {
            newBlock = RPMBlocks.Decoration.SILTSTONE.defaultBlockState();
        } else if (origin.is(Blocks.SANDSTONE_SLAB)) {
            newBlock = RPMBlocks.Decoration.SILTSTONE_SLAB.defaultBlockState();
        } else if (origin.is(Blocks.SANDSTONE_STAIRS)) {
            newBlock = RPMBlocks.Decoration.SILTSTONE_STAIRS.defaultBlockState();
        } else if (origin.is(Blocks.SANDSTONE_WALL)) {
            newBlock = RPMBlocks.Decoration.SILTSTONE_WALL.defaultBlockState();
        } else if (origin.is(Blocks.SMOOTH_SANDSTONE)) {
            newBlock = RPMBlocks.Decoration.SMOOTH_SILTSTONE.defaultBlockState();
        } else if (origin.is(Blocks.SMOOTH_SANDSTONE_SLAB)) {
            newBlock = RPMBlocks.Decoration.SMOOTH_SILTSTONE_SLAB.defaultBlockState();
        } else if (origin.is(Blocks.SMOOTH_SANDSTONE_STAIRS)) {
            newBlock = RPMBlocks.Decoration.SMOOTH_SILTSTONE_STAIRS.defaultBlockState();
        } else if (origin.is(Blocks.GRASS_BLOCK)) {
            newBlock = Blocks.DIRT.defaultBlockState();
        }
        if(newBlock != null) {
            for(Property property: origin.getProperties()) {
                if(newBlock.hasProperty(property)) {
                    newBlock = newBlock.setValue(property, origin.getValue(property));
                }
            }
        }
        return newBlock;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if(!this.level().isClientSide) {
            BlockState origin = this.level().getBlockState(blockHitResult.getBlockPos());
            BlockState newBlock = getSiltedBlockState(origin);
            if (newBlock != null) {
                this.level().setBlock(blockHitResult.getBlockPos(), newBlock, Block.UPDATE_ALL);
            }
        }
        ICrackable.onHitBlock(blockHitResult, this);
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
    public boolean hurt(DamageSource damageSource, float v) {
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

    @Override
    public void setCrackable(boolean crackable) {
        this.crackable = crackable;
    }

    @Override
    public boolean getCrackable() {
        return this.crackable;
    }
}
