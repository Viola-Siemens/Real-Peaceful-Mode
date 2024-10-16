package com.hexagram2021.real_peaceful_mode.common.entity.capability;

import com.hexagram2021.real_peaceful_mode.common.item.ConvertibleItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IItemEntityConvertible {
	int getRemainingTicks();
	int maxRemainingTicks();
	void setRemainingTicks(int newTicks);

	Optional<Item> getToConvert();
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	void setToConvert(Optional<Item> toConvert);

	@Nullable
	default Item checkCondition(ItemEntity entity) {
		if(entity.getItem().getItem() instanceof ConvertibleItem convertibleItem) {
			return convertibleItem.checkCondition(entity);
		}
		return null;
	}

	default void convert(ItemEntity entity) {
		this.getToConvert().ifPresent(item -> {
			entity.setItem(new ItemStack(item, entity.getItem().getCount()));
			this.setRemainingTicks(this.maxRemainingTicks());
		});
	}

	default void tick(ItemEntity entity) {
		Item toConvert = this.checkCondition(entity);
		if(toConvert != null) {
			Item previous = this.getToConvert().orElse(null);
			if(toConvert.equals(previous)) {
				this.setRemainingTicks(this.getRemainingTicks() - 1);
				if (this.getRemainingTicks() <= 0) {
					this.convert(entity);
				}
			} else {
				this.setRemainingTicks(this.maxRemainingTicks());
				this.setToConvert(Optional.of(toConvert));
			}
		} else if(this.getRemainingTicks() < this.maxRemainingTicks()) {
			this.setRemainingTicks(this.getRemainingTicks() + 1);
		}
	}
}
