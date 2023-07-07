package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.ZombieTyrant;
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
	public static final EntityType<ZombieTyrant> ZOMBIE_TYRANT = EntityType.Builder.<ZombieTyrant>of(ZombieTyrant::new, MobCategory.MONSTER)
			.sized(1.0F, 2.8F)
			.clientTrackingRange(10)
			.build(new ResourceLocation(MODID, "zombie_tyrant").toString());

	public static void init(RegisterEvent event) {
		event.register(Registries.ENTITY_TYPE, helper -> {
			helper.register(new ResourceLocation(MODID, "dark_zombie_knight"), DARK_ZOMBIE_KNIGHT);
			helper.register(new ResourceLocation(MODID, "zombie_tyrant"), ZOMBIE_TYRANT);
		});
	}
}
