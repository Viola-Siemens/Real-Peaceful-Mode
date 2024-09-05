package com.hexagram2021.real_peaceful_mode.common.item;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class PikeItem extends SwordItem {
	protected static final UUID BASE_ENTITY_REACH_UUID = UUID.fromString("1AF44E47-2E2A-4B6F-AB8E-517DEC01AFD1");

	public PikeItem(Tier tier, int damage, float speed, Properties props) {
		super(tier, damage, speed, props);
		this.defaultModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder()
				.putAll(this.defaultModifiers)
				.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(BASE_ENTITY_REACH_UUID, "Weapon modifier", 1.5D, AttributeModifier.Operation.ADDITION))
				.build();
	}
}
