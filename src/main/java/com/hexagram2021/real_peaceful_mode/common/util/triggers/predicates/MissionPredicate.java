package com.hexagram2021.real_peaceful_mode.common.util.triggers.predicates;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public class MissionPredicate {
	@Nullable
	private final ResourceLocation entityType;
	@Nullable
	private final String missionNamespace;
	@Nullable
	private final String entityNamespace;
	private final MinMaxBounds.Ints count;
	private final MinMaxBounds.Ints heroCount;

	public MissionPredicate() {
		this.entityType = null;
		this.missionNamespace = null;
		this.entityNamespace = null;
		this.count = MinMaxBounds.Ints.ANY;
		this.heroCount = MinMaxBounds.Ints.ANY;
	}

	public MissionPredicate(@Nullable ResourceLocation entityType, @Nullable String missionNamespace, @Nullable String entityNamespace, MinMaxBounds.Ints count, MinMaxBounds.Ints heroCount) {
		this.entityType = entityType;
		this.missionNamespace = missionNamespace;
		this.entityNamespace = entityNamespace;
		this.count = count;
		this.heroCount = heroCount;
	}

	@SuppressWarnings("ConstantConditions")
	public boolean matches(@Nullable EntityType<?> entityType, IMonsterHero hero) {
		boolean ret = true;
		if(this.entityType != null) {
			ret = ret && entityType != null && getRegistryName(entityType).equals(this.entityType);
		}
		int count = hero.getPlayerMissions().finishedMissions().size();
		int heroCount = hero.getHelpedMonsters().size();
		if(this.missionNamespace != null) {
			count = (int) hero.getPlayerMissions().finishedMissions().stream().filter(id -> id.getNamespace().equals(this.missionNamespace)).count();
		}
		if(this.entityNamespace != null) {
			heroCount = (int) hero.getHelpedMonsters().entrySet().stream().filter(entry -> entry.getKey().getNamespace().equals(this.entityNamespace)).count();
		}
		ret = ret && this.count.matches(count);
		ret = ret && this.heroCount.matches(heroCount);
		return ret;
	}

	public static MissionPredicate fromJson(@Nullable JsonElement json) {
		ResourceLocation entityType = null;
		String missionNamespace = null;
		String entityNamespace = null;
		MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
		MinMaxBounds.Ints heroCount = MinMaxBounds.Ints.ANY;
		if (json != null && !json.isJsonNull() && json instanceof JsonObject jsonObject) {
			if(jsonObject.has("entity_type")) {
				entityType = new ResourceLocation(GsonHelper.convertToString(jsonObject, "entity_type"));
			}
			if(jsonObject.has("mission_namespace")) {
				missionNamespace = GsonHelper.convertToString(jsonObject, "mission_namespace");
			}
			if(jsonObject.has("entity_namespace")) {
				entityNamespace = GsonHelper.convertToString(jsonObject, "entity_namespace");
			}
			if(jsonObject.has("total_count")) {
				count = MinMaxBounds.Ints.fromJson(jsonObject.get("total_count"));
			}
			if(jsonObject.has("hero_count")) {
				heroCount = MinMaxBounds.Ints.fromJson(jsonObject.get("hero_count"));
			}
		}
		return new MissionPredicate(entityType, missionNamespace, entityNamespace, count, heroCount);
	}

	public JsonElement serializeToJson() {
		JsonObject ret = new JsonObject();
		if(this.entityType != null) {
			ret.addProperty("entity_type", this.entityType.toString());
		}
		if(this.missionNamespace != null) {
			ret.addProperty("mission_namespace", this.missionNamespace);
		}
		if(this.entityNamespace != null) {
			ret.addProperty("entity_namespace", this.entityNamespace);
		}
		if(!this.count.isAny()) {
			ret.add("total_count", this.count.serializeToJson());
		}
		if(!this.heroCount.isAny()) {
			ret.add("hero_count", this.heroCount.serializeToJson());
		}
		return ret;
	}
}
