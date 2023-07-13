package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.entity.ICrackable;
import com.hexagram2021.real_peaceful_mode.common.register.RPMEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class ArrowItemMixin {
	@Inject(method = "createArrow", at = @At(value = "RETURN"))
	public void addRPMEnchantments(Level level, ItemStack arrowItemStack, LivingEntity user, CallbackInfoReturnable<AbstractArrow> cir) {
		int crackingLevel = user.getMainHandItem().getEnchantmentLevel(RPMEnchantments.CRACKING.get());
		((ICrackable)(cir.getReturnValue())).setCrackable(crackingLevel > 1 || user.getRandom().nextBoolean());
	}
}
