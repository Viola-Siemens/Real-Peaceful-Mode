package com.hexagram2021.real_peaceful_mode.common.entity.goal;

import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ZombieKnightAttackGoal extends MeleeAttackGoal {
	private final DarkZombieKnight knight;
	private int raiseArmTicks;

	public ZombieKnightAttackGoal(DarkZombieKnight knight) {
		super(knight, 1.25D, false);
		this.knight = knight;
	}

	@Override
	public void start() {
		super.start();
		this.raiseArmTicks = 0;
	}

	@Override
	public void stop() {
		super.stop();
		this.knight.setAggressive(false);
	}

	@Override
	public void tick() {
		super.tick();
		++this.raiseArmTicks;
		this.knight.setAggressive(this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2);
	}
}
