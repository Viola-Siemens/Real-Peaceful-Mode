package com.hexagram2021.real_peaceful_mode.common.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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

	//Entity
	RandomSource getRandom();
	Level level();
	double getRandomX(double xr);
	double getRandomY();
	double getRandomZ(double zr);

	default void addLessParticlesAroundSelf(ParticleOptions particleOptions) {
		for(int i = 0; i < 4; ++i) {
			double d0 = this.getRandom().nextGaussian() * 0.02D;
			double d1 = this.getRandom().nextGaussian() * 0.02D;
			double d2 = this.getRandom().nextGaussian() * 0.02D;
			this.level().addParticle(particleOptions, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
		}
	}

	interface Data {
		EntityDataAccessor<Boolean> DATA_ZOMBIE_DANCE = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
	}
}
