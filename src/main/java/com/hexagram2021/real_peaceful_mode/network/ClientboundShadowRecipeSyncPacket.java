package com.hexagram2021.real_peaceful_mode.network;

import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.MonsterCollectionShadowRecipe;
import com.hexagram2021.real_peaceful_mode.common.register.RPMRecipeSerializers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public record ClientboundShadowRecipeSyncPacket(List<MonsterCollectionShadowRecipe> monsterCollectionShadowRecipes) implements CustomPacketPayload, IRPMPacket {
	public static final CustomPacketPayload.Type<ClientboundShadowRecipeSyncPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "shadow_recipe_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundShadowRecipeSyncPacket> STREAM_CODEC = StreamCodec.composite(
			RPMRecipeSerializers.MONSTER_COLLECTION_SHADOW_SERIALIZER.get().streamCodec().apply(ByteBufCodecs.list()), ClientboundShadowRecipeSyncPacket::monsterCollectionShadowRecipes,
			ClientboundShadowRecipeSyncPacket::new
	);

	@Override
	public void handle(IPayloadContext context) {
		if(RPMCommonConfig.ENABLE_JEI_SHADOW_RECIPE.get()) {
			MonsterCollectionShadowRecipe.setMonsterCollectionRecipes(this.monsterCollectionShadowRecipes);
		}
	}

	@Override
	public Type<ClientboundShadowRecipeSyncPacket> type() {
		return TYPE;
	}
}
