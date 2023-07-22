package com.hexagram2021.real_peaceful_mode.common.register;

import com.google.common.collect.Lists;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;
import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODNAME;

@OnlyIn(Dist.CLIENT)
public final class RPMKeys {
	public static final KeyEntry MISSION_SCREEN = new KeyEntry("mission_screen", GLFW.GLFW_KEY_M);

	public static final class KeyEntry {
		public static final List<KeyEntry> ALL_KEYS = Lists.newArrayList();

		private final KeyMapping keyMapping;

		@SuppressWarnings("SameParameterValue")
		private KeyEntry(String name, int defaultKey) {
			String descriptionId = MODID + ".keyinfo." + name;
			this.keyMapping = new KeyMapping(descriptionId, defaultKey, MODNAME);
			this.keyMapping.setKeyConflictContext(KeyConflictContext.IN_GAME);

			ALL_KEYS.add(this);
		}

		public boolean isDown() {
			return this.keyMapping.isDown();
		}

		public KeyMapping getKeyMapping() {
			return this.keyMapping;
		}
	}

	public static void init() {

	}
}
