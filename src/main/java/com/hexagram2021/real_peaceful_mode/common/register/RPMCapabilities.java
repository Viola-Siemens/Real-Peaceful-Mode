package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.entity.capability.IItemEntityConvertible;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMCapabilities {
	public static final ResourceLocation ID_ITEM_ENTITY_CONVERTIBLE = ResourceLocation.fromNamespaceAndPath(MODID, "convertible");
	public static final EntityCapability<IItemEntityConvertible, Void> ITEM_ENTITY_CONVERTIBLE = EntityCapability.createVoid(ID_ITEM_ENTITY_CONVERTIBLE, IItemEntityConvertible.class);

	private RPMCapabilities() {
	}
}
