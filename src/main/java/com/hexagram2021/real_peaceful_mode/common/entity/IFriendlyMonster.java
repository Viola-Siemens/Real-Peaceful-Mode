package com.hexagram2021.real_peaceful_mode.common.entity;

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

	void setDance(boolean dancing);

	@Nullable
	BiFunction<ServerPlayer, ItemStack, Boolean> getRandomEventNpcAction();

	void setRandomEventNpcAction(@Nullable BiFunction<ServerPlayer, ItemStack, Boolean> action);

	@Nullable
	Consumer<Mob> getNpcExtraTickAction();

	void setNpcExtraTickAction(@Nullable Consumer<Mob> action);

	interface Data {
		EntityDataAccessor<Boolean> DATA_ZOMBIE_DANCE = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
	}
}
