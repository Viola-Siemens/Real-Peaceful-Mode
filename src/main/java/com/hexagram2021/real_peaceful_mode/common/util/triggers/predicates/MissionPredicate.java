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

	public boolean matches(@Nullable EntityType<?> entityType, IMonsterHero hero) {
		if(this.entityType != null && entityType != null && !getRegistryName(entityType).equals(this.entityType)) {
			return false;
		}
		int count = hero.getPlayerMissions().finishedMissions().size();
		int heroCount = hero.getHelpedMonsters().size();
		if(this.missionNamespace != null) {
			count = (int) hero.getPlayerMissions().finishedMissions().stream().filter(id -> id.getNamespace().equals(this.missionNamespace)).count();
		}
		if(this.entityNamespace != null) {
			heroCount = (int) hero.getHelpedMonsters().entrySet().stream().filter(entry -> entry.getKey().getNamespace().equals(this.entityNamespace)).count();
		}
		if(this.count != MinMaxBounds.Ints.ANY && !this.count.matches(count)) {
			return false;
		}
		return this.heroCount == MinMaxBounds.Ints.ANY || this.heroCount.matches(heroCount);
	}

	public static MissionPredicate fromJson(@Nullable JsonObject json) {
		ResourceLocation entityType = null;
		String missionNamespace = null;
		String entityNamespace = null;
		MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
		MinMaxBounds.Ints heroCount = MinMaxBounds.Ints.ANY;
		if (json != null && !json.isJsonNull()) {
			if(json.has("entity_type")) {
				entityType = new ResourceLocation(GsonHelper.convertToString(json, "entity_type"));
			}
			if(json.has("mission_namespace")) {
				missionNamespace = GsonHelper.getAsString(json, "mission_namespace");
			}
			if(json.has("entity_namespace")) {
				entityNamespace = GsonHelper.getAsString(json, "entity_namespace");
			}
			if(json.has("total_count")) {
				count = MinMaxBounds.Ints.fromJson(json.get("total_count"));
			}
			if(json.has("hero_count")) {
				heroCount = MinMaxBounds.Ints.fromJson(json.get("hero_count"));
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
