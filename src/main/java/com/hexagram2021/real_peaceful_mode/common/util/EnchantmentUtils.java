package com.hexagram2021.real_peaceful_mode.common.util;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EnchantmentUtils {
	public static int getEnchantmentLevel(ItemStack itemStack, RegistryAccess registryAccess, ResourceKey<Enchantment> enchantment) {
		Holder<Enchantment> holder = registryAccess.lookup(Registries.ENCHANTMENT).map(lookup -> lookup.getOrThrow(enchantment)).orElse(null);
		return holder == null ? 0 : itemStack.getEnchantmentLevel(holder);
	}

	private EnchantmentUtils() {
	}
}
