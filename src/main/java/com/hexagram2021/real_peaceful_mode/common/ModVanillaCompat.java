package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.common.register.RPMFluids;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.util.RPMDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

@SuppressWarnings("deprecation")
public class ModVanillaCompat {
	public static void setup() {
		registerDispensers();
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

	private static final DispenseItemBehavior PIKE_ENTITY_BEHAVIOR = new OptionalDispenseItemBehavior() {
		protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
			BlockPos blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));

			boolean success = false;
			for(LivingEntity entity : blockSource.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), LivingEntity::isAlive)) {
				boolean flag = entity.hurt(RPMDamageSources.pike(entity), 3.0F);
				if(flag) {
					success = true;
					if(itemStack.hurt(1, blockSource.getLevel().random, null)) {
						itemStack.setCount(0);
						break;
					}
				};
			}
			this.setSuccess(success);
			return itemStack;
		}
	};

	private static void registerDispensers() {
		registerDispenser(RPMFluids.MAGIC_POOL_WATER_FLUID);
		registerDispenser(RPMFluids.DARK_MAGIC_POOL_WATER_FLUID);
		DispenserBlock.registerBehavior(RPMItems.Weapons.IRON_PIKE, PIKE_ENTITY_BEHAVIOR);
		DispenserBlock.registerBehavior(RPMItems.Weapons.GOLDEN_PIKE, PIKE_ENTITY_BEHAVIOR);
		DispenserBlock.registerBehavior(RPMItems.Weapons.DIAMOND_PIKE, PIKE_ENTITY_BEHAVIOR);
		DispenserBlock.registerBehavior(RPMItems.Weapons.NETHERITE_PIKE, PIKE_ENTITY_BEHAVIOR);
	}

	private static void registerDispenser(RPMFluids.FluidEntry<?> entry) {
		DispenserBlock.registerBehavior(entry.getBucket(), BUCKET_DISPENSE_BEHAVIOR);
	}
}
