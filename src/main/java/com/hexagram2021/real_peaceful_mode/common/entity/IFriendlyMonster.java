package com.hexagram2021.real_peaceful_mode.common.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface IFriendlyMonster {
	String TAG_DANCING = "RPM_Dancing";

	boolean preventAttack(@Nullable LivingEntity target);

	static boolean preventAttack(Level level, EntityType<?> entityType, @Nullable LivingEntity target) {
		return level.players().stream().anyMatch(player -> {
			if(player instanceof IMonsterHero hero) {
				return hero.isHero(entityType) && (target instanceof AbstractVillager || (target != null && hero.isHero(target.getType())));
			}
			return false;
		});
	}

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

	final class Data {
		public static EntityDataAccessor<Boolean> DATA_ZOMBIE_DANCE;
		public static EntityDataAccessor<Boolean> DATA_SKELETON_DANCE;


		public static EntityDataAccessor<Boolean> DATA_SKELETON_RIGHT_ARM_DETACHED;
	}
}
