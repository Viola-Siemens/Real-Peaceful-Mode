package com.hexagram2021.real_peaceful_mode.common.block.entity;

import com.hexagram2021.real_peaceful_mode.api.IMissionProvider;
import com.hexagram2021.real_peaceful_mode.api.MissionHelper;
import com.hexagram2021.real_peaceful_mode.api.MissionType;
import com.hexagram2021.real_peaceful_mode.common.block.CultureTableBlock;
import com.hexagram2021.real_peaceful_mode.common.crafting.menu.CultureTableMenu;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.IMissionStack;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlockEntities;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
public class CultureTableBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible, IMissionProvider {
	public static final int SLOT_INPUT = 0;
	public static final int SLOT_MIX1 = 1;
	public static final int SLOT_MIX2 = 2;
	public static final int SLOT_FUEL = 3;
	public static final int SLOT_RESULT = 4;
	public static final int NUM_SLOTS = 5;

	protected static final int DATA_ANALYZE_TIME = 0;
	protected static final int DATA_ANALYZE_TOTAL_TIME = 1;
	protected static final int DATA_BONE_MEAL = 2;
	public static final int NUM_DATA_VALUES = 3;

	private static final int[] SLOTS_FOR_UP = new int[]{SLOT_INPUT, SLOT_MIX1, SLOT_MIX2};
	private static final int[] SLOTS_FOR_DOWN = new int[]{SLOT_RESULT, SLOT_FUEL};
	private static final int[] SLOTS_FOR_SIDES = new int[]{SLOT_FUEL, SLOT_MIX1, SLOT_MIX2};

	public static final int BONE_MEAL_AMOUNT = 100;
	public static final int ANALYZE_TIME = 600;

	protected NonNullList<ItemStack> items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);

	int analyzeTime;
	int analyzeDuration;
	int boneMeal;
	
	protected final ContainerData dataAccess = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case DATA_ANALYZE_TIME -> CultureTableBlockEntity.this.analyzeTime;
				case DATA_ANALYZE_TOTAL_TIME -> CultureTableBlockEntity.this.analyzeDuration;
				case DATA_BONE_MEAL -> CultureTableBlockEntity.this.boneMeal;
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case DATA_ANALYZE_TIME -> CultureTableBlockEntity.this.analyzeTime = value;
				case DATA_ANALYZE_TOTAL_TIME -> CultureTableBlockEntity.this.analyzeDuration = value;
				case DATA_BONE_MEAL -> CultureTableBlockEntity.this.boneMeal = value;
			}

		}

		public int getCount() {
			return NUM_DATA_VALUES;
		}
	};

	public CultureTableBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(RPMBlockEntities.CULTURE_TABLE.get(), blockPos, blockState);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.culture_table");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}
	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new CultureTableMenu(id, inventory, this, this.dataAccess);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(nbt, this.items, registries);
		this.analyzeTime = nbt.getInt("AnalyzeTime");
		this.analyzeDuration = nbt.getInt("Duration");
		this.boneMeal = nbt.getInt("BoneMeal");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		nbt.putInt("AnalyzeTime", this.analyzeTime);
		nbt.putInt("Duration", this.analyzeDuration);
		nbt.putInt("BoneMeal", this.boneMeal);
		ContainerHelper.saveAllItems(nbt, this.items, registries);
	}

	public static final Ingredient ACCEPTABLE_FLOWERS = Ingredient.of(
			Items.ALLIUM, Items.BLUE_ORCHID, Items.TORCHFLOWER,
			Items.ORANGE_TULIP, Items.PINK_TULIP, Items.RED_TULIP, Items.WHITE_TULIP
	);
	private static final int MAX_FLOWER_TYPES = ACCEPTABLE_FLOWERS.getItems().length;
	public static final Ingredient SLIME_COLLOID_INGREDIENT = Ingredient.of(
			Items.WHEAT, Items.VINE
	);

	private boolean isLit() {
		return this.analyzeTime > 0;
	}

	public static boolean isInput(ItemStack itemStack) {
		return itemStack.is(Items.GUNPOWDER);
	}

	public static boolean canAnalyzeCreeper(ItemStack itemStack) {
		return ACCEPTABLE_FLOWERS.test(itemStack);
	}
	public static boolean canAnalyzeSlime(ItemStack itemStack) {
		return SLIME_COLLOID_INGREDIENT.test(itemStack);
	}
	public static boolean canAnalyze(ItemStack itemStack) {
		return canAnalyzeCreeper(itemStack) || canAnalyzeSlime(itemStack);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState blockState, CultureTableBlockEntity blockEntity) {
		boolean changed = false;
		if(!blockEntity.canAnalyze()) {
			blockEntity.analyzeTime = 0;
		} else {
			++blockEntity.analyzeTime;
			if(blockEntity.boneMeal > 0) {
				--blockEntity.boneMeal;
				blockEntity.analyzeTime += 5;
			}
			if(blockEntity.analyzeTime >= blockEntity.analyzeDuration) {
				changed = true;
				blockEntity.finishAnalyze();
			}
		}
		if(blockEntity.boneMeal <= 0 && !blockEntity.items.get(SLOT_FUEL).isEmpty()) {
			changed = true;
			blockEntity.items.get(SLOT_FUEL).shrink(1);
			blockEntity.boneMeal = BONE_MEAL_AMOUNT;
		}
		if(blockState.getValue(CultureTableBlock.LIT) != blockEntity.isLit()) {
			changed = true;
			blockState = blockState.setValue(CultureTableBlock.LIT, blockEntity.isLit());
			level.setBlock(pos, blockState, Block.UPDATE_ALL);
		}
		if(changed) {
			setChanged(level, pos, blockState);
		}
	}

	private boolean canAnalyzeCreeper() {
		return canAnalyzeCreeper(this.items.get(SLOT_MIX1)) && canAnalyzeCreeper(this.items.get(SLOT_MIX2)) &&
				!ItemStack.isSameItem(this.items.get(SLOT_MIX1), this.items.get(SLOT_MIX2));
	}
	private boolean canAnalyzeSlime() {
		return canAnalyzeSlime(this.items.get(SLOT_MIX1)) && canAnalyzeSlime(this.items.get(SLOT_MIX2)) &&
				!ItemStack.isSameItem(this.items.get(SLOT_MIX1), this.items.get(SLOT_MIX2));
	}
	private boolean isAnalyzableTable() {
		return isInput(this.items.get(SLOT_INPUT)) && this.items.get(SLOT_RESULT).isEmpty();
	}

	private boolean canAnalyze() {
		return this.isAnalyzableTable() && (this.canAnalyzeCreeper() || this.canAnalyzeSlime());
	}

	private void finishAnalyze() {
		if(this.level instanceof ServerLevel serverLevel) {
			CultureTableMissions mission = this.getTriggerableMission();
			if (mission != null) {
				switch (mission) {
					case CREEPER -> {
						long seed = (serverLevel.getSeed() ^ this.getBlockPos().asLong()) & 0x7fffffff;
						int a = (int) (seed % MAX_FLOWER_TYPES);
						int b = (int) ((seed / MAX_FLOWER_TYPES) % (MAX_FLOWER_TYPES - 1));
						if (b >= a) {
							b += 1;
						}
						this.items.get(SLOT_INPUT).shrink(1);
						ItemStack[] items = ACCEPTABLE_FLOWERS.getItems();
						int cnt = 0;
						if (ItemStack.isSameItem(items[a], this.items.get(SLOT_MIX1))) {
							++cnt;
						}
						if (ItemStack.isSameItem(items[a], this.items.get(SLOT_MIX2))) {
							++cnt;
						}
						if (ItemStack.isSameItem(items[b], this.items.get(SLOT_MIX1))) {
							++cnt;
						}
						if (ItemStack.isSameItem(items[b], this.items.get(SLOT_MIX2))) {
							++cnt;
						}
						this.items.get(SLOT_MIX1).shrink(1);
						this.items.get(SLOT_MIX2).shrink(1);
						if (cnt >= 2) {
							this.items.set(SLOT_RESULT, new ItemStack(RPMItems.Materials.EXPERIMENT_FLOWER));
						} else {
							if (cnt == 1) {
								this.items.set(SLOT_RESULT, serverLevel.getRandom().nextBoolean() ? new ItemStack(Items.BONE_MEAL) : new ItemStack(Items.SHORT_GRASS));
							} else {
								this.items.set(SLOT_RESULT, new ItemStack(Items.SHORT_GRASS));
							}
							return;		//Don't trigger mission.
						}
					}
					case SLIME -> {
						this.items.set(SLOT_RESULT, new ItemStack(RPMItems.Materials.SLIME_COLLOID));
						this.items.get(SLOT_MIX1).shrink(1);
						this.items.get(SLOT_MIX2).shrink(1);
					}
				}
				mission.tryTrigger(serverLevel, this);
			}
		}
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.DOWN) {
			return SLOTS_FOR_DOWN;
		}
		return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
		return this.canPlaceItem(index, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		if (direction == Direction.DOWN && index == SLOT_FUEL) {
			return itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
		}
		return true;
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.items.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(this.items, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.items, index);
	}

	@Override
	public void setItem(int index, ItemStack itemStack) {
		ItemStack slot = this.items.get(index);
		boolean flag = !itemStack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, slot);
		this.items.set(index, itemStack);
		if (itemStack.getCount() > this.getMaxStackSize()) {
			itemStack.setCount(this.getMaxStackSize());
		}

		if ((index == SLOT_INPUT || index == SLOT_MIX1 || index == SLOT_MIX2) && !flag) {
			this.analyzeDuration = ANALYZE_TIME;
			this.analyzeTime = 0;
			this.setChanged();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		}
		return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack itemStack) {
		if (index == SLOT_RESULT) {
			return false;
		}
		if(index == SLOT_INPUT) {
			return isInput(itemStack);
		}
		if (index == SLOT_FUEL) {
			return itemStack.is(Items.BONE_MEAL);
		}
		return canAnalyze(itemStack);
	}

	@Override
	public void clearContent() {
		this.items.clear();
	}

	@Override
	public void fillStackedContents(StackedContents contents) {
		for(ItemStack itemstack : this.items) {
			contents.accountStack(itemstack);
		}
	}

	@Override @Nullable
	public CultureTableMissions getTriggerableMission() {
		if(this.isAnalyzableTable()) {
			if(this.canAnalyzeCreeper()) {
				return CultureTableMissions.CREEPER;
			}
			if(this.canAnalyzeSlime()) {
				return CultureTableMissions.SLIME;
			}
		}
		return null;
	}

	public enum CultureTableMissions implements TriggerableMission<CultureTableBlockEntity>, IMissionStack {
		CREEPER("creeper1", MissionType.FINISH),
		SLIME("slime2", MissionType.FINISH) {
			@Override
			public boolean tryTrigger(ServerLevel serverLevel, CultureTableBlockEntity outer) {
				return false;
			}
		};

		final ResourceLocation missionId;
		final MissionType type;

		CultureTableMissions(String mission, MissionType type) {
			this.missionId = ResourceLocation.fromNamespaceAndPath(MODID, mission);
			this.type = type;
		}

		@Override
		public String getSerializedName() {
			return this.missionId + "/" + this.type.getSerializedName();
		}

		@Override
		public boolean tryTrigger(ServerLevel serverLevel, CultureTableBlockEntity outer) {
			MissionHelper.triggerMissionForPlayers(
					this.missionId(), this.type(),
					serverLevel, player -> player.position().closerThan(outer.getBlockPos().getCenter(), 32.0D),
					null, player -> {
					}
			);
			return true;
		}

		@Override
		public ResourceLocation missionId() {
			return missionId;
		}

		@Override
		public MissionType type() {
			return type;
		}
	}
}
