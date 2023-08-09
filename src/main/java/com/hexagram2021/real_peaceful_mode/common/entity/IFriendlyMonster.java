package com.hexagram2021.real_peaceful_mode.common.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface IFriendlyMonster {
	boolean preventAttack(@Nullable LivingEntity target);

	boolean isDancing();
	void setDance(boolean dancing);

	@Nullable
	BiFunction<ServerPlayer, ItemStack, Boolean> getRandomEventNpcAction();

	void setRandomEventNpcAction(@Nullable BiFunction<ServerPlayer, ItemStack, Boolean> action);

	@Nullable
	Consumer<Mob> getNpcExtraTickAction();

	void setNpcExtraTickAction(@Nullable Consumer<Mob> action);

	static void addLessParticlesAroundSelf(LivingEntity mob, ParticleOptions particleOptions) {
		for(int i = 0; i < 4; ++i) {
			double d0 = mob.getRandom().nextGaussian() * 0.02D;
			double d1 = mob.getRandom().nextGaussian() * 0.02D;
			double d2 = mob.getRandom().nextGaussian() * 0.02D;
			mob.level().addParticle(particleOptions, mob.getRandomX(1.0D), mob.getRandomY() + 1.0D, mob.getRandomZ(1.0D), d0, d1, d2);
		}
	}

	interface Data {
		EntityDataAccessor<Boolean> DATA_ZOMBIE_DANCE = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
	}
}
