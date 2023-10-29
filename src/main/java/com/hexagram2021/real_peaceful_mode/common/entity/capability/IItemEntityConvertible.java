package com.hexagram2021.real_peaceful_mode.common.entity.capability;

import com.hexagram2021.real_peaceful_mode.common.item.ConvertibleItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import javax.annotation.Nullable;

@AutoRegisterCapability
public interface IItemEntityConvertible {
	int getRemainingTicks();
	int maxRemainingTicks();
	void setRemainingTicks(int newTicks);

	@Nullable
	Item getToConvert();
	void setToConvert(Item toConvert);

	@Nullable
	default Item checkCondition(ItemEntity entity) {
		if(entity.getItem().getItem() instanceof ConvertibleItem convertibleItem) {
			return convertibleItem.checkCondition(entity);
		}
		return null;
	}

	default void convert(ItemEntity entity) {
		if(this.getToConvert() != null) {
			entity.setItem(new ItemStack(this.getToConvert(), entity.getItem().getCount()));
			this.setRemainingTicks(this.maxRemainingTicks());
		}
	}

	default void tick(ItemEntity entity) {
		Item toConvert = this.checkCondition(entity);
		if(toConvert != null) {
			if(toConvert.equals(this.getToConvert())) {
				this.setRemainingTicks(this.getRemainingTicks() - 1);
				if (this.getRemainingTicks() <= 0) {
					this.convert(entity);
				}
			} else {
				this.setRemainingTicks(this.maxRemainingTicks());
				this.setToConvert(toConvert);
			}
		} else if(this.getRemainingTicks() < this.maxRemainingTicks()) {
			this.setRemainingTicks(this.getRemainingTicks() + 1);
		}
	}
}
