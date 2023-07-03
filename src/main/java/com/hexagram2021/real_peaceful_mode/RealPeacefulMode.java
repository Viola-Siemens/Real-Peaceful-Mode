package com.hexagram2021.real_peaceful_mode;

import com.hexagram2021.real_peaceful_mode.client.ClientProxy;
import com.hexagram2021.real_peaceful_mode.common.ForgeEventHandler;
import com.hexagram2021.real_peaceful_mode.common.RPMContent;
import com.hexagram2021.real_peaceful_mode.common.RPMSaveData;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod(RealPeacefulMode.MODID)
public class RealPeacefulMode {
    public static final String MODID = "real_peaceful_mode";
    public static final String MODNAME = "Real Peaceful Mode";

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

    public RealPeacefulMode() {
        RPMLogger.logger = LogManager.getLogger(MODID);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
        DeferredWorkQueue queue = DeferredWorkQueue.lookup(Optional.of(ModLoadingStage.CONSTRUCT)).orElseThrow();
        Consumer<Runnable> runLater = job -> queue.enqueueWork(
                ModLoadingContext.get().getActiveContainer(), job
        );
        RPMContent.modConstruction(bus, runLater);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, bootstrapErrorToXCPInDev(() -> ClientProxy::modConstruction));

        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RPMContent.init();
        });
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
