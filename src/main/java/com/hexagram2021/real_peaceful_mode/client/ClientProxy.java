package com.hexagram2021.real_peaceful_mode.client;

import com.hexagram2021.real_peaceful_mode.client.models.DarkZombieKnightModel;
import com.hexagram2021.real_peaceful_mode.client.models.GuardSlimeModel;
import com.hexagram2021.real_peaceful_mode.client.models.SkeletonKingModel;
import com.hexagram2021.real_peaceful_mode.client.models.ZombieTyrantModel;
import com.hexagram2021.real_peaceful_mode.client.renderers.*;
import com.hexagram2021.real_peaceful_mode.client.screens.CultureTableScreen;
import com.hexagram2021.real_peaceful_mode.client.screens.ChatMessageScreen;
import com.hexagram2021.real_peaceful_mode.client.screens.MissionMessageScreen;
import com.hexagram2021.real_peaceful_mode.common.CommonProxy;
import com.hexagram2021.real_peaceful_mode.common.block.skull.RPMSkullTypes;
import com.hexagram2021.real_peaceful_mode.common.register.RPMEntities;
import com.hexagram2021.real_peaceful_mode.common.register.RPMFluids;
import com.hexagram2021.real_peaceful_mode.common.register.RPMKeys;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMenuTypes;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClientProxy extends CommonProxy {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            SkullBlockRenderer.SKIN_BY_TYPE.put(RPMSkullTypes.DARK_ZOMBIE_KNIGHT, ResourceLocation.fromNamespaceAndPath(MODID, "textures/entity/dark_zombie_knight.png"));
        });
    }

    @SubscribeEvent
    public static void registerContainersAndScreens(RegisterMenuScreensEvent event) {
        event.register(RPMMenuTypes.CHAT_MESSAGE_MENU.get(), ChatMessageScreen::new);
        event.register(RPMMenuTypes.MISSION_MESSAGE_MENU.get(), MissionMessageScreen::new);
        event.register(RPMMenuTypes.CULTURE_TABLE_MENU.get(), CultureTableScreen::new);
    }

    @SubscribeEvent
    public static void onCreateSkullModel(EntityRenderersEvent.CreateSkullModels event) {
        event.registerSkullModel(RPMSkullTypes.DARK_ZOMBIE_KNIGHT, new SkullModel(event.getEntityModelSet().bakeLayer(RPMModelLayers.DARK_ZOMBIE_KNIGHT_SKULL)));
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        RPMKeys.init();
        RPMKeys.KeyEntry.ALL_KEYS.forEach(keyEntry -> event.register(keyEntry.getKeyMapping()));
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RPMModelLayers.DARK_ZOMBIE_KNIGHT, DarkZombieKnightModel::createBodyLayer);
        event.registerLayerDefinition(RPMModelLayers.PINK_CREEPER, () -> CreeperModel.createBodyLayer(CubeDeformation.NONE));
        event.registerLayerDefinition(RPMModelLayers.HUSK_WORKMAN, () -> LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64));
        event.registerLayerDefinition(RPMModelLayers.DARK_ZOMBIE_KNIGHT_INNER_ARMOR, () -> DarkZombieKnightModel.createArmorLayer(0.5F));
        event.registerLayerDefinition(RPMModelLayers.DARK_ZOMBIE_KNIGHT_OUTER_ARMOR, () -> DarkZombieKnightModel.createArmorLayer(1.0F));
        event.registerLayerDefinition(RPMModelLayers.ZOMBIE_TYRANT, ZombieTyrantModel::createBodyLayer);
        event.registerLayerDefinition(RPMModelLayers.SKELETON_KING, SkeletonKingModel::createBodyLayer);
        event.registerLayerDefinition(RPMModelLayers.HUSK_PHARAOH, () -> LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64));
        event.registerLayerDefinition(RPMModelLayers.GUARD_SLIME, GuardSlimeModel::createInnerBodyLayer);
        event.registerLayerDefinition(RPMModelLayers.GUARD_SLIME_OUTER, GuardSlimeModel::createOuterBodyLayer);
        event.registerLayerDefinition(RPMModelLayers.DARK_ZOMBIE_KNIGHT_SKULL, SkullModel::createHumanoidHeadLayer);
        event.registerLayerDefinition(RPMModelLayers.SKELETON_SKULL, SkeletonSkullRenderer::createSkullLayer);
        event.registerLayerDefinition(RPMModelLayers.FLAME_CRYSTAL, FlameCrystalRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RPMEntities.DARK_ZOMBIE_KNIGHT, DarkZombieKnightRenderer::new);
        event.registerEntityRenderer(RPMEntities.PINK_CREEPER, PinkCreeperRenderer::new);
        event.registerEntityRenderer(RPMEntities.HUSK_WORKMAN, HuskWorkmanRenderer::new);
        event.registerEntityRenderer(RPMEntities.ZOMBIE_TYRANT, ZombieTyrantRenderer::new);
        event.registerEntityRenderer(RPMEntities.SKELETON_KING, SkeletonKingRenderer::new);
        event.registerEntityRenderer(RPMEntities.HUSK_PHARAOH, HuskPharaohRenderer::new);
        event.registerEntityRenderer(RPMEntities.GUARD_SLIME, GuardSlimeRenderer::new);
        event.registerEntityRenderer(RPMEntities.SKELETON_SKULL, SkeletonSkullRenderer::new);
        event.registerEntityRenderer(RPMEntities.TINY_FIREBALL, TinyFireballRenderer::new);
        event.registerEntityRenderer(RPMEntities.FLAME_CRYSTAL, FlameCrystalRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        Function<RPMFluids.FluidEntry<?>, IClientFluidTypeExtensions> fluidTypeExtensionBuilder = entry -> new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return entry.stillTex();
            }
            @Override
            public ResourceLocation getFlowingTexture() {
                return entry.flowingTex();
            }
        };
        Consumer<RPMFluids.FluidEntry<?>> register = entry -> event.registerFluidType(fluidTypeExtensionBuilder.apply(entry), entry.getType());

        register.accept(RPMFluids.DARK_MAGIC_POOL_WATER_FLUID);
        register.accept(RPMFluids.MAGIC_POOL_WATER_FLUID);
    }
}
