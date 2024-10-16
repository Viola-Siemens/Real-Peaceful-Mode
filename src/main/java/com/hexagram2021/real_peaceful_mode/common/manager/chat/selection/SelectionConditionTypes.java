package com.hexagram2021.real_peaceful_mode.common.manager.chat.selection;

import net.minecraft.resources.ResourceLocation;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class SelectionConditionTypes {
	public static final ISelectionConditionType DUMMY = register("dummy", () -> ISelectionCondition.DummySelectionCondition.CODEC);
	public static final ISelectionConditionType MISSION = register("mission", () -> ISelectionCondition.MissionSelectionCondition.CODEC);
	public static final ISelectionConditionType GREETING_TIME = register("greeting_timed", () -> ISelectionCondition.GreetingTimedSelectionCondition.CODEC);
	public static final ISelectionConditionType MATERIAL_COLLECTION = register("material_collection", () -> ISelectionCondition.MaterialCollectionSelectionCondition.CODEC);
	public static final ISelectionConditionType ITEM_IN_INVENTORY = register("item_in_inventory", () -> ISelectionCondition.ItemInInventorySelectionCondition.CODEC);
	public static final ISelectionConditionType INSIDE_STRUCTURE = register("inside_structure", () -> ISelectionCondition.InsideStructureSelectionCondition.CODEC);
	public static final ISelectionConditionType NAME = register("name", () -> ISelectionCondition.NameSelectionCondition.CODEC);

	public static final ISelectionConditionType SAVED_SLIME = register("slime/saved", () -> SlimeChatSelectionConditions.SavedSlimeSelectionCondition.CODEC);

	public static void init() {
	}

	private static ISelectionConditionType register(String name, ISelectionConditionType type) {
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MODID, name);
		ISelectionConditionType.registerConditionType(id, type);
		return type;
	}

	private SelectionConditionTypes() {
	}
}
