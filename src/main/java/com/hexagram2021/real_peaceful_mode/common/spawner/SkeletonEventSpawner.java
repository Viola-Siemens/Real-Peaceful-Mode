package com.hexagram2021.real_peaceful_mode.common.spawner;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.entity.IRightArmDetachable;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class SkeletonEventSpawner extends AbstractEventSpawner<Skeleton> {
	private static final ResourceLocation SKELETON_ARM_MISSION = new ResourceLocation(MODID, "events/skeleton_arm");

	private static final List<Tuple<ResourceLocation, Function3<ServerLevel, BlockPos, Float, Boolean>>> MISSIONS = Lists.newArrayList(
			new Tuple<>(SKELETON_ARM_MISSION, (level, blockPos, yRot) -> {
				Skeleton skeleton = EntityType.SKELETON.create(level);
				if(skeleton == null) {
					return false;
				}
				Fox fox = EntityType.FOX.create(level);
				if(fox == null) {
					skeleton.discard();
					return false;
				}
				skeleton.setYRot(yRot);
				skeleton.moveTo(blockPos.getCenter());
				if(skeleton instanceof IRightArmDetachable rightArmDetachable) {
					rightArmDetachable.setRightArmDetached(true);
				}
				if(level.getBiome(blockPos).is(Tags.Biomes.IS_SNOWY)) {
					fox.setVariant(Fox.Type.SNOW);
				} else {
					fox.setVariant(Fox.Type.RED);
				}
				fox.setYRot(yRot);
				fox.moveTo(blockPos.getCenter());
				fox.setSitting(false);
				fox.setIsCrouching(false);
				fox.setIsInterested(false);
				fox.setJumping(false);
				fox.setSleeping(true);
				fox.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(RPMItems.DebugItems.SKELETON_ARM.get()));
				fox.getNavigation().stop();
				fox.getMoveControl().setWantedPosition(fox.getX(), fox.getY(), fox.getZ(), 0.0D);
				if(skeleton instanceof IFriendlyMonster monster) {
					monster.setRandomEventNpcAction((player, itemStack) -> {
						if(itemStack.isEmpty()) {
							MissionHelper.triggerMissionForPlayer(
									SKELETON_ARM_MISSION, SummonBlockEntity.SummonMissionType.RECEIVE, player,
									skeleton, player1 -> {}
							);
							return true;
						}
						if(itemStack.is(RPMItems.DebugItems.SKELETON_ARM.get())) {
							MissionHelper.triggerMissionForPlayer(
									SKELETON_ARM_MISSION, SummonBlockEntity.SummonMissionType.FINISH, player,
									skeleton, player1 -> {
										if(skeleton instanceof IRightArmDetachable rightArmDetachable) {
											rightArmDetachable.setRightArmDetached(false);
										}
										monster.setRandomEventNpcAction(null);
										monster.setNpcExtraTickAction(null);
										monster.setDance(true);
									}
							);
							return true;
						}
						return false;
					});
					monster.setNpcExtraTickAction(MOB_SWEAT);
				}
				ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
				if(helmet.getItem() instanceof DyeableLeatherItem dyeable) {
					dyeable.setColor(helmet, 0x4b4a4a);
				}
				skeleton.setItemSlot(EquipmentSlot.HEAD, helmet);
				level.addFreshEntity(skeleton);
				level.addFreshEntity(fox);
				return true;
			})
	);

	private static final List<Either<Item, TagKey<Item>>> INTERACT_ITEMS = Lists.newArrayList(
			Either.left(Items.AIR),
			Either.left(RPMItems.DebugItems.SKELETON_ARM.get())
	);

	/**
	 *
	 * @param id				Mission ID
	 * @param consumer			What to do when a random event is happening (for example, spawn mob, destroy blocks).
	 * @param either			The item will be used to interact with skeletons.
	 */
	@SuppressWarnings("unused")
	public static void addRandomEvent(ResourceLocation id, Function3<ServerLevel, BlockPos, Float, Boolean> consumer, @Nullable Either<Item, TagKey<Item>> either) {
		MISSIONS.add(new Tuple<>(id, consumer));
		if(either != null) {
			INTERACT_ITEMS.add(either);
		}
	}

	private int index = 0;

	@Override
	protected boolean spawnEventNpc(ServerLevel level, ServerPlayer player) {
		return this.spawnEventFor(level, player, MISSIONS.get(this.index).getB(), (tryPos) -> {
			RPMLogger.debug("Spawn a new skeleton's random event %s at (%d, %d, %d).".formatted(this.getMissionId(), tryPos.getX(), tryPos.getY(), tryPos.getZ()));
			this.index = (this.index + 1) % MISSIONS.size();
		});
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

	@Override
	public boolean spawnEventFor(ServerLevel level, ServerPlayer player, ResourceLocation missionId) {
		Optional<Tuple<ResourceLocation, Function3<ServerLevel, BlockPos, Float, Boolean>>> optional =
				MISSIONS.stream().filter(tp -> tp.getA().equals(missionId)).findFirst();
		if(optional.isPresent()) {
			this.spawnEventFor(level, player, optional.get().getB(), tryPos ->
					RPMLogger.debug("Spawn a new skeleton's random event %s at (%d, %d, %d).".formatted(this.getMissionId(), tryPos.getX(), tryPos.getY(), tryPos.getZ())));
			return true;
		}
		return false;
	}
}
