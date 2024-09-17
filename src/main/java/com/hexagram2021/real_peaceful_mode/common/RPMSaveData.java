package com.hexagram2021.real_peaceful_mode.common;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;

public class RPMSaveData extends SavedData {
	@Nullable
	private static RPMSaveData INSTANCE;
	public static final String dataName = "RealPeacefulMode-SaveData";

	public RPMSaveData() {
		super();
	}

	public RPMSaveData(CompoundTag nbt, HolderLookup.Provider registries) {
		this();
	}

	@Override
	public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
		return nbt;
	}


	public static void markInstanceDirty() {
		if(INSTANCE != null) {
			INSTANCE.setDirty();
		}
	}

	public static void setInstance(RPMSaveData in) {
		INSTANCE = in;
	}
}
