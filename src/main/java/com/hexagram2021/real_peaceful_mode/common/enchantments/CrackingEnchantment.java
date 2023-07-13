package com.hexagram2021.real_peaceful_mode.common.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class CrackingEnchantment extends Enchantment {
	public CrackingEnchantment(Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, EnchantmentCategory.BOW, equipmentSlots);
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinCost(int lvl) {
		return 12 + 20 * (lvl - 1);
	}

	@Override
	public int getMaxCost(int lvl) {
		return super.getMinCost(lvl) + 25;
	}
}
