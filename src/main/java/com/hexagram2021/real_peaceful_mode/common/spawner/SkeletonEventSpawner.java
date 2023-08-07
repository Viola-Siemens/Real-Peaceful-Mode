package com.hexagram2021.real_peaceful_mode.common.spawner;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class SkeletonEventSpawner extends AbstractEventSpawner<Skeleton> {
	private static final ResourceLocation SKELETON_ARM_MISSION = new ResourceLocation(MODID, "events/skeleton_arm");

	private static final List<Tuple<ResourceLocation, Function3<ServerLevel, BlockPos, Float, Boolean>>> MISSIONS = Lists.newArrayList(
			new Tuple<>(SKELETON_ARM_MISSION, (level, blockPos, yRot) -> {
				//TODO
				return false;
			})
	);

	private static final List<Either<Item, TagKey<Item>>> INTERACT_ITEMS = Lists.newArrayList(
			Either.left(Items.AIR)
	);

	/**
	 *
	 * @param id				Mission ID
	 * @param consumer			What to do when a random event is happening (for example, spawn mob, destroy blocks).
	 * @param either			The item will be used to interact with skeletons.
	 */
	public static void addRandomEvent(ResourceLocation id, Function3<ServerLevel, BlockPos, Float, Boolean> consumer, Either<Item, TagKey<Item>> either) {
		MISSIONS.add(new Tuple<>(id, consumer));
		INTERACT_ITEMS.add(either);
	}

	private int index = 0;

	@Override
	protected boolean spawnEventNpc(ServerLevel level, ServerPlayer player) {
		return false;
	}

	@Override
	public EntityType<Skeleton> getMonsterType() {
		return EntityType.SKELETON;
	}

	@Override
	public ResourceKey<Level> dimension() {
		return Level.OVERWORLD;
	}

	@Override
	protected boolean checkSpawnConditions(ServerLevel level, ServerPlayer player) {
		return level.canSeeSky(player.blockPosition());
	}

	@Override
	protected ResourceLocation getMissionId() {
		return MISSIONS.get(this.index).getA();
	}

	@Override
	public boolean isInteractItem(Holder<Item> item) {
		return INTERACT_ITEMS.stream().anyMatch(either -> either.map(
				eitherItem -> item.value().equals(eitherItem),
				item::is
		));
	}
}
