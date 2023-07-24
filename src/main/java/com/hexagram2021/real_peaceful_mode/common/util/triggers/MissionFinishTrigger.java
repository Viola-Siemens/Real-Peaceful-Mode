package com.hexagram2021.real_peaceful_mode.common.util.triggers;

import com.google.gson.JsonObject;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.util.triggers.predicates.MissionPredicate;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class MissionFinishTrigger extends SimpleCriterionTrigger<MissionFinishTrigger.TriggerInstance> {
	static final ResourceLocation ID = new ResourceLocation(MODID, "mission_finish");

	@Override
	protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext context) {
		MissionPredicate missionPredicate = MissionPredicate.fromJson(GsonHelper.getAsJsonObject(json, "mission"));
		return new MissionFinishTrigger.TriggerInstance(predicate, missionPredicate);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	public void trigger(ServerPlayer player, @Nullable EntityType<?> entityType) {
		this.trigger(player, instance -> {
			if(player instanceof IMonsterHero hero) {
				return instance.matches(entityType, hero);
			}
			return false;
		});
	}

	public static class TriggerInstance extends AbstractCriterionTriggerInstance {
		private final MissionPredicate missionPredicate;

		public TriggerInstance(ContextAwarePredicate predicate, MissionPredicate missionPredicate) {
			super(MissionFinishTrigger.ID, predicate);
			this.missionPredicate = missionPredicate;
		}

		public boolean matches(@Nullable EntityType<?> entityType, IMonsterHero hero) {
			return this.missionPredicate.matches(entityType, hero);
		}
	}
}
