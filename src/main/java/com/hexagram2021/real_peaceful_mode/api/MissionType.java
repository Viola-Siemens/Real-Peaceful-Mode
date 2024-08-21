package com.hexagram2021.real_peaceful_mode.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public enum MissionType implements StringRepresentable {
	RECEIVE,
	FINISH;

	public static final Map<String, MissionType> TYPE_BY_NAME;

	public String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	static {
		ImmutableMap.Builder<String, MissionType> builder = ImmutableMap.builder();
		Arrays.stream(values()).forEach(type -> builder.put(type.getSerializedName(), type));
		TYPE_BY_NAME = builder.build();
	}
}
