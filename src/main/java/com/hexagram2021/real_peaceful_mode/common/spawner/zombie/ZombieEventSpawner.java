package com.hexagram2021.real_peaceful_mode.common.spawner.zombie;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.spawner.AbstractEventSpawner;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.mojang.datafixers.util.Function3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class ZombieEventSpawner extends AbstractEventSpawner<Zombie> {
	private static final ResourceLocation ZOMBIE_HELMET_MISSION = new ResourceLocation(MODID, "events/zombie_helmet");

	private static final List<Tuple<ResourceLocation, Function3<ServerLevel, BlockPos, Float, Boolean>>> MISSIONS = Lists.newArrayList(
			new Tuple<>(ZOMBIE_HELMET_MISSION, (level, blockPos, yRot) -> {
				Zombie zombie = EntityType.ZOMBIE.create(level);
				if(zombie == null) {
					return false;
				}
				zombie.setYRot(yRot);
				zombie.moveTo(blockPos.getCenter());
				zombie.setBaby(true);
				if(zombie instanceof IFriendlyMonster monster) {
					monster.setRandomEventNpcAction((player, itemStack) -> {
						if(itemStack.isEmpty()) {
							MissionHelper.triggerMissionForPlayer(
									ZOMBIE_HELMET_MISSION, SummonBlockEntity.SummonMissionType.RECEIVE, player,
									zombie, player1 -> {}
							);
							return true;
						}
						if(itemStack.is(Tags.Items.ARMORS_HELMETS)) {
							MissionHelper.triggerMissionForPlayer(
									ZOMBIE_HELMET_MISSION, SummonBlockEntity.SummonMissionType.FINISH, player,
									zombie, player1 -> {
										zombie.setItemSlot(EquipmentSlot.HEAD, itemStack.copy());
										itemStack.shrink(1);
										monster.setRandomEventNpcAction(null);
										monster.setNpcExtraTickAction(null);
									}
							);
							return true;
						}
						return false;
					});
					monster.setNpcExtraTickAction(mob -> {
						if(mob.level().isClientSide) {
							double x = mob.getRandom().nextGaussian() * 0.02D;
							double y = mob.getRandom().nextGaussian() * 0.02D;
							double z = mob.getRandom().nextGaussian() * 0.02D;
							mob.level().addParticle(ParticleTypes.SPLASH, mob.getRandomX(1.0D), mob.getRandomY() + 0.5D, mob.getRandomZ(1.0D), x, y, z);
						}
					});
				}
				ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
				helmet.setDamageValue(20);
				zombie.setItemSlot(EquipmentSlot.HEAD, helmet);
				level.addFreshEntity(zombie);
				return true;
			})
	);

	/**
	 *
	 * @param id				Mission ID
	 * @param consumer			What to do when a random event is happening (for example, spawn mob, destroy blocks).
	 */
	public static void addRandomEvent(ResourceLocation id, Function3<ServerLevel, BlockPos, Float, Boolean> consumer) {
		MISSIONS.add(new Tuple<>(id, consumer));
	}

	private int index = 0;

	@Override
	protected boolean spawnEventNpc(ServerLevel level, ServerPlayer player) {
		float angle = player.getYRot() * Mth.PI / 180.0F;
		Vec3 pos = player.position().subtract(-Mth.sin(angle) * 16, 0, Mth.cos(angle) * 16);
		BlockPos blockPos = new BlockPos((int) pos.x, (int) pos.y - 5, (int) pos.z);
		if(level.getBlockState(blockPos).isAir()) {
			return false;
		}
		for(int y = 1; y < 10; ++y) {
			BlockPos tryPos = blockPos.above(y);
			if(level.getBlockState(tryPos).isAir()) {
				RPMLogger.debug("Spawn a new zombie's random event %s at (%d, %d, %d).".formatted(this.getMissionId(), tryPos.getX(), tryPos.getY(), tryPos.getZ()));
				boolean ret = MISSIONS.get(this.index).getB().apply(level, tryPos, player.getYRot());
				this.index = (this.index + 1) % MISSIONS.size();
				return ret;
			}
		}
		return false;
	}

	@Override
	protected EntityType<Zombie> getMonsterType() {
		return EntityType.ZOMBIE;
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
}
