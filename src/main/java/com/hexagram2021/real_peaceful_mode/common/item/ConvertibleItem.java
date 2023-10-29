package com.hexagram2021.real_peaceful_mode.common.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ConvertibleItem {
	/**
	 * @param entity		The item entity of this item.
	 * @return				null if condition not matches; otherwise if condition matches.
	 */
	@Nullable
	Item checkCondition(ItemEntity entity);

	ConvertibleSpiritBeadItem addConvertItem(Function<ItemEntity, Boolean> condition, Supplier<? extends Item> newItem);
}
