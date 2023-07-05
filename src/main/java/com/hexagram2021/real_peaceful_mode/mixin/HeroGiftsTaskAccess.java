package com.hexagram2021.real_peaceful_mode.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(GiveGiftToHero.class)
public interface HeroGiftsTaskAccess  {
	@Accessor("GIFTS")
	static Map<VillagerProfession, ResourceLocation> getGifts() {
		throw new UnsupportedOperationException("Replaced by Mixin");
	}
}
