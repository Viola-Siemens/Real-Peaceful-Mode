package com.hexagram2021.real_peaceful_mode.common.entity;

import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class DarkZombieKnight extends Zombie {
	public DarkZombieKnight(EntityType<? extends DarkZombieKnight> entityType, Level level) {
		super(entityType, level);
	}
	public DarkZombieKnight(Level level) {
		super(RPMEntities.DARK_ZOMBIE_KNIGHT, level);
	}
}
