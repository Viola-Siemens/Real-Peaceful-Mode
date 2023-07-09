package com.hexagram2021.real_peaceful_mode.common.world.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class RPMTrades {
	public static final int DEFAULT_SUPPLY = 12;
	public static final int COMMON_ITEMS_SUPPLY = 16;
	public static final int UNCOMMON_ITEMS_SUPPLY = 3;
	public static final int ONLY_SUPPLY_ONCE = 1;

	public static final int XP_LEVEL_1_SELL = 1;
	public static final int XP_LEVEL_1_BUY = 2;
	public static final int XP_LEVEL_2_SELL = 5;
	public static final int XP_LEVEL_2_BUY = 10;
	public static final int XP_LEVEL_3_SELL = 10;
	public static final int XP_LEVEL_3_BUY = 20;
	public static final int XP_LEVEL_4_SELL = 15;
	public static final int XP_LEVEL_4_BUY = 30;
	public static final int XP_LEVEL_5_TRADE = 30;

	public static final float LOW_TIER_PRICE_MULTIPLIER = 0.05F;

	static class EmeraldForItems implements VillagerTrades.ItemListing {
		private final Item item;
		private final int cost;
		private final int numberOfEmerald;
		private final int maxUses;
		private final int Xp;
		private final float priceMultiplier;

		public EmeraldForItems(ItemLike item, int cost, int numberOfEmerald, int maxUses, int Xp) {
			this.item = item.asItem();
			this.cost = cost;
			this.numberOfEmerald = numberOfEmerald;
			this.maxUses = maxUses;
			this.Xp = Xp;
			this.priceMultiplier = LOW_TIER_PRICE_MULTIPLIER;
		}

		@Override @Nullable
		public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource rand) {
			ItemStack itemstack = new ItemStack(this.item, this.cost);
			return new MerchantOffer(itemstack, new ItemStack(Items.EMERALD, numberOfEmerald), this.maxUses, this.Xp, this.priceMultiplier);
		}
	}

	static class ItemsForEmeralds implements VillagerTrades.ItemListing {
		private final ItemStack itemStack;
		private final int emeraldCost;
		private final int numberOfItems;
		private final int maxUses;
		private final int Xp;
		private final float priceMultiplier;

		public ItemsForEmeralds(ItemLike item, int emeraldCost, int numberOfItems, int maxUses, int Xp) {
			this(new ItemStack(item), emeraldCost, numberOfItems, maxUses, Xp);
		}

		public ItemsForEmeralds(ItemStack itemStack, int emeraldCost, int numberOfItems, int maxUses, int Xp) {
			this.itemStack = itemStack;
			this.emeraldCost = emeraldCost;
			this.numberOfItems = numberOfItems;
			this.maxUses = maxUses;
			this.Xp = Xp;
			this.priceMultiplier = LOW_TIER_PRICE_MULTIPLIER;
		}

		@Override @Nullable
		public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource rand) {
			return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(this.itemStack.getItem(), this.numberOfItems), this.maxUses, this.Xp, this.priceMultiplier);
		}
	}

	static class WrittenBookForBead implements VillagerTrades.ItemListing {
		private final Component title;
		private final Component author;
		private final Component[] contents;
		private final ItemLike costItem;
		private final int cost;
		private final int maxUses;
		private final int Xp;
		private final float priceMultiplier;

		public WrittenBookForBead(Component title, Component author, ItemLike costItem, int cost, int maxUses, int Xp, Component... contents) {
			this.title = title;
			this.author = author;
			this.contents = contents;
			this.costItem = costItem;
			this.cost = cost;
			this.maxUses = maxUses;
			this.Xp = Xp;
			this.priceMultiplier = LOW_TIER_PRICE_MULTIPLIER;
		}

		@Nullable
		@Override
		public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource rand) {
			ItemStack itemstack = new ItemStack(Items.WRITTEN_BOOK);
			CompoundTag compoundtag = new CompoundTag();
			compoundtag.putString(WrittenBookItem.TAG_TITLE, this.title.getString());
			compoundtag.putString(WrittenBookItem.TAG_AUTHOR, this.author.getString());
			ListTag pages = new ListTag();
			for (Component content : this.contents) {
				pages.add(StringTag.valueOf("{\"text\":\"" + content.getString() + "\"}"));
			}
			compoundtag.put(WrittenBookItem.TAG_PAGES, pages);
			itemstack.setTag(compoundtag);
			return new MerchantOffer(new ItemStack(this.costItem, this.cost), new ItemStack(Items.EMERALD), itemstack, this.maxUses, this.Xp, this.priceMultiplier);
		}
	}
}
