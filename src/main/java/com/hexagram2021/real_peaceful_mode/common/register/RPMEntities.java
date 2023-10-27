package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import com.hexagram2021.real_peaceful_mode.common.entity.HuskWorkmanEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.PinkCreeperEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.HuskPharaoh;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.SkeletonKing;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.ZombieTyrant;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.FlameEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.SkeletonSkullEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.TinyFireballEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegisterEvent;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class RPMEntities {
    public static final EntityType<DarkZombieKnight> DARK_ZOMBIE_KNIGHT = EntityType.Builder.of(DarkZombieKnight::new, MobCategory.MONSTER)
            .sized(0.6F, 1.9F)
            .clientTrackingRange(8)
            .build(new ResourceLocation(MODID, "dark_zombie_knight").toString());
    public static final EntityType<PinkCreeperEntity> PINK_CREEPER = EntityType.Builder.of(PinkCreeperEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.7F)
            .clientTrackingRange(8)
            .build(new ResourceLocation(MODID, "pink_creeper").toString());
    public static final EntityType<HuskWorkmanEntity> HUSK_WORKMAN = EntityType.Builder.of(HuskWorkmanEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(8)
            .build(new ResourceLocation(MODID, "husk_workman").toString());
    public static final EntityType<ZombieTyrant> ZOMBIE_TYRANT = EntityType.Builder.of(ZombieTyrant::new, MobCategory.MONSTER)
            .sized(0.9F, 2.0F)
            .clientTrackingRange(10)
            .build(new ResourceLocation(MODID, "zombie_tyrant").toString());
    public static final EntityType<SkeletonKing> SKELETON_KING = EntityType.Builder.of(SkeletonKing::new, MobCategory.MONSTER)
            .sized(0.9F, 2.0F)
            .clientTrackingRange(10)
            .build(new ResourceLocation(MODID, "skeleton_king").toString());
    public static final EntityType<HuskPharaoh> HUSK_PHARAOH = EntityType.Builder.of(HuskPharaoh::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(8)
            .build(new ResourceLocation(MODID, "husk_pharaoh").toString());
    public static final EntityType<SkeletonSkullEntity> SKELETON_SKULL = EntityType.Builder.<SkeletonSkullEntity>of(SkeletonSkullEntity::new, MobCategory.MISC)
            .sized(0.3125F, 0.3125F)
            .clientTrackingRange(8)
            .updateInterval(6)
            .build(new ResourceLocation(MODID, "skeleton_skull").toString());
    public static final EntityType<TinyFireballEntity> TINY_FIREBALL = EntityType.Builder.<TinyFireballEntity>of(TinyFireballEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(6)
            .build(new ResourceLocation(MODID, "tiny_fireball").toString());
    public static final EntityType<FlameEntity> FLAME_CRYSTAL = EntityType.Builder.<FlameEntity>of(FlameEntity::new, MobCategory.MISC)
            .sized(1.25F, 1.25F)
            .clientTrackingRange(16)
            .updateInterval(Integer.MAX_VALUE)
            .build(new ResourceLocation(MODID, "flame_crystal").toString());

    public static void init(RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE, helper -> {
            helper.register(new ResourceLocation(MODID, "dark_zombie_knight"), DARK_ZOMBIE_KNIGHT);
            helper.register(new ResourceLocation(MODID, "pink_creeper"), PINK_CREEPER);
            helper.register(new ResourceLocation(MODID, "husk_workman"), HUSK_WORKMAN);
            helper.register(new ResourceLocation(MODID, "zombie_tyrant"), ZOMBIE_TYRANT);
            helper.register(new ResourceLocation(MODID, "skeleton_king"), SKELETON_KING);
            helper.register(new ResourceLocation(MODID, "husk_pharaoh"), HUSK_PHARAOH);
            helper.register(new ResourceLocation(MODID, "skeleton_skull"), SKELETON_SKULL);
            helper.register(new ResourceLocation(MODID, "tiny_fireball"), TINY_FIREBALL);
            helper.register(new ResourceLocation(MODID, "flame_crystal"), FLAME_CRYSTAL);
        });
    }
}
