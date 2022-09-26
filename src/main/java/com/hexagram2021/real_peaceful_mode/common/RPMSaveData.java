package com.hexagram2021.real_peaceful_mode.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class RPMSaveData extends SavedData {
	private static RPMSaveData INSTANCE;
	public static final String dataName = "RealPeacefulMode-SaveData";

	public RPMSaveData()
	{
		super();
	}

	public RPMSaveData(CompoundTag nbt) {
		this();
	}

	@NotNull
	@Override
	public CompoundTag save(@NotNull CompoundTag nbt) {
		ListTag dimensionList = new ListTag();

		//nbt.put("", dimensionList);


		ListTag receivedShaderList = new ListTag();
		//nbt.put("", receivedShaderList);

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
