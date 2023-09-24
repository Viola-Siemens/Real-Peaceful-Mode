package com.hexagram2021.real_peaceful_mode.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public abstract class ScepterItem<T extends AbstractHurtingProjectile> extends ProjectileWeaponItem implements Vanishable {
    public ScepterItem(Properties props) {
        super(props);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_OR_FIREWORK;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack scepter = player.getItemInHand(hand);
        if (!level.isClientSide) {
            Vec3 vec = player.getLookAngle();
            if(scepter.getMaxDamage() - scepter.getDamageValue() >= 10) {
                T projectile = this.createProjectile(level, player, vec.x(), vec.y(), vec.z());
                scepter.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(player.getUsedItemHand()));
                level.addFreshEntity(projectile);
                player.getCooldowns().addCooldown(this, 10);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(scepter);
    }

    public abstract T createProjectile(Level level, LivingEntity owner, double directionX, double directionY, double directionZ);
}
