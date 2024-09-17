package com.hexagram2021.real_peaceful_mode.common.util.triggers;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.util.triggers.predicates.MissionPredicate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

import java.util.Optional;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class MissionFinishTrigger extends SimpleCriterionTrigger<MissionFinishTrigger.TriggerInstance> {
	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "mission_finish");

	@Override
	public Codec<MissionFinishTrigger.TriggerInstance> codec() {
		return MissionFinishTrigger.TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, @Nullable EntityType<?> entityType) {
		this.trigger(player, instance -> {
			if(player instanceof IMonsterHero hero) {
				return instance.matches(entityType, hero);
			}
			return false;
		});
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, MissionPredicate missionPredicate) implements SimpleCriterionTrigger.SimpleInstance {
		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(MissionFinishTrigger.TriggerInstance::player),
				MissionPredicate.CODEC.fieldOf("mission").forGetter(MissionFinishTrigger.TriggerInstance::missionPredicate)
		).apply(instance, TriggerInstance::new));

		public boolean matches(@Nullable EntityType<?> entityType, IMonsterHero hero) {
			return this.missionPredicate.matches(entityType, hero);
		}
	}
}
