package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.common.register.RPMFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

@SuppressWarnings("deprecation")
public class ModVanillaCompat {
	public static void setup() {
		registerDispenser(RPMFluids.MAGIC_POOL_WATER_FLUID);
		registerDispenser(RPMFluids.DARK_MAGIC_POOL_WATER_FLUID);
	}

	private static final DispenseItemBehavior BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
		private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

		@Override
		public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
			BucketItem bucketitem = (BucketItem)itemStack.getItem();
			BlockPos blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
			Level level = blockSource.getLevel();
			if(bucketitem.emptyContents(null, level, blockpos, null)) {
				bucketitem.checkExtraContent(null, level, itemStack, blockpos);
				return new ItemStack(Items.BUCKET);
			}
			return this.defaultBehavior.dispense(blockSource, itemStack);
		}
	};

	private static void registerDispenser(RPMFluids.FluidEntry<?> entry) {
		DispenserBlock.registerBehavior(entry.getBucket(), BUCKET_DISPENSE_BEHAVIOR);
	}
}
