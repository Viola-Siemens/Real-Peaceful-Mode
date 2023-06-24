package com.hexagram2021.real_peaceful_mode.common.register;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.crafting.compat.ModsCompatManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMItems {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static class RawOreItems {
		public static ItemEntry<Item> RAW_ALUMINUM = ItemEntry.register(
				"raw_aluminum", () -> new Item(new Item.Properties()), ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);
		public static ItemEntry<Item> RAW_SILVER = ItemEntry.register(
				"raw_silver", () -> new Item(new Item.Properties()), ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);
		public static ItemEntry<Item> RAW_MANGANESE = ItemEntry.register(
				"raw_manganese", () -> new Item(new Item.Properties()), ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);

		private RawOreItems() {}

		public static void init() {}
	}

	public static class SpiritBeads {
		public static ItemEntry<Item> HUGE_SPIRIT_BEAD = ItemEntry.register(
				"huge_spirit_bead", () -> new Item(new Item.Properties()) {
					@Override
					public boolean isFoil(@NotNull ItemStack itemStack) {
						return true;
					}
				}, ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);

		private SpiritBeads() {}

		public static void init() {}
	}

	public static class ECCompatItems {
		public static ItemEntry<Item> ALUMINUM_CONCENTRATE = ItemEntry.register(
				"aluminum_concentrate", () -> new Item(new Item.Properties()), ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);
		public static ItemEntry<Item> MELTED_ALUMINUM_BUCKET = ItemEntry.register(
				"melted_aluminum_bucket", () -> new Item(new Item.Properties()), ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);
		public static ItemEntry<Item> SILVER_CONCENTRATE = ItemEntry.register(
				"silver_concentrate", () -> new Item(new Item.Properties()), ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);
		public static ItemEntry<Item> MELTED_SILVER_BUCKET = ItemEntry.register(
				"melted_silver_bucket", () -> new Item(new Item.Properties()), ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);
		public static ItemEntry<Item> MANGANESE_CONCENTRATE = ItemEntry.register(
				"manganese_concentrate", () -> new Item(new Item.Properties()), ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);
		public static ItemEntry<Item> MELTED_MANGANESE_BUCKET = ItemEntry.register(
				"melted_manganese_bucket", () -> new Item(new Item.Properties()), ItemEntry.ItemGroupType.MATERIAL_AND_FOODS
		);

		private ECCompatItems() {}

		public static void init() {}
	}

	private RPMItems() {}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		RawOreItems.init();
		SpiritBeads.init();

		if(ModsCompatManager.EMERALD_CRAFT) {
			ECCompatItems.init();
		}
	}

	public static class ItemEntry<T extends Item> implements Supplier<T>, ItemLike {
		public enum ItemGroupType {
			BUILDING_BLOCKS,
			MATERIAL_AND_FOODS,
			CREATIVE_ONLY
		}

		public static final List<ItemEntry<? extends Item>> BUILDING_BLOCKS = Lists.newArrayList();
		public static final List<ItemEntry<? extends Item>> MATERIAL_AND_FOODS = Lists.newArrayList();
		public static final List<ItemEntry<? extends Item>> CREATIVE_ONLY = Lists.newArrayList();

		private final RegistryObject<T> regObject;

		public static <T extends Item> ItemEntry<T> register(String name, Supplier<? extends T> make, ItemGroupType itemGroupType) {
			return new ItemEntry<>(REGISTER.register(name, make), itemGroupType);
		}

		private ItemEntry(RegistryObject<T> regObject, ItemGroupType itemGroupType) {
			this.regObject = regObject;
			switch (itemGroupType) {
				case BUILDING_BLOCKS -> BUILDING_BLOCKS.add(this);
				case MATERIAL_AND_FOODS -> MATERIAL_AND_FOODS.add(this);
				case CREATIVE_ONLY -> CREATIVE_ONLY.add(this);
			}
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
