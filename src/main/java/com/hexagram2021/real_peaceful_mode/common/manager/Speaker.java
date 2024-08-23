package com.hexagram2021.real_peaceful_mode.common.manager;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Speaker implements StringRepresentable {
	PLAYER("player"),
	NPC("npc");

	public static final Codec<Speaker> CODEC = StringRepresentable.fromEnum(Speaker::values);
	private static final Map<String, Speaker> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Speaker::getSerializedName, Function.identity()));

	private final String name;

	Speaker(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public static Speaker byName(String name) {
		return BY_NAME.get(name);
	}
}
