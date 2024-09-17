package com.hexagram2021.real_peaceful_mode.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class RPMCommonConfig {
	private static final String REGISTRY_NAME_MATCHER = "([a-z0-9_.-]+:[a-z0-9_/.-]+)";

	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	private static final ModConfigSpec SPEC;

	public static final ModConfigSpec.ConfigValue<List<? extends String>> DISABLE_EVENTS;

	public static final ModConfigSpec.IntValue RANDOM_EVENT_CHECKER_INTERVAL;
	public static final ModConfigSpec.IntValue RANDOM_EVENT_POSSIBILITY;
	public static final ModConfigSpec.IntValue RANDOM_EVENT_POSSIBILITY_ADDER;
	public static final ModConfigSpec.IntValue RANDOM_EVENT_POSSIBILITY_MAX;

	public static final ModConfigSpec.ConfigValue<List<? extends String>> DISABLE_MISSIONS;

	public static final ModConfigSpec.ConfigValue<List<? extends String>> DISABLE_CHATS;

	public static final ModConfigSpec.BooleanValue ENABLE_JEI_SHADOW_RECIPE;

	private RPMCommonConfig() {}

	static {
		BUILDER.push("real_peaceful_mode-common-config");
		BUILDER.comment("You can determine whether each mission will be triggered or not.");
			BUILDER.push("missions");
				DISABLE_MISSIONS = BUILDER.comment("ID of missions to disable. Disabled missions cannot be received or finished by players. If you disable the last mission of a monster's quest, this monster will never become passive. And if you disable mission A required by mission B, B can be triggered without finishing mission A.")
						.defineListAllowEmpty("DISABLE_MISSIONS", List.of(), () -> "real_peaceful_mode:dummy", o -> o instanceof String str && str.matches(REGISTRY_NAME_MATCHER));
			BUILDER.pop();

			BUILDER.comment("You can determine whether each random event will happen to a hero player or not.");
			BUILDER.push("random-events");
				DISABLE_EVENTS = BUILDER.comment("Entity Type ID of monsters to disable. For example, if you add \"minecraft:zombie\" to this list, no random events will trigger after you complete zombies' quests.")
						.defineListAllowEmpty("DISABLE_EVENTS", List.of(), () -> "real_peaceful_mode:dummy", o -> o instanceof String str && str.matches(REGISTRY_NAME_MATCHER));

				RANDOM_EVENT_CHECKER_INTERVAL = BUILDER.comment("How many ticks (20 ticks = 1 second) will delay for a try to spawn a random event").defineInRange("RANDOM_EVENT_CHECKER_INTERVAL", 20000, 1200, 192000);
				RANDOM_EVENT_POSSIBILITY = BUILDER.comment("The possibility (in percentage) for spawning a random event during a try.").defineInRange("RANDOM_EVENT_POSSIBILITY", 25, 0, 100);
				RANDOM_EVENT_POSSIBILITY_ADDER = BUILDER.comment("If last try failed to spawn a random event, how much will the possibility increase next time. If success, it will be back to RANDOM_EVENT_POSSIBILITY.").defineInRange("RANDOM_EVENT_POSSIBILITY_ADDER", 5, 0, 50);
				RANDOM_EVENT_POSSIBILITY_MAX = BUILDER.comment("The max possibility (in percentage) for spawning a random event. It will never be more than this value.").defineInRange("RANDOM_EVENT_POSSIBILITY_MAX", 80, 0, 100);
			BUILDER.pop();

			BUILDER.comment("You can determine whether each chat will happen to a hero player or not.");
			BUILDER.push("chats");
				DISABLE_CHATS = BUILDER.comment("Entity Type ID of monsters to disable. For example, if you add \"real_peaceful_mode:pink_creeper\" to this list, no chats will trigger when you interact with pink creepers.")
						.defineListAllowEmpty("DISABLE_CHATS", List.of(), () -> "real_peaceful_mode:dummy", o -> o instanceof String str && str.matches(REGISTRY_NAME_MATCHER));
			BUILDER.pop();

			BUILDER.comment("Compatibilities with other mods.");
			BUILDER.push("compatibilities");
				ENABLE_JEI_SHADOW_RECIPE = BUILDER.comment("If disabled, players cannot use JEI to look over shadow recipes, but it might be faster for starting the server.")
						.define("ENABLE_JEI_SHADOW_RECIPE", true);
			BUILDER.pop();
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	public static ModConfigSpec getConfig() {
		return SPEC;
	}
}
