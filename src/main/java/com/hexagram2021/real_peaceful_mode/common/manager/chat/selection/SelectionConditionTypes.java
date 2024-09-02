package com.hexagram2021.real_peaceful_mode.common.manager.chat.selection;

import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class SelectionConditionTypes {
	public static final ISelectionConditionType MISSION = register("mission", () -> ISelectionCondition.MissionSelectionCondition.CODEC);
	public static final ISelectionConditionType GREETING_TIME = register("greeting_timed", () -> ISelectionCondition.GreetingTimedSelectionCondition.CODEC);
	public static final ISelectionConditionType MATERIAL_COLLECTION = register("material_collection", () -> ISelectionCondition.MaterialCollectionSelectionCondition.CODEC);

	public static void init() {
	}

	private static ISelectionConditionType register(String name, ISelectionConditionType type) {
		ResourceLocation id = new ResourceLocation(MODID, name);
		ISelectionConditionType.registerConditionType(id, type);
		return type;
	}

	private SelectionConditionTypes() {
	}
}
