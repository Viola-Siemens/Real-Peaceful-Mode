package com.hexagram2021.real_peaceful_mode.mixin;

import com.hexagram2021.real_peaceful_mode.common.item.SlimeScepterItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin {
	@Inject(method = "canEnchant", at = @At(value = "HEAD"), cancellable = true)
	public void rpm$supportCustomWeapons(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if(itemStack.getItem() instanceof SlimeScepterItem) {
			cir.setReturnValue(true);
		}
	}
}
