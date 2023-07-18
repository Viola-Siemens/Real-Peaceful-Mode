package com.hexagram2021.real_peaceful_mode.common.crafting.menu;

import com.hexagram2021.real_peaceful_mode.common.register.RPMMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import static com.hexagram2021.real_peaceful_mode.common.block.entity.CultureTableBlockEntity.*;

public class CultureTableMenu extends AbstractContainerMenu {
	private final Container container;
	private final ContainerData data;
	protected final Level level;

	public static final int INV_SLOT_START = 5;
	private static final int INV_SLOT_END = 32;
	private static final int USE_ROW_SLOT_START = 32;
	private static final int USE_ROW_SLOT_END = 41;

	public CultureTableMenu(int id, Inventory inventory) {
		this(id, inventory, new SimpleContainer(NUM_SLOTS), new SimpleContainerData(NUM_DATA_VALUES));
	}

	public CultureTableMenu(int id, Inventory inventory, Container container, ContainerData data) {
		super(RPMMenuTypes.CULTURE_TABLE_MENU.get(), id);
		this.container = container;
		this.data = data;
		this.level = inventory.player.level();

		this.addSlot(new Slot(this.container, SLOT_INPUT, 32, 41));
		this.addSlot(new Slot(this.container, SLOT_MIX1, 32, 17));
		this.addSlot(new Slot(this.container, SLOT_MIX2, 56, 17));
		this.addSlot(new CultureTableMenu.CultureTableFuelSlot(this.container, SLOT_FUEL, 81, 9));
		this.addSlot(new CultureTableMenu.CultureTableResultSlot(this.container, SLOT_RESULT, 116, 35));

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
		}

		this.addDataSlots(this.data);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack slotItem = slot.getItem();
			itemstack = slotItem.copy();
			if (index == SLOT_RESULT) {
				if (!this.moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(slotItem, itemstack);
			} else if (index != SLOT_FUEL && index != SLOT_INPUT && index != SLOT_MIX1 && index != SLOT_MIX2) {
				if (isInput(slotItem)) {
					if (!this.moveItemStackTo(slotItem, SLOT_INPUT, SLOT_MIX1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (canAnalyze(slotItem)) {
					if (!this.moveItemStackTo(slotItem, SLOT_MIX1, SLOT_FUEL, false)) {
						return ItemStack.EMPTY;
					}
				} else if (CultureTableFuelSlot.isFuel(slotItem)) {
					if (!this.moveItemStackTo(slotItem, SLOT_FUEL, SLOT_RESULT, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= INV_SLOT_START && index < INV_SLOT_END) {
					if (!this.moveItemStackTo(slotItem, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= USE_ROW_SLOT_START && index < USE_ROW_SLOT_END &&
						!this.moveItemStackTo(slotItem, INV_SLOT_START, INV_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(slotItem, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
				return ItemStack.EMPTY;
			}

			if (slotItem.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (slotItem.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, slotItem);
		}

		return itemstack;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	public int getBoneMealAmount() {
		int i = this.data.get(2);
		return i * 18 / BONE_MEAL_AMOUNT;
	}

	public int getAnalyzeProgress() {
		int i = this.data.get(1);
		if (i == 0) {
			i = ANALYZE_TIME;
		}

		return this.data.get(0) * 24 / i;
	}

	static class CultureTableFuelSlot extends Slot {
		public CultureTableFuelSlot(Container container, int slot, int x, int y) {
			super(container, slot, x, y);
		}

		public static boolean isFuel(ItemStack itemStack) {
			return itemStack.is(Items.BONE_MEAL);
		}

		@Override
		public boolean mayPlace(ItemStack itemStack) {
			return isFuel(itemStack);
		}
	}

	static class CultureTableResultSlot extends Slot {

		public CultureTableResultSlot(Container container, int slot, int x, int y) {
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack itemStack) {
			return false;
		}

		@Override
		public int getMaxStackSize(ItemStack itemStack) {
			return 1;
		}
	}
}
