package com.hexagram2021.real_peaceful_mode.common.register;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.SkeletonSkullEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.TinyFireballEntity;
import com.hexagram2021.real_peaceful_mode.common.item.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("unused")
public class RPMItems {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, MODID);

	private RPMItems() {
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		SpiritBeads.init();
		Materials.init();
		Weapons.init();
		DebugItems.init();
	}

	public static void runLater() {
		SpiritBeads.runLater();
	}

	public static class SpiritBeads {
		public static final ItemEntry<SpiritBeadItem> HUGE_SPIRIT_BEAD = ItemEntry.register(
				"huge_spirit_bead", () -> new SpiritBeadItem(null, new Item.Properties()) {
					@Override
					public boolean isFoil(ItemStack itemStack) {
						return true;
					}
				}
		);

		public static final ItemEntry<ConvertibleSpiritBeadItem> ZOMBIE_SPIRIT_BEAD = ItemEntry.register("zombie_spirit_bead", () -> new ConvertibleSpiritBeadItem(EntityType.ZOMBIE, new Item.Properties()));
		public static final ItemEntry<SpiritBeadItem> SKELETON_SPIRIT_BEAD = ItemEntry.register("skeleton_spirit_bead", () -> new SpiritBeadItem(EntityType.SKELETON, new Item.Properties()));
		public static final ItemEntry<SpiritBeadItem> CREEPER_SPIRIT_BEAD = ItemEntry.register("creeper_spirit_bead", () -> new SpiritBeadItem(EntityType.CREEPER, new Item.Properties()));
		public static final ItemEntry<SpiritBeadItem> SLIME_SPIRIT_BEAD = ItemEntry.register("slime_spirit_bead", () -> new SpiritBeadItem(EntityType.SLIME, new Item.Properties()));
		public static final ItemEntry<SpiritBeadItem> GUARDIAN_SPIRIT_BEAD = ItemEntry.register("guardian_spirit_bead", () -> new SpiritBeadItem(EntityType.GUARDIAN, new Item.Properties()));
		public static final ItemEntry<ConvertibleSpiritBeadItem> HUSK_SPIRIT_BEAD = ItemEntry.register("husk_spirit_bead", () -> new ConvertibleSpiritBeadItem(EntityType.HUSK, new Item.Properties()));
		public static final ItemEntry<ConvertibleSpiritBeadItem> DROWNED_SPIRIT_BEAD = ItemEntry.register("drowned_spirit_bead", () -> new ConvertibleSpiritBeadItem(EntityType.DROWNED, new Item.Properties()));

		private SpiritBeads() {
		}

		public static void init() {
		}

		public static void runLater() {
			FluidType water = NeoForgeMod.WATER_TYPE.value();
			Function<ItemEntity, Boolean> wetCondition = entity -> entity.isEyeInFluidType(water);
			Function<ItemEntity, Boolean> dryCondition = entity -> {
				if(entity.isEyeInFluidType(water)) {
					return false;
				}
				Holder<Biome> biome = entity.level().getBiome(entity.blockPosition());
				return biome.is(Tags.Biomes.IS_HOT) && biome.is(Tags.Biomes.IS_DRY) && entity.level().canSeeSky(entity.blockPosition());
			};

			ZOMBIE_SPIRIT_BEAD.get()
					.addConvertItem(wetCondition, DROWNED_SPIRIT_BEAD)
					.addConvertItem(dryCondition, HUSK_SPIRIT_BEAD);
			HUSK_SPIRIT_BEAD.get().addConvertItem(wetCondition, ZOMBIE_SPIRIT_BEAD);
			DROWNED_SPIRIT_BEAD.get().addConvertItem(dryCondition, ZOMBIE_SPIRIT_BEAD);
		}
	}

	public static class Materials {
		public static final ItemEntry<Item> CRYSTAL_SKULL = ItemEntry.register(
				"crystal_skull", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1))
		);
		public static final ItemEntry<Item> ALUNITE = ItemEntry.register(
				"alunite", () -> new Item(new Item.Properties())
		);
		public static final ItemEntry<Item> PAC = ItemEntry.register(
				"pac", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16))
		);
		public static final ItemEntry<Item> TUFF_BRICK = ItemEntry.register(
				"tuff_brick", () -> new Item(new Item.Properties())
		);
		public static final ItemEntry<Item> EXPERIMENT_FLOWER = ItemEntry.register(
				"experiment_flower", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1))
		);
		public static final ItemEntry<Item> EXPLOSION_BLOCKER = ItemEntry.register(
				"explosion_blocker", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1))
		);
		public static final ItemEntry<Item> SLIME_COLLOID = ItemEntry.register(
				"slime_colloid", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16))
		);

		private Materials() {
		}

		public static void init() {
		}
	}

	public static class Weapons {
		public static final ItemEntry<ScepterItem<SkeletonSkullEntity>> SKELETON_SCEPTER = ItemEntry.register(
				"skeleton_scepter", () -> new ScepterItem<>(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).durability(1396)) {
					@Override
					protected SkeletonSkullEntity createProjectile(Level level, LivingEntity owner, double directionX, double directionY, double directionZ) {
						SkeletonSkullEntity skull = new SkeletonSkullEntity(level, owner, directionX, directionY, directionZ);
						skull.setPos(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
						return skull;
					}

					@Override
					public boolean isValidRepairItem(ItemStack scepter, ItemStack material) {
						return material.is(Items.BONE) || super.isValidRepairItem(scepter, material);
					}
				}
		);
		public static final ItemEntry<ScepterItem<TinyFireballEntity>> PHARAOH_SCEPTER = ItemEntry.register(
				"pharaoh_scepter", () -> new ScepterItem<>(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).durability(1280)) {
					@Override
					protected TinyFireballEntity createProjectile(Level level, LivingEntity owner, double directionX, double directionY, double directionZ) {
						TinyFireballEntity fireball = new TinyFireballEntity(level, owner, directionX, directionY, directionZ);
						fireball.setPos(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
						return fireball;
					}

					@Override
					public boolean isValidRepairItem(ItemStack scepter, ItemStack material) {
						return material.is(RPMBlocks.Decoration.SILTSTONE.asItem()) || super.isValidRepairItem(scepter, material);
					}
				}
		);
		public static final ItemEntry<SlimeScepterItem> SLIME_SCEPTER = ItemEntry.register(
				"slime_scepter", () -> new SlimeScepterItem(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).durability(1280))
		);
		public static final ItemEntry<PikeItem> IRON_PIKE = ItemEntry.register(
				"iron_pike", () -> new PikeItem(Tiers.IRON, new Item.Properties().stacksTo(1).attributes(PikeItem.createAttributes(Tiers.IRON, 4.0F, -3.0F)))
		);
		public static final ItemEntry<PikeItem> GOLDEN_PIKE = ItemEntry.register(
				"golden_pike", () -> new PikeItem(Tiers.GOLD, new Item.Properties().stacksTo(1).attributes(PikeItem.createAttributes(Tiers.GOLD, 4.0F, -3.0F)))
		);
		public static final ItemEntry<PikeItem> DIAMOND_PIKE = ItemEntry.register(
				"diamond_pike", () -> new PikeItem(Tiers.DIAMOND, new Item.Properties().stacksTo(1).attributes(PikeItem.createAttributes(Tiers.DIAMOND, 4.0F, -3.0F)))
		);
		public static final ItemEntry<PikeItem> NETHERITE_PIKE = ItemEntry.register(
				"netherite_pike", () -> new PikeItem(Tiers.NETHERITE, new Item.Properties().stacksTo(1).attributes(PikeItem.createAttributes(Tiers.NETHERITE, 4.0F, -3.0F)))
		);

		public static final ItemEntry<FireChargeItem> TINY_FLAME = ItemEntry.register(
				"tiny_flame", () -> new FireChargeItem(new Item.Properties())
		);
		public static final ItemEntry<FireChargeItem> TINY_SOUL_FLAME = ItemEntry.register(
				"tiny_soul_flame", () -> new FireChargeItem(new Item.Properties())
		);

		private Weapons() {
		}

		public static void init() {
		}
	}

	public static class Records {
		public static final ItemEntry<Item> RECORD_ZOMBIE = ItemEntry.register(
				"record_zombie", () -> new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(RPMJukeboxSongKeys.MUSIC_DISC_ZOMBIE))
		);
		public static final ItemEntry<Item> RECORD_SKELETON = ItemEntry.register(
				"record_skeleton", () -> new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(RPMJukeboxSongKeys.MUSIC_DISC_SKELETON))
		);
		public static final ItemEntry<Item> RECORD_CREEPER = ItemEntry.register(
				"record_creeper", () -> new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(RPMJukeboxSongKeys.MUSIC_DISC_CREEPER))
		);

		private Records() {
		}

		public static void init() {
		}
	}

	public static class DebugItems {
		public static final ItemEntry<Item> ZOMBIES_WISH = ItemEntry.register(
				"zombies_wish", () -> new DebugWishItem(EntityType.ZOMBIE, new Item.Properties())
		);
		public static final ItemEntry<Item> SKELETONS_WISH = ItemEntry.register(
				"skeletons_wish", () -> new DebugWishItem(EntityType.SKELETON, new Item.Properties())
		);
		public static final ItemEntry<Item> CREEPERS_WISH = ItemEntry.register(
				"creepers_wish", () -> new DebugWishItem(EntityType.CREEPER, new Item.Properties())
		);
		public static final ItemEntry<Item> HUSKS_WISH = ItemEntry.register(
				"husks_wish", () -> new DebugWishItem(EntityType.HUSK, new Item.Properties())
		);
		public static final ItemEntry<Item> SLIMES_WISH = ItemEntry.register(
				"slimes_wish", () -> new DebugWishItem(EntityType.SLIME, new Item.Properties())
		);
		public static final ItemEntry<Item> DROWNEDS_WISH = ItemEntry.register(
				"drowneds_wish", () -> new DebugWishItem(EntityType.DROWNED, new Item.Properties())
		);

		public static final ItemEntry<SpawnEggItem> DARK_ZOMBIE_KNIGHT_SPAWN_EGG = ItemEntry.register(
				"dark_zombie_knight_spawn_egg", () -> new DeferredSpawnEggItem(
						() -> RPMEntities.DARK_ZOMBIE_KNIGHT, 0x084616, 0x3cbc12, new Item.Properties()
				)
		);
		public static final ItemEntry<SpawnEggItem> GUARD_SLIME_SPAWN_EGG = ItemEntry.register(
				"guard_slime_spawn_egg", () -> new DeferredSpawnEggItem(
						() -> RPMEntities.GUARD_SLIME, 0x90d4a6, 0xe2e9e4, new Item.Properties()
				)
		);

		public static final ItemEntry<ArmorItem> WHITE_MUSTACHE = ItemEntry.register(
				"white_mustache", () -> new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties()) {
					@Override
					public boolean isDamageable(ItemStack stack) {
						return false;
					}

					@Override
					public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
						return ResourceLocation.fromNamespaceAndPath(MODID, "textures/models/white_mustache.png");
					}
				}
		);

		public static final ItemEntry<Item> SKELETON_ARM = ItemEntry.register(
            "skeleton_arm", () -> new Item(new Item.Properties().stacksTo(1))
		);

		private DebugItems() {
		}

		public static void init() {
		}
	}

	public static class ItemEntry<T extends Item> implements Supplier<T>, ItemLike {

		public static final List<ItemEntry<? extends Item>> ALL_ITEMS = Lists.newArrayList();

		private final DeferredHolder<Item, T> regObject;

		private ItemEntry(DeferredHolder<Item, T> regObject) {
			this.regObject = regObject;
			ALL_ITEMS.add(this);
		}

		public static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make) {
			return new ItemEntry<>(REGISTER.register(name, make));
		}

		@Override
		public T get() {
			return this.regObject.get();
		}

		@Override
		public Item asItem() {
			return this.regObject.get();
		}

		public ResourceLocation getId() {
			return this.regObject.getId();
		}
	}
}
