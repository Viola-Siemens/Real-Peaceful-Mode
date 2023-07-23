package com.hexagram2021.real_peaceful_mode;

import com.hexagram2021.real_peaceful_mode.client.ClientProxy;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.RPMContent;
import com.hexagram2021.real_peaceful_mode.common.RPMSaveData;
import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.register.RPMStructures;
import com.hexagram2021.real_peaceful_mode.common.register.RPMTriggers;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.hexagram2021.real_peaceful_mode.common.world.village.Villages;
import com.hexagram2021.real_peaceful_mode.network.ClientboundMissionMessagePacket;
import com.hexagram2021.real_peaceful_mode.network.IRPMPacket;
import com.hexagram2021.real_peaceful_mode.network.GetMissionsPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
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

	public static final SimpleChannel packetHandler = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main"))
			.networkProtocolVersion(() -> VERSION)
			.serverAcceptedVersions(VERSION::equals)
			.clientAcceptedVersions(VERSION::equals)
			.simpleChannel();

	public RealPeacefulMode() {
		RPMLogger.logger = LogManager.getLogger(MODID);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.addListener(this::tagsUpdated);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		DeferredWorkQueue queue = DeferredWorkQueue.lookup(Optional.of(ModLoadingStage.CONSTRUCT)).orElseThrow();
		Consumer<Runnable> runLater = job -> queue.enqueueWork(
				ModLoadingContext.get().getActiveContainer(), job
		);
		RPMContent.modConstruction(bus, runLater);
		DistExecutor.safeRunWhenOn(Dist.CLIENT, bootstrapErrorToXCPInDev(() -> ClientProxy::modConstruction));

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RPMCommonConfig.getConfig());

		bus.addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
		MinecraftForge.EVENT_BUS.register(this);
	}

	private static int messageId = 0;
	private static <T extends IRPMPacket> void registerMessage(Class<T> packetType,
															   Function<FriendlyByteBuf, T> constructor) {
		packetHandler.registerMessage(messageId++, packetType, IRPMPacket::write, constructor, (packet, ctx) -> packet.handle(ctx.get()));
	}

	private void setup(final FMLCommonSetupEvent event) {
		RPMTriggers.init();
		event.enqueueWork(RPMContent::init);
		registerMessage(GetMissionsPacket.class, GetMissionsPacket::new);
		registerMessage(ClientboundMissionMessagePacket.class, ClientboundMissionMessagePacket::new);
	}

	public void tagsUpdated(TagsUpdatedEvent event) {
		if(event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
			return;
		}

		RPMStructures.init(event.getRegistryAccess());
		Villages.addAllStructuresToPool(event.getRegistryAccess());
	}

	public void serverStarted(ServerStartedEvent event) {
		ServerLevel world = event.getServer().getLevel(Level.OVERWORLD);
		assert world != null;
		if(!world.isClientSide) {
			RPMSaveData worldData = world.getDataStorage().computeIfAbsent(RPMSaveData::new, RPMSaveData::new, RPMSaveData.dataName);
			RPMSaveData.setInstance(worldData);
		}
	}
}
