package com.hexagram2021.real_peaceful_mode.server.loots;

import com.google.common.collect.ImmutableMap;
import com.hexagram2021.real_peaceful_mode.common.register.RPMLootFunctionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class ShiftMapDecorationFunction extends LootItemConditionalFunction {
	private final String key;
	private final int xShift;
	private final int zShift;

	public static final MapCodec<ShiftMapDecorationFunction> CODEC = RecordCodecBuilder.mapCodec(
			instance -> commonFields(instance).and(instance.group(
					Codec.STRING.optionalFieldOf("key", "+").forGetter(function -> function.key),
					Codec.INT.fieldOf("x").forGetter(function -> function.xShift),
					Codec.INT.fieldOf("z").forGetter(function -> function.zShift)
			)).apply(instance, ShiftMapDecorationFunction::new)
	);

	protected ShiftMapDecorationFunction(List<LootItemCondition> predicates, String key, int xShift, int zShift) {
		super(predicates);
		this.key = key;
		this.xShift = xShift;
		this.zShift = zShift;
	}

	@Override
	public LootItemFunctionType<ShiftMapDecorationFunction> getType() {
		return RPMLootFunctionTypes.SHIFT_MAP_DECORATION.get();
	}

	@Override
	protected ItemStack run(ItemStack itemStack, LootContext context) {
		MapDecorations mapDecorations = itemStack.get(DataComponents.MAP_DECORATIONS);
		if(mapDecorations != null) {
			ImmutableMap.Builder<String, MapDecorations.Entry> builder = ImmutableMap.builder();
			mapDecorations.decorations().forEach((name, entry) -> {
				if(!name.equals(this.key)) {
					builder.put(name, entry);
				}
			});
			MapDecorations.Entry entry = mapDecorations.decorations().get(this.key);
			if(entry != null) {
				builder.put(this.key, new MapDecorations.Entry(entry.type(), entry.x() + this.xShift, entry.z() + this.zShift, entry.rotation()));
			}
			itemStack.set(DataComponents.MAP_DECORATIONS, new MapDecorations(builder.build()));
		}
		return itemStack;
	}
}
