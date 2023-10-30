package com.hexagram2021.real_peaceful_mode.common.enchantments;

import com.hexagram2021.real_peaceful_mode.common.register.RPMEnchantmentCategories;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class UndeadFlameEnchantment extends Enchantment {
	public UndeadFlameEnchantment(Rarity rarity, EquipmentSlot... equipmentSlots) {
		super(rarity, RPMEnchantmentCategories.SCEPTER, equipmentSlots);
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getMinCost(int lvl) {
		return 15;
	}

	@Override
	public int getMaxCost(int lvl) {
		return 40;
	}

	@Override
	public boolean canEnchant(ItemStack itemStack) {
		return super.canEnchant(itemStack);
	}
}
