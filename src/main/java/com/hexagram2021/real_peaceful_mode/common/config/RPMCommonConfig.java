package com.hexagram2021.real_peaceful_mode.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class RPMCommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.BooleanValue ZOMBIE_RANDOM_EVENT;
	public static final ForgeConfigSpec.BooleanValue SKELETON_RANDOM_EVENT;
	public static final ForgeConfigSpec.BooleanValue CREEPER_RANDOM_EVENT;

	public static final ForgeConfigSpec.IntValue RANDOM_EVENT_CHECKER_INTERVAL;
	public static final ForgeConfigSpec.IntValue RANDOM_EVENT_POSSIBILITY;
	public static final ForgeConfigSpec.IntValue RANDOM_EVENT_POSSIBILITY_ADDER;
	public static final ForgeConfigSpec.IntValue RANDOM_EVENT_POSSIBILITY_MAX;

	private RPMCommonConfig() {}

	static {
		BUILDER.push("real_peaceful_mode-common-config");
			BUILDER.comment("You can determine whether each random event will happen to a hero player or not.");
			BUILDER.push("random-events");
				ZOMBIE_RANDOM_EVENT = BUILDER.define("ZOMBIE_RANDOM_EVENT", true);
				SKELETON_RANDOM_EVENT = BUILDER.define("SKELETON_RANDOM_EVENT", true);
				CREEPER_RANDOM_EVENT = BUILDER.define("CREEPER_RANDOM_EVENT", true);

				RANDOM_EVENT_CHECKER_INTERVAL = BUILDER.comment("How many ticks (20 ticks = 1 second) will delay for a try to spawn a random event").defineInRange("RANDOM_EVENT_CHECKER_INTERVAL", 5000, 1000, 96000);
				RANDOM_EVENT_POSSIBILITY = BUILDER.comment("The possibility (in percentage) for spawning a random event during a try.").defineInRange("RANDOM_EVENT_POSSIBILITY", 25, 0, 100);
				RANDOM_EVENT_POSSIBILITY_ADDER = BUILDER.comment("If last try failed to spawn a random event, how much will the possibility increase next time. If success, it will be back to RANDOM_EVENT_POSSIBILITY.").defineInRange("RANDOM_EVENT_POSSIBILITY_ADDER", 5, 0, 50);
				RANDOM_EVENT_POSSIBILITY_MAX = BUILDER.comment("The max possibility (in percentage) for spawning a random event. It will never be more than this value.").defineInRange("RANDOM_EVENT_POSSIBILITY_MAX", 80, 0, 100);
			BUILDER.pop();
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	public static ForgeConfigSpec getConfig() {
		return SPEC;
	}
}
