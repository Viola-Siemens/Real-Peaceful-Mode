package com.hexagram2021.real_peaceful_mode.common.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface RegistryHelper {
	static ResourceLocation getRegistryName(Item item) {
		return BuiltInRegistries.ITEM.getKey(item);
	}
	static ResourceLocation getRegistryName(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}
	static ResourceLocation getRegistryName(VillagerProfession profession) {
		return BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession);
	}
	static ResourceLocation getRegistryName(EntityType<?> entityType) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
	}
}
