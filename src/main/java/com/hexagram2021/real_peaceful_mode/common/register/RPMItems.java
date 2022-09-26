package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.RealPeacefulMode;
import com.hexagram2021.real_peaceful_mode.common.crafting.compat.ModsCompatManager;
import net.minecraft.Util;
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
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMItems {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static class RawOreItems {
		public static ItemRegObject<Item> RAW_ALUMINUM = ItemRegObject.register(
				"raw_aluminum", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP))
		);
		public static ItemRegObject<Item> RAW_SILVER = ItemRegObject.register(
				"raw_silver", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP))
		);
		public static ItemRegObject<Item> RAW_MANGANESE = ItemRegObject.register(
				"raw_manganese", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP))
		);

		private RawOreItems() {}

		public static void init() {}
	}

	public static class SpiritBeads {
		public static ItemRegObject<Item> HUGE_SPIRIT_BEAD = ItemRegObject.register(
				"huge_spirit_bead", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP)) {
					@Override
					public boolean isFoil(@NotNull ItemStack itemStack) {
						return true;
					}
				}
		);

		private SpiritBeads() {}

		public static void init() {}
	}

	public static class ECCompatItems {
		public static ItemRegObject<Item> ALUMINUM_CONCENTRATE = ItemRegObject.register(
				"aluminum_concentrate", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP))
		);
		public static ItemRegObject<Item> MELTED_ALUMINUM_BUCKET = ItemRegObject.register(
				"melted_aluminum_bucket", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP))
		);
		public static ItemRegObject<Item> SILVER_CONCENTRATE = ItemRegObject.register(
				"silver_concentrate", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP))
		);
		public static ItemRegObject<Item> MELTED_SILVER_BUCKET = ItemRegObject.register(
				"melted_silver_bucket", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP))
		);
		public static ItemRegObject<Item> MANGANESE_CONCENTRATE = ItemRegObject.register(
				"manganese_concentrate", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP))
		);
		public static ItemRegObject<Item> MELTED_MANGANESE_BUCKET = ItemRegObject.register(
				"melted_manganese_bucket", () -> new Item(new Item.Properties().tab(RealPeacefulMode.ITEM_GROUP))
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

	public static class ItemRegObject<T extends Item> implements Supplier<T>, ItemLike {
		private final RegistryObject<T> regObject;

		private static ItemRegObject<Item> simple(String name) {
			return simple(name, $ -> { }, $ -> { });
		}

		private static ItemRegObject<Item> simple(String name, Consumer<Item.Properties> makeProps, Consumer<Item> processItem) {
			return register(name, () -> Util.make(new Item(Util.make(new Item.Properties(), makeProps)), processItem));
		}

		private static <T extends Item> ItemRegObject<T> register(String name, Supplier<? extends T> make) {
			return new ItemRegObject<>(REGISTER.register(name, make));
		}

		private static <T extends Item> ItemRegObject<T> of(T existing) {
			return new ItemRegObject<>(RegistryObject.create(existing.getRegistryName(), ForgeRegistries.ITEMS));
		}

		private ItemRegObject(RegistryObject<T> regObject)
		{
			this.regObject = regObject;
		}

		@Override
		@Nonnull
		public T get()
		{
			return regObject.get();
		}

		@Nonnull
		@Override
		public Item asItem()
		{
			return regObject.get();
		}

		public ResourceLocation getId()
		{
			return regObject.getId();
		}
	}
}
