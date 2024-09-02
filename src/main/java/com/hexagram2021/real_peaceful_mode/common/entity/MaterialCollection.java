package com.hexagram2021.real_peaceful_mode.common.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public record MaterialCollection(ResourceLocation entityType, long endTime) {
	public static final Codec<MaterialCollection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("entity_type").forGetter(MaterialCollection::entityType),
			Codec.LONG.fieldOf("end_time").forGetter(MaterialCollection::endTime)
	).apply(instance, MaterialCollection::new));

	public boolean test(LivingEntity entity) {
		return this.entityType().equals(getRegistryName(entity.getType())) && entity.level().getGameTime() >= this.endTime();
	}
}
