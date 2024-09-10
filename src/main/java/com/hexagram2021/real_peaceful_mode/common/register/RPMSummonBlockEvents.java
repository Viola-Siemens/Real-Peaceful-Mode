package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.api.SummonBlockEntityHelper;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.HuskPharaoh;
import com.hexagram2021.real_peaceful_mode.common.world.structures.pieces.SlimeMazePieces;
import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMSummonBlockEvents {
	public static final ResourceLocation CONDITION_PHARAOH = new ResourceLocation(MODID, "pharaoh");
	public static final ResourceLocation WORK_SLIME_LADDER = new ResourceLocation(MODID, "slime_place_ladder");

	public static void registerSummonBlockExtraConditions() {
		SummonBlockEntityHelper.registerExtraCondition(CONDITION_PHARAOH, (level, pos) -> !HuskPharaoh.conditionToStone(level, pos));
	}
	public static void registerSummonBlockExtraWorks() {
		SummonBlockEntityHelper.registerSummonBlockExtraWork(WORK_SLIME_LADDER, SlimeMazePieces.SlimeMazeHousingPiece::extraWorkAfterTrigger);
	}

	private RPMSummonBlockEvents() {
	}
}
