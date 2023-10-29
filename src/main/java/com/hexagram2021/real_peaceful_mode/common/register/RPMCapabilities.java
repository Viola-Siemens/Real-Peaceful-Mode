package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.entity.capability.IItemEntityConvertible;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMCapabilities {
	public static final ResourceLocation ID_ITEM_ENTITY_CONVERTIBLE = new ResourceLocation(MODID, "convertible");
	public static final Capability<IItemEntityConvertible> ITEM_ENTITY_CONVERTIBLE = CapabilityManager.get(new CapabilityToken<>(){});

	private RPMCapabilities() {
	}
}
