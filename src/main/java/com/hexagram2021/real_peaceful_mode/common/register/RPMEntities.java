package com.hexagram2021.real_peaceful_mode.common.register;

import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnightEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.GuardSlimeEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.HuskWorkmanEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.PinkCreeperEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.HuskPharaoh;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.SkeletonKing;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.ZombieTyrant;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.FlameEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.SkeletonSkullEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.misc.TinyFireballEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.RegisterEvent;

public class RPMEntities {
    public static final EntityType<DarkZombieKnightEntity> DARK_ZOMBIE_KNIGHT = EntityType.Builder.of(DarkZombieKnightEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.9F)
            .clientTrackingRange(8)
            .ridingOffset(-0.45F)
            .build(RPMEntityKeys.DARK_ZOMBIE_KNIGHT.location().toString());
    public static final EntityType<PinkCreeperEntity> PINK_CREEPER = EntityType.Builder.of(PinkCreeperEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.7F)
            .clientTrackingRange(8)
            .build(RPMEntityKeys.PINK_CREEPER.location().toString());
    public static final EntityType<HuskWorkmanEntity> HUSK_WORKMAN = EntityType.Builder.of(HuskWorkmanEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(8)
            .build(RPMEntityKeys.HUSK_WORKMAN.location().toString());
    public static final EntityType<ZombieTyrant> ZOMBIE_TYRANT = EntityType.Builder.of(ZombieTyrant::new, MobCategory.MONSTER)
            .sized(0.9F, 2.0F)
            .clientTrackingRange(10)
            .build(RPMEntityKeys.ZOMBIE_TYRANT.location().toString());
    public static final EntityType<SkeletonKing> SKELETON_KING = EntityType.Builder.of(SkeletonKing::new, MobCategory.MONSTER)
            .sized(0.9F, 2.0F)
            .clientTrackingRange(10)
            .build(RPMEntityKeys.SKELETON_KING.location().toString());
    public static final EntityType<HuskPharaoh> HUSK_PHARAOH = EntityType.Builder.of(HuskPharaoh::new, MobCategory.MONSTER)
            .sized(0.6F, 1.95F)
            .clientTrackingRange(8)
            .build(RPMEntityKeys.HUSK_PHARAOH.location().toString());
    public static final EntityType<GuardSlimeEntity> GUARD_SLIME = EntityType.Builder.of(GuardSlimeEntity::new, MobCategory.MONSTER)
            .sized(2.04F, 2.04F)
            .clientTrackingRange(10)
            .build(RPMEntityKeys.GUARD_SLIME.location().toString());
    public static final EntityType<SkeletonSkullEntity> SKELETON_SKULL = EntityType.Builder.<SkeletonSkullEntity>of(SkeletonSkullEntity::new, MobCategory.MISC)
            .sized(0.3125F, 0.3125F)
            .clientTrackingRange(8)
            .updateInterval(6)
            .build(RPMEntityKeys.SKELETON_SKULL.location().toString());
    public static final EntityType<TinyFireballEntity> TINY_FIREBALL = EntityType.Builder.<TinyFireballEntity>of(TinyFireballEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(6)
            .build(RPMEntityKeys.TINY_FIREBALL.location().toString());
    public static final EntityType<FlameEntity> FLAME_CRYSTAL = EntityType.Builder.<FlameEntity>of(FlameEntity::new, MobCategory.MISC)
            .sized(1.25F, 1.25F)
            .clientTrackingRange(16)
            .updateInterval(Integer.MAX_VALUE)
            .build(RPMEntityKeys.FLAME_CRYSTAL.location().toString());

    public static void init(RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE, helper -> {
            helper.register(RPMEntityKeys.DARK_ZOMBIE_KNIGHT, DARK_ZOMBIE_KNIGHT);
            helper.register(RPMEntityKeys.PINK_CREEPER, PINK_CREEPER);
            helper.register(RPMEntityKeys.HUSK_WORKMAN, HUSK_WORKMAN);
            helper.register(RPMEntityKeys.ZOMBIE_TYRANT, ZOMBIE_TYRANT);
            helper.register(RPMEntityKeys.SKELETON_KING, SKELETON_KING);
            helper.register(RPMEntityKeys.HUSK_PHARAOH, HUSK_PHARAOH);
            helper.register(RPMEntityKeys.GUARD_SLIME, GUARD_SLIME);
            helper.register(RPMEntityKeys.SKELETON_SKULL, SKELETON_SKULL);
            helper.register(RPMEntityKeys.TINY_FIREBALL, TINY_FIREBALL);
            helper.register(RPMEntityKeys.FLAME_CRYSTAL, FLAME_CRYSTAL);
        });
    }
}
