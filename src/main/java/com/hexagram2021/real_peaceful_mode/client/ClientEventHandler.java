package com.hexagram2021.real_peaceful_mode.client;

import com.hexagram2021.real_peaceful_mode.RealPeacefulMode;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class ClientEventHandler implements ResourceManagerReloadListener {
	@Override
	public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
		RealPeacefulMode.proxy.clearRenderCaches();
	}
}
