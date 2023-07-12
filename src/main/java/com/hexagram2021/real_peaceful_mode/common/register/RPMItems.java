package com.hexagram2021.real_peaceful_mode.common.register;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.item.SkeletonScepterItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("unused")
public class RPMItems {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static class SpiritBeads {
		public static final ItemEntry<Item> HUGE_SPIRIT_BEAD = ItemEntry.register(
				"huge_spirit_bead", () -> new Item(new Item.Properties()) {
					@Override
					public boolean isFoil(@NotNull ItemStack itemStack) {
						return true;
					}
				}
		);

		public static final ItemEntry<Item> ZOMBIE_SPIRIT_BEAD = ItemEntry.register("zombie_spirit_bead", () -> new Item(new Item.Properties()));
		public static final ItemEntry<Item> SKELETON_SPIRIT_BEAD = ItemEntry.register("skeleton_spirit_bead", () -> new Item(new Item.Properties()));
		public static final ItemEntry<Item> CREEPER_SPIRIT_BEAD = ItemEntry.register("creeper_spirit_bead", () -> new Item(new Item.Properties()));

		private SpiritBeads() {}

		public static void init() {}
	}

	public static class Materials {
		public static ItemEntry<Item> CRYSTAL_SKULL = ItemEntry.register(
				"crystal_skull", () -> new Item(new Item.Properties().stacksTo(1))
		);

		public static ItemEntry<Item> TUFF_BRICK = ItemEntry.register(
			"tuff_brick", () -> new Item(new Item.Properties().stacksTo(64))
		);

		private Materials() {}

		public static void init() {}
	}

	public static class Weapons {
		public static ItemEntry<SkeletonScepterItem> SKELETON_SCEPTER = ItemEntry.register(
				"skeleton_scepter", () -> new SkeletonScepterItem(new Item.Properties().stacksTo(1))
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

		private Weapons() {}

		public static void init() {}
	}

	public static class DebugItems {
		public static ItemEntry<Item> ZOMBIES_WISH = ItemEntry.register(
				"zombies_wish", () -> new Item(new Item.Properties()) {
					@Override @Nonnull
					public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
						ItemStack itemstack = player.getItemInHand(hand);
						if(hand == InteractionHand.MAIN_HAND && player.getAbilities().instabuild && player instanceof IMonsterHero hero) {
							hero.setHero(EntityType.ZOMBIE);
							player.sendSystemMessage(Component.translatable("message.real_peaceful_mode.zombies_wish.success"));
							return InteractionResultHolder.consume(itemstack);
						}
						return InteractionResultHolder.pass(itemstack);
					}
				}
		);
		public static ItemEntry<Item> SKELETONS_WISH = ItemEntry.register(
				"skeletons_wish", () -> new Item(new Item.Properties()) {
					@Override @Nonnull
					public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
						ItemStack itemstack = player.getItemInHand(hand);
						if(hand == InteractionHand.MAIN_HAND && player.getAbilities().instabuild && player instanceof IMonsterHero hero) {
							hero.setHero(EntityType.SKELETON);
							player.sendSystemMessage(Component.translatable("message.real_peaceful_mode.skeletons_wish.success"));
							return InteractionResultHolder.consume(itemstack);
						}
						return InteractionResultHolder.pass(itemstack);
					}
				}
		);
		public static ItemEntry<Item> CREEPERS_WISH = ItemEntry.register(
				"creepers_wish", () -> new Item(new Item.Properties()) {
					@Override @Nonnull
					public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
						ItemStack itemstack = player.getItemInHand(hand);
						if(hand == InteractionHand.MAIN_HAND && player.getAbilities().instabuild && player instanceof IMonsterHero hero) {
							hero.setHero(EntityType.CREEPER);
							player.sendSystemMessage(Component.translatable("message.real_peaceful_mode.creepers_wish.success"));
							return InteractionResultHolder.consume(itemstack);
						}
						return InteractionResultHolder.pass(itemstack);
					}
				}
		);

		public static ItemEntry<SpawnEggItem> DARK_ZOMBIE_KNIGHT_SPAWN_EGG = ItemEntry.register(
				"dark_zombie_knight_spawn_egg", () -> new ForgeSpawnEggItem(
						() -> RPMEntities.DARK_ZOMBIE_KNIGHT, 0x084616, 0x3cbc12, new Item.Properties()
				)
		);

		private DebugItems() {}

		public static void init() {}
	}

	private RPMItems() {}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		SpiritBeads.init();
		Materials.init();
		Weapons.init();
		DebugItems.init();
	}

	public static class ItemEntry<T extends Item> implements Supplier<T>, ItemLike {

		public static final List<ItemEntry<? extends Item>> ALL_ITEMS = Lists.newArrayList();

		private final RegistryObject<T> regObject;

		public static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make) {
			return new ItemEntry<>(REGISTER.register(name, make));
		}

		private ItemEntry(RegistryObject<T> regObject) {
			this.regObject = regObject;
			ALL_ITEMS.add(this);
		}

		@Override
		@Nonnull
		public T get()
		{
			return this.regObject.get();
		}

		@Nonnull
		@Override
		public Item asItem()
		{
			return this.regObject.get();
		}

		public ResourceLocation getId()
		{
			return this.regObject.getId();
		}
	}
}
