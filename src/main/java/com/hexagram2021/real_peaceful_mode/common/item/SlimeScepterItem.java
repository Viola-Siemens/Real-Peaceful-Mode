package com.hexagram2021.real_peaceful_mode.common.item;

import com.hexagram2021.real_peaceful_mode.api.BlockConversionRegistry;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMobEffects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

@SuppressWarnings("deprecation")
public class SlimeScepterItem extends Item implements Vanishable {
	public SlimeScepterItem(Properties props) {
		super(props);
	}

	@Override
	public int getEnchantmentValue() {
		return 2;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static BlockState getChangedBlockState(BlockState origin, Block newBlock) {
		BlockState newBlockState = newBlock.defaultBlockState();
		for(Property property: origin.getProperties()) {
			if(newBlockState.hasProperty(property)) {
				newBlockState = newBlockState.setValue(property, origin.getValue(property));
			}
		}
		return newBlockState;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		BlockPos blockPos = context.getClickedPos();
		BlockState blockState = level.getBlockState(blockPos);
		ItemStack handItem = context.getItemInHand();
		Block stickedBlock = BlockConversionRegistry.STICKABLES.get(blockState.getBlock());
		Block unstickedBlock = BlockConversionRegistry.UNSTICKABLES.get(blockState.getBlock());
		BlockState newBlockState = null;
		int deltaDamage = 0;
		if(stickedBlock != null) {
			newBlockState = getChangedBlockState(blockState, stickedBlock);
			deltaDamage = 2;
		} else if(unstickedBlock != null) {
			newBlockState = getChangedBlockState(blockState, unstickedBlock);
			deltaDamage = -2;
		}

		if(newBlockState != null) {
			level.setBlock(blockPos, newBlockState, Block.UPDATE_ALL);
			if (player instanceof ServerPlayer serverPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, blockPos, handItem);
				if(deltaDamage <= 0) {
					deltaDamage = Math.max(deltaDamage, -handItem.getDamageValue());
					handItem.setDamageValue(handItem.getDamageValue() + deltaDamage);
				} else {
					handItem.hurtAndBreak(deltaDamage, player, player1 -> player1.broadcastBreakEvent(context.getHand()));
				}
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return super.useOn(context);
	}

	@Override
	public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
		return this.isCorrectToolForDrops(itemStack, blockState) ? 100.0F : 1.0F;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemStack, LivingEntity enemy, LivingEntity user) {
		itemStack.hurtAndBreak(2, user, living -> living.broadcastBreakEvent(EquipmentSlot.MAINHAND));

		if(user instanceof Player player && !player.getCooldowns().isOnCooldown(this)) {
			enemy.addEffect(new MobEffectInstance(RPMMobEffects.GLUE.get(), 40), user);
			player.getCooldowns().addCooldown(this, 60);
		}
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity user) {
		if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
			itemStack.hurtAndBreak(1, user, living -> living.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}

		return true;
	}

	@Override
	public boolean isValidRepairItem(ItemStack scepter, ItemStack material) {
		return material.is(RPMBlocks.Decoration.STICKY_STONE.asItem()) || super.isValidRepairItem(scepter, material);
	}

	//Forge
	@Override
	public boolean isCorrectToolForDrops(ItemStack itemStack, BlockState state) {
		return BlockConversionRegistry.UNSTICKABLES.containsKey(state.getBlock());
	}
}
