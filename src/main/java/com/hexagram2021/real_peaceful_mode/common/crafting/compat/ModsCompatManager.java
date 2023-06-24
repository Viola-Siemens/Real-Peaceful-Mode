package com.hexagram2021.real_peaceful_mode.common.crafting.compat;

import net.minecraftforge.fml.ModList;

public class ModsCompatManager {
	public static boolean EMERALD_CRAFT = false;

	public static void compatModLoaded() {
		ModList modList = ModList.get();
		if(modList.isLoaded("emeraldcraft")) {
			EMERALD_CRAFT = true;
		}
	}

	public static void SolveCompat() {

	}
}
