package com.hexagram2021.real_peaceful_mode.common.crafting.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CultureTableRecipeInput(ItemStack mix1, ItemStack mix2) implements RecipeInput {
	public static final int SLOT_MIX1 = 0;
	public static final int SLOT_MIX2 = 1;

	@Override
	public ItemStack getItem(int index) {
		return switch (index) {
			case SLOT_MIX1 -> this.mix1;
			case SLOT_MIX2 -> this.mix2;
			default -> throw new IllegalArgumentException("No item for index " + index);
		};
	}

	@Override
	public int size() {
		return 2;
	}
}
