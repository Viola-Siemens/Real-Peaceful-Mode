package com.hexagram2021.real_peaceful_mode;

import com.hexagram2021.real_peaceful_mode.api.RandomEventSpawnerHelper;
import com.hexagram2021.real_peaceful_mode.api.event.RegisterRandomEventSpawnerEvent;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.RPMContent;
import com.hexagram2021.real_peaceful_mode.common.RPMSaveData;
import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.MonsterCollectionShadowRecipe;
import com.hexagram2021.real_peaceful_mode.common.spawner.AbstractEventSpawner;
import com.hexagram2021.real_peaceful_mode.common.spawner.SkeletonEventSpawner;
import com.hexagram2021.real_peaceful_mode.common.spawner.ZombieEventSpawner;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.hexagram2021.real_peaceful_mode.common.world.village.Villages;
import com.hexagram2021.real_peaceful_mode.network.*;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.function.Supplier;

@Mod(RealPeacefulMode.MODID)
public class RealPeacefulMode {
	public static final String MODID = "real_peaceful_mode";
	public static final String MODNAME = "Real Peaceful Mode";
	public static final String VERSION = ModList.get().getModFileById(MODID).versionString();

	public static <T>
	Supplier<T> bootstrapErrorToXCPInDev(Supplier<T> in) {
		if(FMLLoader.isProduction()) {
			return in;
		}
		return () -> {
			try {
				return in.get();
			} catch(BootstrapMethodError e) {
				throw new RuntimeException(e);
			}
		};
	}

	public RealPeacefulMode(IEventBus modBus, ModContainer modContainer) {
		RPMLogger.logger = LogManager.getLogger(MODID);
		NeoForge.EVENT_BUS.addListener(this::tagsUpdated);
		NeoForge.EVENT_BUS.addListener(this::serverStarting);
		NeoForge.EVENT_BUS.addListener(this::registerRandomEventSpawners);
		NeoForge.EVENT_BUS.addListener(this::serverStarted);
		NeoForge.EVENT_BUS.addListener(this::datapackSync);
		RPMContent.modConstruction(modBus);

		modContainer.registerConfig(ModConfig.Type.COMMON, RPMCommonConfig.getConfig());

		modBus.addListener(this::setup);
		NeoForge.EVENT_BUS.register(new ForgeEventHandler());
		NeoForge.EVENT_BUS.addListener(RPMContent::registerCommands);
	}

	private void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(RPMContent::init);
	}

	public void tagsUpdated(TagsUpdatedEvent event) {
		if(event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
			return;
		}

		Villages.addAllStructuresToPool(event.getRegistryAccess());
	}

	public void datapackSync(OnDatapackSyncEvent event) {
		ServerPlayer player = event.getPlayer();
		ClientboundShadowRecipeSyncPacket packet = new ClientboundShadowRecipeSyncPacket(MonsterCollectionShadowRecipe.getMonsterCollectionRecipes(
				event.getPlayerList().getServer().reloadableRegistries()::getLootTable
		));
		if(player == null) {
			PacketDistributor.sendToAllPlayers(packet);
		} else {
			PacketDistributor.sendToPlayer(player, packet);
		}
	}

	public static boolean isInteractItem(Holder<Item> item, EntityType<?> entityType) {
		return RandomEventSpawnerHelper.getAllRandomEventSpawners().stream()
				.anyMatch(spawner -> spawner.getMonsterType().equals(entityType) && spawner.isInteractItem(item));
	}

	public void serverStarting(ServerStartingEvent event) {
		RandomEventSpawnerHelper.clearRandomEventSpawners();
		NeoForge.EVENT_BUS.post(new RegisterRandomEventSpawnerEvent(Dist.DEDICATED_SERVER));
	}

	public void registerRandomEventSpawners(RegisterRandomEventSpawnerEvent event) {
		AbstractEventSpawner<?>[] rpmSpawners = new AbstractEventSpawner<?>[]{
				new ZombieEventSpawner(),
				new SkeletonEventSpawner()
		};
		Arrays.stream(rpmSpawners).forEach(RandomEventSpawnerHelper::registerRandomEventSpawner);
	}

	public void serverStarted(ServerStartedEvent event) {
		ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
		assert world != null;
		if(!world.isClientSide) {
			RPMSaveData worldData = world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(RPMSaveData::new, RPMSaveData::new), RPMSaveData.dataName);
			RPMSaveData.setInstance(worldData);
		}
	}
}
