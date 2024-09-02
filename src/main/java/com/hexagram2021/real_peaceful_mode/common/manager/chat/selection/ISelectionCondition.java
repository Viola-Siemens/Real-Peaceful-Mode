package com.hexagram2021.real_peaceful_mode.common.manager.chat.selection;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.Former;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ISelectionCondition {
	Codec<ISelectionCondition> REGISTRY_CODEC = ISelectionConditionType.REGISTRY_CODEC.dispatch(ISelectionCondition::type, ISelectionConditionType::codec);

	ISelectionConditionType type();
	boolean check(ServerPlayer player, LivingEntity npc);

	record MissionSelectionCondition(List<Former> formers) implements ISelectionCondition {
		public static final Codec<MissionSelectionCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Former.LIST_CODEC.fieldOf("requires").forGetter(MissionSelectionCondition::formers)
		).apply(instance, MissionSelectionCondition::new));

		@Override
		public ISelectionConditionType type() {
			return SelectionConditionTypes.MISSION;
		}

		@Override
		public boolean check(ServerPlayer player, LivingEntity npc) {
			if(player instanceof IMonsterHero hero) {
				return this.formers.stream().allMatch(former -> former.checkComplete(hero.rpm$getPlayerMissions()));
			}
			return false;
		}
	}
	record GreetingTimedSelectionCondition(GreetingTime greetingTime) implements ISelectionCondition {
		public static final Codec<GreetingTimedSelectionCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				GreetingTime.CODEC.fieldOf("time").forGetter(GreetingTimedSelectionCondition::greetingTime)
		).apply(instance, GreetingTimedSelectionCondition::new));

		@Override
		public ISelectionConditionType type() {
			return SelectionConditionTypes.GREETING_TIME;
		}

		@Override
		public boolean check(ServerPlayer player, LivingEntity npc) {
			long tickOfDay = player.serverLevel().getDayTime() % SharedConstants.TICKS_PER_GAME_DAY;
			return this.greetingTime.is(tickOfDay);
		}

		public enum GreetingTime implements StringRepresentable {
			MORNING("morning", 0L, 5000L),			//06:00 ~ 11:00
			NOON("noon", 5000L, 8000L),				//11:00 ~ 14:00
			AFTERNOON("afternoon", 8000L, 12000L),	//14:00 ~ 18:00
			EVENING("evening", 12000L, 15000L),		//18:00 ~ 21:00
			NIGHT("night", 15000L, 24000L);			//21:00 ~ 06:00

			public static final Codec<GreetingTime> CODEC = StringRepresentable.fromEnum(GreetingTime::values);
			private static final Map<String, GreetingTime> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(GreetingTime::getSerializedName, Function.identity()));

			private final String name;
			private final long beginTime;
			private final long endTime;

			GreetingTime(String name, long beginTime, long endTime) {
				this.name = name;
				this.beginTime = beginTime;
				this.endTime = endTime;
			}

			@Override
			public String getSerializedName() {
				return this.name;
			}

			public boolean is(long time) {
				if(this.beginTime < this.endTime) {
					return this.beginTime <= time && this.endTime > time;
				}
				return this.beginTime <= time || this.endTime > time;
			}

			public static GreetingTime byName(String name) {
				return BY_NAME.get(name);
			}
		}
	}
	record MaterialCollectionSelectionCondition(ResourceLocation lootTable, boolean finish) implements ISelectionCondition {
		public static final Codec<MaterialCollectionSelectionCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("loot_table").forGetter(MaterialCollectionSelectionCondition::lootTable),
				Codec.BOOL.fieldOf("finish").forGetter(MaterialCollectionSelectionCondition::finish)
		).apply(instance, MaterialCollectionSelectionCondition::new));

		@Override
		public ISelectionConditionType type() {
			return SelectionConditionTypes.MATERIAL_COLLECTION;
		}

		@Override
		public boolean check(ServerPlayer player, LivingEntity npc) {
			if(player instanceof IMonsterHero hero) {
				return this.finish ? hero.rpm$materialCollected(this.lootTable, npc) : hero.rpm$canCollectMaterial(this.lootTable);
			}
			return false;
		}
	}
}
