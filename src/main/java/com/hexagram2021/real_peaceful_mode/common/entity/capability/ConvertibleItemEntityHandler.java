package com.hexagram2021.real_peaceful_mode.common.entity.capability;

import com.hexagram2021.real_peaceful_mode.common.register.RPMCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConvertibleItemEntityHandler implements ICapabilityProvider, INBTSerializable<Tag> {
	private final ItemEntityConvertible itemEntityConvertible;
	private final LazyOptional<IItemEntityConvertible> holder;

	public ConvertibleItemEntityHandler(int maxRemainingTicks) {
		this.itemEntityConvertible = new ItemEntityConvertible(maxRemainingTicks);
		this.holder = LazyOptional.of(() -> this.itemEntityConvertible);
	}

	@Override @NotNull
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		return RPMCapabilities.ITEM_ENTITY_CONVERTIBLE.orEmpty(cap, this.holder);
	}

	@Override
	public Tag serializeNBT() {
		return this.itemEntityConvertible.serializeNBT();
	}

	@Override
	public void deserializeNBT(Tag nbt) {
		this.itemEntityConvertible.deserializeNBT(nbt);
	}
}
