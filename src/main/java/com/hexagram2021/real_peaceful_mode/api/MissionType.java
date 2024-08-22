package com.hexagram2021.real_peaceful_mode.api;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum MissionType implements StringRepresentable {
	RECEIVE("receive"),
	FINISH("finish");


	public static final Codec<MissionType> CODEC = StringRepresentable.fromEnum(MissionType::values);
	public static final Map<String, MissionType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(MissionType::getSerializedName, Function.identity()));

	private final String name;

	MissionType(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public static MissionType byName(String name) {
		return BY_NAME.get(name);
	}
}
