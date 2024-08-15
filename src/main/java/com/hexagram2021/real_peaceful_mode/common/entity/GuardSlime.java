package com.hexagram2021.real_peaceful_mode.common.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class GuardSlime extends Slime {
	public GuardSlime(EntityType<? extends GuardSlime> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void setSize(int size, boolean newSpawn) {
		super.setSize(size, newSpawn);
		int i = Mth.clamp(size, 1, 127);
		Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(i * i * 2);
		Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.25D + 0.1D * i);
	}

	@Override
	protected float getAttackDamage() {
		return super.getAttackDamage() + 1.0F;
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.ARMOR, 4.0D);
	}
}
