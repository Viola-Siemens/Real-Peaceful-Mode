package com.hexagram2021.real_peaceful_mode.common.spawner;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.common.block.entity.SummonBlockEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import com.hexagram2021.real_peaceful_mode.common.entity.IFriendlyMonster;
import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class ZombieEventSpawner extends AbstractEventSpawner<Zombie> {
	private static final ResourceLocation ZOMBIE_ROBBERY_MISSION = new ResourceLocation(MODID, "events/zombie_robbery");
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
						ItemStack currentHelmet = zombie.getItemBySlot(EquipmentSlot.HEAD);
						if(itemStack.is(Tags.Items.ARMORS_HELMETS) && itemStack.getMaxDamage() - itemStack.getDamageValue() > currentHelmet.getMaxDamage() - currentHelmet.getDamageValue()) {
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
					monster.setNpcExtraTickAction(MOB_SWEAT);
				}
				ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
				helmet.setDamageValue(20);
				zombie.setItemSlot(EquipmentSlot.HEAD, helmet);
				level.addFreshEntity(zombie);
				return true;
			}),
			new Tuple<>(ZOMBIE_ROBBERY_MISSION, (level, blockPos, yRot) -> {
				Zombie zombie = EntityType.ZOMBIE.create(level);
				if(zombie == null) {
					return false;
				}
				DarkZombieKnight darkZombieKnight = RPMEntities.DARK_ZOMBIE_KNIGHT.create(level);
				if(darkZombieKnight == null) {
					zombie.discard();
					return false;
				}
				ZombieHorse zombieHorse = EntityType.ZOMBIE_HORSE.create(level);
				if(zombieHorse == null) {
					zombie.discard();
					darkZombieKnight.discard();
					return false;
				}
				zombie.setYRot(yRot);
				zombie.moveTo(blockPos.getCenter());
				zombie.setBaby(false);
				darkZombieKnight.setYRot(360.0F - yRot);
				darkZombieKnight.moveTo(blockPos.getCenter());
				darkZombieKnight.setBuster(false);
				zombieHorse.setYRot(360.0F - yRot);
				zombieHorse.moveTo(blockPos.getCenter());
				zombieHorse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(zombieHorse.getRandom().nextDouble() * 0.1D + 0.25D);
				darkZombieKnight.startRiding(zombieHorse);
				darkZombieKnight.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(RPMItems.Weapons.GOLDEN_PIKE));

				ItemStack money = new ItemStack(Items.BUNDLE);
				CompoundTag nbt = money.getOrCreateTag();
				if(!nbt.contains("Items", Tag.TAG_LIST)) {
					nbt.put("Items", new ListTag());
				}
				ListTag listTag = nbt.getList("Items", Tag.TAG_COMPOUND);
				CompoundTag gold = new CompoundTag();
				CompoundTag iron = new CompoundTag();
				CompoundTag rottenFlesh = new CompoundTag();
				new ItemStack(Items.GOLD_INGOT, level.getRandom().nextInt(7, 11)).save(gold);
				new ItemStack(Items.IRON_INGOT, level.getRandom().nextInt(1, 5)).save(iron);
				new ItemStack(Items.ROTTEN_FLESH, level.getRandom().nextInt(2, 12)).save(rottenFlesh);
				listTag.add(gold);
				listTag.add(iron);
				listTag.add(rottenFlesh);
				nbt.put("Items", listTag);
				money.setTag(nbt);
				darkZombieKnight.setItemSlot(EquipmentSlot.OFFHAND, money.copy());
				darkZombieKnight.setDropChance(EquipmentSlot.OFFHAND, 2.0F);

				if(zombie instanceof IFriendlyMonster monster) {
					monster.setRandomEventNpcAction((player, itemStack) -> {
						if(itemStack.isEmpty()) {
							MissionHelper.triggerMissionForPlayer(
									ZOMBIE_ROBBERY_MISSION, SummonBlockEntity.SummonMissionType.RECEIVE, player,
									zombie, player1 -> {}
							);
							return true;
						}
						if(ItemStack.isSameItemSameTags(itemStack, money)) {
							MissionHelper.triggerMissionForPlayer(
									ZOMBIE_ROBBERY_MISSION, SummonBlockEntity.SummonMissionType.FINISH, player,
									zombie, player1 -> {
										zombie.setItemSlot(EquipmentSlot.MAINHAND, itemStack.copy());
										itemStack.shrink(1);
										monster.setRandomEventNpcAction(null);
										monster.setNpcExtraTickAction(null);
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
				zombie.setItemSlot(EquipmentSlot.HEAD, helmet);
				darkZombieKnight.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
				darkZombieKnight.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
				level.addFreshEntity(zombie);
				level.addFreshEntity(zombieHorse);
				level.addFreshEntity(darkZombieKnight);
				return true;
			})
	);

	private static final List<Either<Item, TagKey<Item>>> INTERACT_ITEMS = Lists.newArrayList(
			Either.left(Items.AIR),
			Either.right(Tags.Items.ARMORS_HELMETS),
			Either.left(Items.BUNDLE)
	);

	/**
	 *
	 * @param id				Mission ID
	 * @param consumer			What to do when a random event is happening (for example, spawn mob, destroy blocks).
	 * @param either			The item will be used to interact with zombies.
	 */
	public static void addRandomEvent(ResourceLocation id, Function3<ServerLevel, BlockPos, Float, Boolean> consumer, Either<Item, TagKey<Item>> either) {
		MISSIONS.add(new Tuple<>(id, consumer));
		INTERACT_ITEMS.add(either);
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
	public EntityType<Zombie> getMonsterType() {
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

	@Override
	public boolean isInteractItem(Holder<Item> item) {
		return INTERACT_ITEMS.stream().anyMatch(either -> either.map(
				eitherItem -> item.value().equals(eitherItem),
				item::is
		));
	}
}
