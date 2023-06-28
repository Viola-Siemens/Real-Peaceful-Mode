package com.hexagram2021.real_peaceful_mode.common.mission;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class MissionManager extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();

	private Map<ResourceLocation, Mission> missionsByName = ImmutableMap.of();

	public MissionManager() {
		super(GSON, "missions");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> missions, ResourceManager resourceManager, ProfilerFiller profilerFiller) {

	}

	public static class Mission {
		private Mission[] ins;
		private Mission[] outs;
		private final ResourceLocation advancementId;

		public Mission(ResourceLocation advancementId) {
			this.advancementId = advancementId;
		}
	}
}
