package com.hexagram2021.real_peaceful_mode.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class PikeItem extends SwordItem {
	protected static final ResourceLocation BASE_ENTITY_REACH_ID = ResourceLocation.fromNamespaceAndPath(MODID, "base_entity_reach");

	public PikeItem(Tier tier, Properties props) {
		super(tier, props);
	}

	public static ItemAttributeModifiers createAttributes(Tier pTier, float attackDamage, float attackSpeed) {
		ItemAttributeModifiers swordModifiers = SwordItem.createAttributes(pTier, attackDamage, attackSpeed);
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		for(ItemAttributeModifiers.Entry entry: swordModifiers.modifiers()) {
			builder.add(entry.attribute(), entry.modifier(), entry.slot());
		}
		builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(BASE_ENTITY_REACH_ID, 1.5D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		return builder.build();
	}
}
