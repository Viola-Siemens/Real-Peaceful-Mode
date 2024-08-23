package com.hexagram2021.real_peaceful_mode.common.manager;

import com.hexagram2021.real_peaceful_mode.api.MissionType;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.IMissionStack;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.PlayerMissions;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;

public record Former(ResourceLocation missionId, MissionType type) implements IMissionStack {
	public static final Codec<Former> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(Former::missionId),
			MissionType.CODEC.optionalFieldOf("type", MissionType.FINISH).forGetter(Former::type)
	).apply(instance, Former::new));
	public static final Codec<Former> CODEC = Codec.either(DIRECT_CODEC, ResourceLocation.CODEC)
			.xmap(either -> either.map(Function.identity(), missionId -> new Former(missionId, MissionType.FINISH)), Either::left);
	public static final Codec<List<Former>> LIST_CODEC = CODEC.listOf();

	public boolean checkComplete(PlayerMissions playerMissions) {
		if(IMonsterHero.missionDisabled(this.missionId)) {
			return false;
		}
		if(this.type == MissionType.RECEIVE && IMonsterHero.underMission(playerMissions, this.missionId)) {
			return true;
		}
		return IMonsterHero.completeMission(playerMissions, this.missionId);
	}
}
