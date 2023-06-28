package com.hexagram2021.real_peaceful_mode.common.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public final class RPMCreativeTabs {
	private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

	public static final RegistryObject<CreativeModeTab> REAL_PEACEFUL_MODE = register(
			"real_peaceful_mode", Component.translatable("itemGroup.real_peaceful_mode"), () -> new ItemStack(RPMItems.SpiritBeads.HUGE_SPIRIT_BEAD),
			(parameters, output) -> RPMItems.ItemEntry.ALL_ITEMS.forEach(output::accept)
	);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	@SuppressWarnings("SameParameterValue")
	private static RegistryObject<CreativeModeTab> register(String name, Component title, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator generator) {
		return REGISTER.register(name, () -> CreativeModeTab.builder().title(title).icon(icon).displayItems(generator).build());
	}
}
