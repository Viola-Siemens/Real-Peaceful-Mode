package com.hexagram2021.real_peaceful_mode.common;

import com.hexagram2021.real_peaceful_mode.common.crafting.compat.ModsCompatManager;
import com.hexagram2021.real_peaceful_mode.common.entity.DarkZombieKnightEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.GuardSlimeEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.HuskWorkmanEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.PinkCreeperEntity;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.HuskPharaoh;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.SkeletonKing;
import com.hexagram2021.real_peaceful_mode.common.entity.boss.ZombieTyrant;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.ChatMessageTypes;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.SelectionConditionTypes;
import com.hexagram2021.real_peaceful_mode.common.register.*;
import com.hexagram2021.real_peaceful_mode.common.world.village.Villages;
import com.hexagram2021.real_peaceful_mode.mixin.BlockEntityTypeAccess;
import com.hexagram2021.real_peaceful_mode.server.commands.RPMCommands;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Set;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RPMContent {
	public static void modConstruction(IEventBus bus) {
		ModsCompatManager.compatModLoaded();

		ChatMessageTypes.init();
		SelectionConditionTypes.init();

		initTags();

		RPMFluids.init(bus);
		RPMBlocks.init(bus);
		RPMItems.init(bus);
		RPMMobEffects.init(bus);
		Villages.Registers.init(bus);
		RPMRecipes.init(bus);
		RPMRecipeSerializers.init(bus);
		RPMBlockEntities.init(bus);
		RPMCreativeTabs.init(bus);
		RPMMenuTypes.init(bus);
		RPMStructureTypes.init(bus);
		RPMEnchantments.init(bus);
	}

	private static void initTags() {
		RPMBlockTags.init();
		RPMItemTags.init();
		RPMBiomeTags.init();
		RPMStructureTags.init();
		RPMStructureKeys.init();
		RPMStructureSetKeys.init();

		RPMEnchantmentCategories.init();
	}

	public static void init() {
		Villages.init();
		ModVanillaCompat.setup();
		appendBlocksToBlockEntities();
		RPMSummonBlockEvents.registerSummonBlockExtraConditions();
		RPMSummonBlockEvents.registerSummonBlockExtraWorks();

		RPMItems.runLater();
	}

	private static void appendBlocksToBlockEntities() {
		BlockEntityTypeAccess skullBuilderAccess = (BlockEntityTypeAccess) BlockEntityType.SKULL;
		Set<Block> skullValidBlocks = new ObjectOpenHashSet<>(skullBuilderAccess.rpm$getValidBlocks());

		skullValidBlocks.add(RPMBlocks.Decoration.DARK_ZOMBIE_KNIGHT_SKULL.get());
		skullValidBlocks.add(RPMBlocks.Decoration.DARK_ZOMBIE_KNIGHT_WALL_SKULL.get());

		skullBuilderAccess.rpm$setValidBlocks(skullValidBlocks);
	}

	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		RPMSounds.init(event);
		RPMEntities.init(event);
		RPMStructurePieceTypes.init();
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(RPMEntities.DARK_ZOMBIE_KNIGHT, DarkZombieKnightEntity.createAttributes().build());
		event.put(RPMEntities.PINK_CREEPER, PinkCreeperEntity.createAttributes().build());
		event.put(RPMEntities.HUSK_WORKMAN, HuskWorkmanEntity.createAttributes().build());
		event.put(RPMEntities.ZOMBIE_TYRANT, ZombieTyrant.createAttributes().build());
		event.put(RPMEntities.SKELETON_KING, SkeletonKing.createAttributes().build());
		event.put(RPMEntities.HUSK_PHARAOH, HuskPharaoh.createAttributes().build());
		event.put(RPMEntities.GUARD_SLIME, GuardSlimeEntity.createAttributes().build());
	}


	@SubscribeEvent
	public static void registerEntitySpawnPlacement(SpawnPlacementRegisterEvent event) {
		event.register(RPMEntities.DARK_ZOMBIE_KNIGHT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
		event.register(RPMEntities.GUARD_SLIME, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GuardSlimeEntity::checkSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
	}

	public static void registerCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(RPMCommands.register(event.getBuildContext()));
	}
}
