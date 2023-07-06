package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegisterEvent;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMEntities {
	public static final EntityType<DarkZombieKnight> DARK_ZOMBIE_KNIGHT = EntityType.Builder.<DarkZombieKnight>of(DarkZombieKnight::new, MobCategory.MONSTER)
			.sized(0.6F, 1.9F)
			.clientTrackingRange(8)
			.build(new ResourceLocation(MODID, "dark_zombie_knight").toString());

	public static void init(RegisterEvent event) {
		event.register(Registries.ENTITY_TYPE, helper -> {
			helper.register(new ResourceLocation(MODID, "dark_zombie_knight"), DARK_ZOMBIE_KNIGHT);
		});
	}
}
