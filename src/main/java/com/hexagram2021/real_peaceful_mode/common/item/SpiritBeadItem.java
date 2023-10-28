package com.hexagram2021.real_peaceful_mode.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SpiritBeadItem extends Item {
	@Nullable
	private final EntityType<?> entityType;

	public SpiritBeadItem(@Nullable EntityType<?> entityType, Properties properties) {
		super(properties);
		this.entityType = entityType;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
		super.appendHoverText(itemStack, level, components, flag);
		components.add(Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GRAY));
	}
}
