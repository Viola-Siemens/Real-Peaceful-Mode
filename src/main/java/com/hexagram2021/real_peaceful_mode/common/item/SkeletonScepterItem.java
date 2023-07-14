package com.hexagram2021.real_peaceful_mode.common.item;

import com.hexagram2021.real_peaceful_mode.common.entity.misc.SkeletonSkullEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class SkeletonScepterItem extends ProjectileWeaponItem implements Vanishable {
    public SkeletonScepterItem(Properties props) {
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
            SkeletonSkullEntity skull = new SkeletonSkullEntity(level, player, vec.x(), vec.y(), vec.z());
            skull.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
            scepter.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(player.getUsedItemHand()));
            level.addFreshEntity(skull);
            player.getCooldowns().addCooldown(this, 5);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(scepter);
    }
}
