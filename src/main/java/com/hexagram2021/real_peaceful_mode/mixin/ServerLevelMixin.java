package com.hexagram2021.real_peaceful_mode.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
	@Shadow @Final @Mutable
	private List<CustomSpawner> customSpawners;

	@Inject(method = "<init>", at = @At(value = "RETURN"))
	private void addRPMEventSpawners(MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess storage,
									 ServerLevelData serverLevelData, ResourceKey<Level> dimension, LevelStem stem,
									 ChunkProgressListener progressListener, boolean debug, long seed, List<CustomSpawner> customSpawners,
									 boolean tickTime, RandomSequences randomSequences, CallbackInfo ci) {
		if(dimension.equals(Level.OVERWORLD)) {
			this.customSpawners = ImmutableList.<CustomSpawner>builder().addAll(this.customSpawners).add().build();
		}
	}
}
