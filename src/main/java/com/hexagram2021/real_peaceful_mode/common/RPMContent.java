package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.common.crafting.compat.ModsCompatManager;
import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnight;
import com.hexagram2021.real_peaceful_mode.common.entity.PinkCreeperEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.HuskPharaoh;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.SkeletonKing;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.ZombieTyrant;
import com.hexagram2021.real_peaceful_mode.common.register.*;
import com.hexagram2021.real_peaceful_mode.common.world.village.Villages;
import com.hexagram2021.real_peaceful_mode.mixin.BlockEntityTypeAccess;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Set;
import java.util.function.Consumer;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RPMContent {
	public static void modConstruction(IEventBus bus, Consumer<Runnable> runLater) {
		ModsCompatManager.compatModLoaded();

		initTags();

		RPMFluids.init(bus);
		RPMBlocks.init(bus);
		RPMItems.init(bus);
		RPMMobEffects.init(bus);
		Villages.Registers.init(bus);
		RPMBlockEntities.init(bus);
		RPMCreativeTabs.init(bus);
		RPMMenuTypes.init(bus);
		RPMStructureTypes.init(bus);
		RPMEnchantments.init(bus);
	}

	private static void initTags() {
		RPMBlockTags.init();
		RPMBiomeTags.init();
		RPMStructureTags.init();
		RPMStructureKeys.init();
		RPMStructureSetKeys.init();
	}

	public static void init() {
		Villages.init();
		ModVanillaCompat.setup();
		appendBlocksToBlockEntities();
	}

	private static void appendBlocksToBlockEntities() {
		BlockEntityTypeAccess skullBuilderAccess = (BlockEntityTypeAccess) BlockEntityType.SKULL;
		Set<Block> skullValidBlocks = new ObjectOpenHashSet<>(skullBuilderAccess.rpm_getValidBlocks());

		skullValidBlocks.add(RPMBlocks.Decoration.DARK_ZOMBIE_KNIGHT_SKULL.get());
		skullValidBlocks.add(RPMBlocks.Decoration.DARK_ZOMBIE_KNIGHT_WALL_SKULL.get());

		skullBuilderAccess.rpm_setValidBlocks(skullValidBlocks);
	}

	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		RPMSounds.init(event);
		RPMEntities.init(event);
		RPMStructurePieceTypes.init();
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(RPMEntities.DARK_ZOMBIE_KNIGHT, DarkZombieKnight.createAttributes().build());
		event.put(RPMEntities.PINK_CREEPER, PinkCreeperEntity.createAttributes().build());
		event.put(RPMEntities.ZOMBIE_TYRANT, ZombieTyrant.createAttributes().build());
		event.put(RPMEntities.SKELETON_KING, SkeletonKing.createAttributes().build());
		event.put(RPMEntities.HUSK_PHARAOH, HuskPharaoh.createAttributes().build());
	}
}
