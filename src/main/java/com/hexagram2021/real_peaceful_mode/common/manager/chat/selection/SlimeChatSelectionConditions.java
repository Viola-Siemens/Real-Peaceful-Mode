package com.hexagram2021.real_peaceful_mode.common.manager.chat.selection;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;

public interface SlimeChatSelectionConditions {
	record SavedSlimeSelectionCondition() implements ISelectionCondition {
		public static final MapCodec<SavedSlimeSelectionCondition> CODEC = MapCodec.unit(SavedSlimeSelectionCondition::new);

		@Override
		public ISelectionConditionType type() {
			return SelectionConditionTypes.SAVED_SLIME;
		}

		@Override
		public boolean check(ServerPlayer player, LivingEntity npc) {
			return npc instanceof Slime slime && slime.targetSelector.getAvailableGoals().isEmpty();
		}
	}
}
