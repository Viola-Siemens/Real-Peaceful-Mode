package com.hexagram2021.real_peaceful_mode.common.entity.capability;

import com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class ItemEntityConvertible implements IItemEntityConvertible {
	public static final Codec<ItemEntityConvertible> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("MaxRemainingTicks").forGetter(ItemEntityConvertible::maxRemainingTicks),
			Codec.INT.fieldOf("RemainingTicks").forGetter(ItemEntityConvertible::getRemainingTicks),
			ResourceLocation.CODEC.optionalFieldOf("ToConvert").xmap(id -> id.map(BuiltInRegistries.ITEM::get), item -> item.map(RegistryHelper::getRegistryName)).forGetter(ItemEntityConvertible::getToConvert)
	).apply(instance, ItemEntityConvertible::new));

	protected final int maxRemainingTicks;
	protected int remainingTicks;

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	protected Optional<Item> toConvert;

	public ItemEntityConvertible(int maxRemainingTicks) {
		this(maxRemainingTicks, maxRemainingTicks, Optional.empty());
	}
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public ItemEntityConvertible(int maxRemainingTicks, int remainingTicks, Optional<Item> toConvert) {
		this.maxRemainingTicks = maxRemainingTicks;
		this.remainingTicks = remainingTicks;
		this.toConvert = toConvert;
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

	@Override
	public Optional<Item> getToConvert() {
		return this.toConvert;
	}

	@Override
	public void setToConvert(Optional<Item> toConvert) {
		this.toConvert = toConvert;
	}
}
