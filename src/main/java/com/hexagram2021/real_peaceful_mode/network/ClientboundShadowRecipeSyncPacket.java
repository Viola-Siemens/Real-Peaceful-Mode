package com.hexagram2021.real_peaceful_mode.network;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.config.RPMCommonConfig;
import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.MonsterCollectionShadowRecipe;
import com.hexagram2021.real_peaceful_mode.common.register.RPMRecipeSerializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;

public class ClientboundShadowRecipeSyncPacket implements IRPMPacket {
	private final List<MonsterCollectionShadowRecipe> monsterCollectionShadowRecipes;

	public ClientboundShadowRecipeSyncPacket(List<MonsterCollectionShadowRecipe> monsterCollectionShadowRecipes) {
		this.monsterCollectionShadowRecipes = monsterCollectionShadowRecipes;
	}

	public ClientboundShadowRecipeSyncPacket(FriendlyByteBuf buf) {
		this.monsterCollectionShadowRecipes = buf.readCollection(Lists::newArrayListWithCapacity, friendlyByteBuf -> {
			ResourceLocation id = friendlyByteBuf.readResourceLocation();
			return RPMRecipeSerializers.MONSTER_COLLECTION_SHADOW_SERIALIZER.get().fromNetwork(id, friendlyByteBuf);
		});
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeCollection(this.monsterCollectionShadowRecipes, (friendlyByteBuf, recipe) -> {
			friendlyByteBuf.writeResourceLocation(recipe.getId());
			RPMRecipeSerializers.MONSTER_COLLECTION_SHADOW_SERIALIZER.get().toNetwork(friendlyByteBuf, recipe);
		});
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		if(RPMCommonConfig.ENABLE_JEI_SHADOW_RECIPE.get()) {
			MonsterCollectionShadowRecipe.setMonsterCollectionRecipes(this.monsterCollectionShadowRecipes);
		}
	}
}
