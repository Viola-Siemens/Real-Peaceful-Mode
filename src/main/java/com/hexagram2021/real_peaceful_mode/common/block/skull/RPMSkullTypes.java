package com.hexagram2021.real_peaceful_mode.common.block.skull;

import net.minecraft.world.level.block.SkullBlock;

public enum RPMSkullTypes implements SkullBlock.Type {
	DARK_ZOMBIE_KNIGHT("real_peaceful_mode:dark_zombie_knight");

	private final String name;

	RPMSkullTypes(String name) {
		this.name = name;
		TYPES.put(name, this);
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
