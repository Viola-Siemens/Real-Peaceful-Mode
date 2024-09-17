package com.hexagram2021.real_peaceful_mode.common.util.triggers.predicates;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import javax.annotation.Nullable;

import java.util.Optional;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public record MissionPredicate(Optional<ResourceLocation> entityType, Optional<String> missionNamespace, Optional<String> entityNamespace,
							   MinMaxBounds.Ints count, MinMaxBounds.Ints heroCount) {
	public static final Codec<MissionPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.optionalFieldOf("entity_type").forGetter(MissionPredicate::entityType),
			Codec.STRING.optionalFieldOf("mission_namespace").forGetter(MissionPredicate::missionNamespace),
			Codec.STRING.optionalFieldOf("entity_namespace").forGetter(MissionPredicate::entityNamespace),
			MinMaxBounds.Ints.CODEC.optionalFieldOf("total_count", MinMaxBounds.Ints.ANY).forGetter(MissionPredicate::count),
			MinMaxBounds.Ints.CODEC.optionalFieldOf("hero_count", MinMaxBounds.Ints.ANY).forGetter(MissionPredicate::heroCount)
	).apply(instance, MissionPredicate::new));

	public MissionPredicate() {
		this(Optional.empty(), Optional.empty(), Optional.empty(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY);
	}

	public boolean matches(@Nullable EntityType<?> entityType, IMonsterHero hero) {
		if(this.entityType.isPresent() && entityType != null && !getRegistryName(entityType).equals(this.entityType.get())) {
			return false;
		}
		int count = hero.rpm$getPlayerMissions().getFinishedMissions().size();
		int heroCount = hero.rpm$getHelpedMonsters().size();
		if(this.missionNamespace.isPresent()) {
			count = (int) hero.rpm$getPlayerMissions().getFinishedMissions().stream().filter(id -> id.getNamespace().equals(this.missionNamespace.get())).count();
		}
		if(this.entityNamespace.isPresent()) {
			heroCount = (int) hero.rpm$getHelpedMonsters().entrySet().stream().filter(entry -> entry.getKey().getNamespace().equals(this.entityNamespace.get())).count();
		}
		if(this.count != MinMaxBounds.Ints.ANY && !this.count.matches(count)) {
			return false;
		}
		return this.heroCount == MinMaxBounds.Ints.ANY || this.heroCount.matches(heroCount);
	}
}
