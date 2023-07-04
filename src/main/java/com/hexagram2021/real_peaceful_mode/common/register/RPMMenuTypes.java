package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.crafting.menu.MissionMessageMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMMenuTypes {
	private static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

	public static final RegistryObject<MenuType<MissionMessageMenu>> MISSION_MESSAGE_MENU = REGISTER.register("mission_message", () -> new MenuType<>(MissionMessageMenu::new, FeatureFlags.VANILLA_SET));

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
