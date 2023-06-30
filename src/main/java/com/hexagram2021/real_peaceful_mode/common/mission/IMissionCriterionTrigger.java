package com.hexagram2021.real_peaceful_mode.common.mission;

import net.minecraft.advancements.CriterionTriggerInstance;

import java.util.Objects;

public interface IMissionCriterionTrigger<T extends CriterionTriggerInstance> {
	void addPlayerListener(PlayerMissions playerMissions, IMissionCriterionTrigger.Listener<T> listener);
	void removePlayerListener(PlayerMissions playerMissions, IMissionCriterionTrigger.Listener<T> listener);
	void removePlayerListeners(PlayerMissions playerMissions);

	class Listener<T extends CriterionTriggerInstance> {
		private final T trigger;
		private final MissionManager.Mission mission;

		public Listener(T trigger, MissionManager.Mission mission) {
			this.trigger = trigger;
			this.mission = mission;
		}

		public T getTriggerInstance() {
			return this.trigger;
		}

		public void run(PlayerMissions missions) {
			missions.award(this.mission);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Listener<?> listener = (Listener<?>) o;
			return this.trigger.equals(listener.trigger) && this.mission.equals(listener.mission);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.trigger, this.mission);
		}
	}
}
