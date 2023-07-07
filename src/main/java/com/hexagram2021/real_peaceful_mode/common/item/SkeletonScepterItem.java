package com.hexagram2021.real_peaceful_mode.common.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Vanishable;

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

	//TODO
}
