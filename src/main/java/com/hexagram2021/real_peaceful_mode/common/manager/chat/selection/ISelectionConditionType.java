package com.hexagram2021.real_peaceful_mode.common.manager.chat.selection;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

@FunctionalInterface
public interface ISelectionConditionType {
	Map<ResourceLocation, ISelectionConditionType> SELECTION_CONDITION_TYPES = Maps.newHashMap();
	Map<ISelectionConditionType, ResourceLocation> SELECTION_CONDITION_IDS = Maps.newIdentityHashMap();
	static void registerConditionType(ResourceLocation id, ISelectionConditionType conditionType) {
		SELECTION_CONDITION_TYPES.put(id, conditionType);
		SELECTION_CONDITION_IDS.put(conditionType, id);
	}

	Codec<ISelectionConditionType> REGISTRY_CODEC = new Codec<>() {
		@Override
		public <R> DataResult<Pair<ISelectionConditionType, R>> decode(DynamicOps<R> ops, R input) {
			return ResourceLocation.CODEC.decode(ops, input).flatMap(pair -> {
				if (!SELECTION_CONDITION_TYPES.containsKey(pair.getFirst())) {
					return DataResult.error(() -> "Unexpected selection condition type: %s".formatted(pair.getFirst()));
				}
				return DataResult.success(pair.mapFirst(SELECTION_CONDITION_TYPES::get));
			});
		}

		@Override
		public <R> DataResult<R> encode(ISelectionConditionType input, DynamicOps<R> ops, R prefix) {
			ResourceLocation id = SELECTION_CONDITION_IDS.get(input);
			if (id == null) {
				return DataResult.error(() -> "Unknown selection condition type: %s".formatted(input));
			}
			R key = ops.createString(id.toString());
			return ops.mergeToPrimitive(prefix, key);
		}
	};

	MapCodec<? extends ISelectionCondition> codec();
}
