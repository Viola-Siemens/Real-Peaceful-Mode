package com.hexagram2021.real_peaceful_mode.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SummonBlockEntity extends BlockEntity {
	public static final String TAG_SUMMON_ENTITY = "summon";
	public static final String TAG_SUMMON_ENTITY_ID = "id";
	public static final String TAG_SUMMON_ENTITY_Tag = "tag";

	public SummonBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(RPMBlockEntities.SUMMON_BLOCK, blockPos, blockState);
	}

	//TODO
}
