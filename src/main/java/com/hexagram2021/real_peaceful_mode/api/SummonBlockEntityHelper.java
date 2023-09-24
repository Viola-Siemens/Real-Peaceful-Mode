package com.hexagram2021.real_peaceful_mode.api;

import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.function.BiFunction;

@SuppressWarnings("JavadocReference")
public class SummonBlockEntityHelper {
	/**
	 * API for adding custom summon block extra condition. If it does not meet the 'extra_condition', the summon block will not be triggered.
	 *
	 * @see com.hexagram2021.real_peaceful_mode.common.RPMContent#registerSummonBlockExtraConditions
	 *
	 * @param conditionName			Name of the condition.
	 * @param condition				Check the extra condition. Return 'true' to trigger, while 'false' not to triggered.
	 *
	 */
	public static void registerExtraCondition(ResourceLocation conditionName, BiFunction<ServerLevel, BlockPos, Boolean> condition) {
		SummonBlockEntity.registerExtraCondition(conditionName.toString(), condition);
	}
}
