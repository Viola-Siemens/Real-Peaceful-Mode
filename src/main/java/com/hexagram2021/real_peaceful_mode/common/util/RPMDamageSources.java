package com.hexagram2021.real_peaceful_mode.common.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMDamageSources {
	private static final ResourceKey<DamageType> PIKE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "pike_hidden"));

	public static DamageSource pike(LivingEntity victim) {
		return victim.damageSources().source(PIKE);
	}
}
