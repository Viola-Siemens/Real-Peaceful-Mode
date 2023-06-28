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

	public static final RegistryObject<CreativeModeTab> BUILDING_BLOCKS = register(
			"building_blocks", Component.translatable("itemGroup.real_peaceful_mode.building_blocks"), () -> new ItemStack(RPMBlocks.OreBlocks.MANGANESE_BLOCK),
			(parameters, output) -> RPMItems.ItemEntry.BUILDING_BLOCKS.forEach(output::accept)
	);
	public static final RegistryObject<CreativeModeTab> MATERIAL_AND_FOODS = register(
			"material_and_foods", Component.translatable("itemGroup.real_peaceful_mode.material_and_foods"), () -> new ItemStack(RPMItems.SpiritBeads.HUGE_SPIRIT_BEAD),
			(parameters, output) -> RPMItems.ItemEntry.MATERIAL_AND_FOODS.forEach(output::accept)
	);
	/*
	public static final RegistryObject<CreativeModeTab> CREATIVE_ONLY = register(
			"creative_only", Component.translatable("itemGroup.real_peaceful_mode.creative_only"), () -> new ItemStack(),
			(parameters, output) -> RPMItems.ItemEntry.CREATIVE_ONLY.forEach(output::accept)
	);
	 */

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}

	private static RegistryObject<CreativeModeTab> register(String name, Component title, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator generator) {
		return REGISTER.register(name, () -> CreativeModeTab.builder().title(title).icon(icon).displayItems(generator).build());
	}
}
