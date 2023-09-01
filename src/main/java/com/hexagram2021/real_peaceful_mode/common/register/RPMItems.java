package com.hexagram2021.real_peaceful_mode.common.register;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.SkeletonSkullEntity;
import com.hexagram2021.real_peaceful_mode.common.item.DebugWishItem;
import com.hexagram2021.real_peaceful_mode.common.item.ScepterItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("unused")
public class RPMItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private RPMItems() {
    }

    public static void init(IEventBus bus) {
        REGISTER.register(bus);

        SpiritBeads.init();
        Materials.init();
        Weapons.init();
        DebugItems.init();
    }

    public static class SpiritBeads {
        public static final ItemEntry<Item> HUGE_SPIRIT_BEAD = ItemEntry.register(
                "huge_spirit_bead", () -> new Item(new Item.Properties()) {
                    @Override
                    public boolean isFoil(ItemStack itemStack) {
                        return true;
                    }
                }
        );

        public static final ItemEntry<Item> ZOMBIE_SPIRIT_BEAD = ItemEntry.register("zombie_spirit_bead", () -> new Item(new Item.Properties()));
        public static final ItemEntry<Item> SKELETON_SPIRIT_BEAD = ItemEntry.register("skeleton_spirit_bead", () -> new Item(new Item.Properties()));
        public static final ItemEntry<Item> CREEPER_SPIRIT_BEAD = ItemEntry.register("creeper_spirit_bead", () -> new Item(new Item.Properties()));
        public static final ItemEntry<Item> SLIME_SPIRIT_BEAD = ItemEntry.register("slime_spirit_bead", () -> new Item(new Item.Properties()));
        public static final ItemEntry<Item> GUARDIAN_SPIRIT_BEAD = ItemEntry.register("guardian_spirit_bead", () -> new Item(new Item.Properties()));
        public static final ItemEntry<Item> HUSK_SPIRIT_BEAD = ItemEntry.register("husk_spirit_bead", () -> new Item(new Item.Properties()));
        public static final ItemEntry<Item> DROWNED_SPIRIT_BEAD = ItemEntry.register("drowned_spirit_bead", () -> new Item(new Item.Properties()));

        private SpiritBeads() {
        }

        public static void init() {
        }
    }

    public static class Materials {
        public static ItemEntry<Item> CRYSTAL_SKULL = ItemEntry.register(
                "crystal_skull", () -> new Item(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1))
        );
        public static ItemEntry<Item> TUFF_BRICK = ItemEntry.register(
                "tuff_brick", () -> new Item(new Item.Properties())
        );
        public static ItemEntry<Item> EXPERIMENT_FLOWER = ItemEntry.register(
                "experiment_flower", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1))
        );
        public static ItemEntry<Item> EXPLOSION_BLOCKER = ItemEntry.register(
                "explosion_blocker", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1))
        );

        private Materials() {
        }

        public static void init() {
        }
    }

    public static class Weapons {
        public static ItemEntry<ScepterItem<SkeletonSkullEntity>> SKELETON_SCEPTER = ItemEntry.register(
                "skeleton_scepter", () -> new ScepterItem<>(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).durability(1396)) {
                    @Override
                    public SkeletonSkullEntity createProjectile(Level level, LivingEntity owner, double directionX, double directionY, double directionZ) {
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
        public static ItemEntry<ScepterItem<SkeletonSkullEntity>> PHARAOH_SCEPTER = ItemEntry.register(
                "pharaoh_scepter", () -> new ScepterItem<>(new Item.Properties().rarity(Rarity.EPIC).stacksTo(1).durability(1280)) {
                    @Override
                    public SkeletonSkullEntity createProjectile(Level level, LivingEntity owner, double directionX, double directionY, double directionZ) {
                        SkeletonSkullEntity skull = new SkeletonSkullEntity(level, owner, directionX, directionY, directionZ);
                        skull.setPos(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
                        return skull;
                    }

                    @Override
                    public boolean isValidRepairItem(ItemStack scepter, ItemStack material) {
                        return material.is(Items.SANDSTONE) || super.isValidRepairItem(scepter, material);
                    }
                }
        );
        public static ItemEntry<SwordItem> IRON_PIKE = ItemEntry.register(
                "iron_pike", () -> new SwordItem(Tiers.IRON, 4, -3.0F, new Item.Properties().stacksTo(1))
        );
        public static ItemEntry<SwordItem> GOLDEN_PIKE = ItemEntry.register(
                "golden_pike", () -> new SwordItem(Tiers.GOLD, 4, -3.0F, new Item.Properties().stacksTo(1))
        );
        public static ItemEntry<SwordItem> DIAMOND_PIKE = ItemEntry.register(
                "diamond_pike", () -> new SwordItem(Tiers.DIAMOND, 4, -3.0F, new Item.Properties().stacksTo(1))
        );
        public static ItemEntry<SwordItem> NETHERITE_PIKE = ItemEntry.register(
                "netherite_pike", () -> new SwordItem(Tiers.NETHERITE, 4, -3.0F, new Item.Properties().stacksTo(1))
        );

        private Weapons() {
        }

        public static void init() {
        }
    }

    public static class Records {
        public static ItemEntry<RecordItem> RECORD_ZOMBIE = ItemEntry.register(
                "record_zombie", () -> new RecordItem(1, () -> RPMSounds.MUSIC_DISC_ZOMBIE, new Item.Properties().stacksTo(1), 120)
        );
        public static ItemEntry<RecordItem> RECORD_SKELETON = ItemEntry.register(
                "record_skeleton", () -> new RecordItem(2, () -> RPMSounds.MUSIC_DISC_SKELETON, new Item.Properties().stacksTo(1), 120)
        );
        public static ItemEntry<RecordItem> RECORD_CREEPER = ItemEntry.register(
                "record_creeper", () -> new RecordItem(3, () -> RPMSounds.MUSIC_DISC_CREEPER, new Item.Properties().stacksTo(1), 120)
        );

        private Records() {
        }

        public static void init() {
        }
    }

    public static class DebugItems {
        public static ItemEntry<Item> ZOMBIES_WISH = ItemEntry.register(
                "zombies_wish", () -> new DebugWishItem(EntityType.ZOMBIE, new Item.Properties())
        );
        public static ItemEntry<Item> SKELETONS_WISH = ItemEntry.register(
                "skeletons_wish", () -> new DebugWishItem(EntityType.SKELETON, new Item.Properties())
        );
        public static ItemEntry<Item> CREEPERS_WISH = ItemEntry.register(
                "creepers_wish", () -> new DebugWishItem(EntityType.CREEPER, new Item.Properties())
        );

        public static ItemEntry<SpawnEggItem> DARK_ZOMBIE_KNIGHT_SPAWN_EGG = ItemEntry.register(
                "dark_zombie_knight_spawn_egg", () -> new ForgeSpawnEggItem(
                        () -> RPMEntities.DARK_ZOMBIE_KNIGHT, 0x084616, 0x3cbc12, new Item.Properties()
                )
        );

        public static ItemEntry<Item> WHITE_MUSTACHE = ItemEntry.register(
                "white_mustache", () -> new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new Item.Properties()) {
                    @Override
                    public boolean isDamageable(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
                        return MODID+":textures/models/white_mustache.png";
                    }
                }
        );

        private DebugItems() {
        }

        public static void init() {
        }
    }

    public static class ItemEntry<T extends Item> implements Supplier<T>, ItemLike {

        public static final List<ItemEntry<? extends Item>> ALL_ITEMS = Lists.newArrayList();

        private final RegistryObject<T> regObject;

        private ItemEntry(RegistryObject<T> regObject) {
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
