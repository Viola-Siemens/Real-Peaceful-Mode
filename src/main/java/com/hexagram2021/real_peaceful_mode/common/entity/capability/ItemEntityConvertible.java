package com.hexagram2021.real_peaceful_mode.common.entity.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public class ItemEntityConvertible implements IItemEntityConvertible, INBTSerializable<Tag> {
	protected final int maxRemainingTicks;
	protected int remainingTicks;

	@Nullable
	protected Item toConvert;

	public ItemEntityConvertible(int maxRemainingTicks) {
		this.maxRemainingTicks = maxRemainingTicks;
		this.remainingTicks = maxRemainingTicks;
		this.toConvert = null;
	}

	@Override
	public int getRemainingTicks() {
		return this.remainingTicks;
	}

	@Override
	public int maxRemainingTicks() {
		return this.maxRemainingTicks;
	}

	@Override
	public void setRemainingTicks(int newTicks) {
		this.remainingTicks = newTicks;
	}

	@Override @Nullable
	public Item getToConvert() {
		return this.toConvert;
	}

	@Override
	public void setToConvert(Item toConvert) {
		this.toConvert = toConvert;
	}

	@Override
	public Tag serializeNBT() {
		CompoundTag ret = new CompoundTag();
		ret.putInt("remainingTicks", this.remainingTicks);
		if(this.toConvert != null) {
			ret.putString("toConvert", getRegistryName(this.toConvert).toString());
		}
		return ret;
	}

	@Override
	public void deserializeNBT(Tag nbt) {
		if(nbt instanceof CompoundTag compoundTag) {
			if(compoundTag.contains("remainingTicks", Tag.TAG_INT)) {
				this.remainingTicks = compoundTag.getInt("remainingTicks");
			} else {
				this.remainingTicks = this.maxRemainingTicks;
			}
			if(compoundTag.contains("toConvert", Tag.TAG_STRING)) {
				String id = compoundTag.getString("toConvert");
				if(ResourceLocation.isValidResourceLocation(id)) {
					this.toConvert = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
				}
			}
		}
	}
}
