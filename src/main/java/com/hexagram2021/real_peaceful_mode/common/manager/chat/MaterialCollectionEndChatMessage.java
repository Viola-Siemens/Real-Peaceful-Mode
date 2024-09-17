package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class MaterialCollectionEndChatMessage extends EndChatMessage {
	public static final MapCodec<MaterialCollectionEndChatMessage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(MaterialCollectionEndChatMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.NPC).forGetter(MaterialCollectionEndChatMessage::speaker),
			ResourceLocation.CODEC.xmap(id -> ResourceKey.create(Registries.LOOT_TABLE, id), ResourceKey::location).fieldOf("loot_table").forGetter(MaterialCollectionEndChatMessage::lootTable)
	).apply(instance, MaterialCollectionEndChatMessage::new));

	final ResourceKey<LootTable> lootTable;

	public MaterialCollectionEndChatMessage(String messageKey, Speaker speaker, ResourceKey<LootTable> lootTable) {
		super(messageKey, speaker);
		this.lootTable = lootTable;
	}

	@Override
	public IChatMessageType type() {
		return ChatMessageTypes.MATERIAL_COLLECTION_END;
	}

	@Override @Nonnull
	public BiConsumer<IMonsterHero, LivingEntity> onFinish() {
		return (hero, npc) -> {
			if(hero instanceof ServerPlayer serverPlayer && hero.rpm$endCollectingMaterial(this.lootTable().location())) {
				serverPlayer.serverLevel().getServer().reloadableRegistries().getLootTable(this.lootTable())
						.getRandomItems(
								new LootParams.Builder(serverPlayer.serverLevel()).create(LootContextParamSets.EMPTY),
								itemStack -> serverPlayer.level().addFreshEntity(
										new ItemEntity(serverPlayer.level(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), itemStack)
								)
						);
			}
		};
	}

	public ResourceKey<LootTable> lootTable() {
		return this.lootTable;
	}
}
