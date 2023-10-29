package com.hexagram2021.real_peaceful_mode.common.item;

import com.hexagram2021.real_peaceful_mode.common.register.RPMCapabilities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConvertibleSpiritBeadItem extends SpiritBeadItem implements ConvertibleItem {
	private final IdentityHashMap<Function<ItemEntity, Boolean>, Supplier<? extends Item>> conditionMap = new IdentityHashMap<>();

	public ConvertibleSpiritBeadItem(@Nullable EntityType<?> entityType, Properties properties) {
		super(entityType, properties);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		if(!entity.level().isClientSide) {
			entity.getCapability(RPMCapabilities.ITEM_ENTITY_CONVERTIBLE).ifPresent(c -> c.tick(entity));
		}
		return false;
	}

	/**
	 * @param entity		The item entity of this item.
	 * @return				null if condition not matches; otherwise if condition matches.
	 */
	@Override @Nullable
	public Item checkCondition(ItemEntity entity) {
		return this.conditionMap.entrySet().stream()
				.filter(entry -> entry.getKey().apply(entity))
				.findFirst()
				.map(entry -> entry.getValue().get())
				.orElse(null);
	}

	@Override
	public ConvertibleSpiritBeadItem addConvertItem(Function<ItemEntity, Boolean> condition, Supplier<? extends Item> newItem) {
		this.conditionMap.put(condition, newItem);
		return this;
	}
}
