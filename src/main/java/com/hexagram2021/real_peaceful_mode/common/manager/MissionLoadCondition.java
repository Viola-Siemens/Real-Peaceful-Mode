package com.hexagram2021.real_peaceful_mode.common.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.fml.ModList;

import java.util.Objects;

public record MissionLoadCondition(ConditionType type, String value, boolean present) {
	private static final String REGISTRY_NAME_MATCHER = "([a-z0-9_.-]+:[a-z0-9_/.-]+)";

	public MissionLoadCondition(ConditionType type, String value) {
		this(type, value, true);
	}

	public MissionLoadCondition(ConditionType type, String value, boolean present) {
		this.type = type;
		this.value = value;
		this.present = present;

		if(this.type == ConditionType.MOD_LOAD) {
			if(!ResourceLocation.isValidNamespace(this.value)) {
				throw new IllegalArgumentException("Not a valid modid for condition value: \"%s\"!".formatted(this.value));
			}
		} else {
			if(!this.value.matches(REGISTRY_NAME_MATCHER)) {
				throw new IllegalArgumentException("Not a valid resource location for condition value: \"%s\"!".formatted(this.value));
			}
		}
	}

	public boolean test() {
		return switch (this.type) {
			case MOD_LOAD -> ModList.get().isLoaded(this.value);
			case ENTITY_REGISTERED -> BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse(this.value));
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MissionLoadCondition that = (MissionLoadCondition) o;
		return this.present == that.present && this.type == that.type && this.value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.value, this.present);
	}

	public static MissionLoadCondition fromJson(JsonElement json) {
		if(json.isJsonObject()) {
			JsonObject jsonObject = json.getAsJsonObject();
			return new MissionLoadCondition(getConditionType(jsonObject), getValue(jsonObject, "value", "id"), GsonHelper.getAsBoolean(jsonObject, "present", true));
		}
		if(json.isJsonPrimitive()) {
			JsonPrimitive primitive = json.getAsJsonPrimitive();
			if(primitive.isString()) {
				return new MissionLoadCondition(ConditionType.MOD_LOAD, primitive.getAsString());
			}
		}
		throw new IllegalArgumentException("Field \"condition\" is not a json object or a string!");
	}

	private static ConditionType getConditionType(JsonObject json) {
		String type = GsonHelper.getAsString(json, "condition_type", "mod_load");
		return switch (type) {
			case "mod", "mod_load" -> ConditionType.MOD_LOAD;
			case "entity", "entity_registered" -> ConditionType.ENTITY_REGISTERED;
			default -> throw new IllegalArgumentException("Unknown condition type value: %s!".formatted(type));
		};
	}

	private static String getValue(JsonObject json, String... possibleKeys) {
		for(String key: possibleKeys) {
			if(json.has(key)) {
				return json.get(key).getAsString();
			}
		}
		throw new IllegalArgumentException("Field \"value\" is not present!");
	}

	public enum ConditionType {
		MOD_LOAD,
		ENTITY_REGISTERED
	}

	private static final String CONDITIONS_FIELD = "conditions";
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean processConditions(JsonObject json) {
		return !json.has(CONDITIONS_FIELD) || MissionLoadCondition.fromJson(json.get(CONDITIONS_FIELD)).test();
	}
}
