package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.crafting.menu.ChatMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.crafting.menu.CultureTableMenu;
import com.hexagram2021.real_peaceful_mode.common.crafting.menu.MissionMessageMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMMenuTypes {
	private static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, MODID);

	public static final DeferredHolder<MenuType<?>, MenuType<MissionMessageMenu>> MISSION_MESSAGE_MENU = REGISTER.register("mission_message", () -> new MenuType<>(MissionMessageMenu::new, FeatureFlags.VANILLA_SET));
	public static final DeferredHolder<MenuType<?>, MenuType<ChatMessageMenu>> CHAT_MESSAGE_MENU = REGISTER.register("chat_message", () -> new MenuType<>(ChatMessageMenu::new, FeatureFlags.VANILLA_SET));
	public static final DeferredHolder<MenuType<?>, MenuType<CultureTableMenu>> CULTURE_TABLE_MENU = REGISTER.register("culture_table", () -> new MenuType<>(CultureTableMenu::new, FeatureFlags.VANILLA_SET));

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
