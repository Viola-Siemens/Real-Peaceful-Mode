package com.hexagram2021.real_peaceful_mode.common.item;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper.getRegistryName;

public class DebugWishItem extends Item {
	private final EntityType<?> entityType;

	public DebugWishItem(EntityType<?> entityType, Properties properties) {
		super(properties);
		this.entityType = entityType;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if(hand == InteractionHand.MAIN_HAND && player.getAbilities().instabuild && player instanceof IMonsterHero hero) {
			hero.setHero(this.entityType);
			ResourceLocation current = getRegistryName(this);
			player.sendSystemMessage(Component.translatable("message." + current.getNamespace() + "." + current.getPath() + ".success"));
			return InteractionResultHolder.consume(itemstack);
		}
		return InteractionResultHolder.pass(itemstack);
	}
}
