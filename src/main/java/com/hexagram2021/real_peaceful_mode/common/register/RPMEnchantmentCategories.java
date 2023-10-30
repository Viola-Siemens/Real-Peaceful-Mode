package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.item.ScepterItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class RPMEnchantmentCategories {
	public static final EnchantmentCategory SCEPTER = EnchantmentCategory.create("SCEPTER", item -> item instanceof ScepterItem<?>);

	public static void init() {
	}
}
