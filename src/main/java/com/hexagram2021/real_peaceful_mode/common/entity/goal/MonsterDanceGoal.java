package com.hexagram2021.real_peaceful_mode.common.entity.goal;

import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class MonsterDanceGoal<T extends Mob & IFriendlyMonster> extends Goal {
	protected final T monster;

	public MonsterDanceGoal(T monster) {
		this.monster = monster;
	}

	@Override
	public boolean canUse() {
		return this.monster.isDancing() && this.monster.getTarget() == null;
	}

	@Override
	public void stop() {
		this.monster.setDance(false);
	}

	@Override
	public void tick() {
		int tickCount = this.monster.tickCount % 30;
		if(tickCount < 2) {
			this.monster.getJumpControl().jump();
		}
	}
}
