package com.hexagram2021.real_peaceful_mode.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.mission.IMissionCriterionTrigger;
import com.hexagram2021.real_peaceful_mode.common.mission.PlayerMissions;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(SimpleCriterionTrigger.class)
public class SimpleCriterionTriggerMixin<T extends AbstractCriterionTriggerInstance> implements IMissionCriterionTrigger<T> {
	private final Map<PlayerMissions, Set<Listener<T>>> players = Maps.newIdentityHashMap();

	@Override
	public void addPlayerListener(PlayerMissions playerMissions, Listener<T> listener) {
		this.players.computeIfAbsent(playerMissions, ignored -> Sets.newHashSet()).add(listener);
	}

	@Override
	public void removePlayerListener(PlayerMissions playerMissions, Listener<T> listener) {
		Set<Listener<T>> set = this.players.get(playerMissions);
		if (set != null) {
			set.remove(listener);
			if (set.isEmpty()) {
				this.players.remove(playerMissions);
			}
		}
	}

	@Override
	public void removePlayerListeners(PlayerMissions playerMissions) {
		this.players.remove(playerMissions);
	}

	@Inject(method = "trigger", at = @At(value = "TAIL"))
	protected void triggerMissions(ServerPlayer player, Predicate<T> predicate, CallbackInfo ci) {
		if(player instanceof IMonsterHero hero) {
			PlayerMissions playerMissions = hero.gerPlayerMissions();
			Set<Listener<T>> set = this.players.get(playerMissions);
			if (set != null && !set.isEmpty()) {
				LootContext lootcontext = EntityPredicate.createContext(player, player);
				List<Listener<T>> list = null;

				for(Listener<T> listener : set) {
					T t = listener.getTriggerInstance();
					if (predicate.test(t) && t.getPlayerPredicate().matches(lootcontext)) {
						if (list == null) {
							list = Lists.newArrayList();
						}

						list.add(listener);
					}
				}

				if (list != null) {
					for(Listener<T> listener1 : list) {
						listener1.run(playerMissions);
					}
				}
			}
		}
	}
}
