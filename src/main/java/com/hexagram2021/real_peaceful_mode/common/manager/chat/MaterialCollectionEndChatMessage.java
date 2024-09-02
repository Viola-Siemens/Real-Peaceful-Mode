package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class MaterialCollectionEndChatMessage extends EndChatMessage {
	public static final Codec<MaterialCollectionEndChatMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(MaterialCollectionEndChatMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.NPC).forGetter(MaterialCollectionEndChatMessage::speaker),
			ResourceLocation.CODEC.fieldOf("loot_table").forGetter(MaterialCollectionEndChatMessage::lootTable)
	).apply(instance, MaterialCollectionEndChatMessage::new));

	final ResourceLocation lootTable;

	public MaterialCollectionEndChatMessage(String messageKey, Speaker speaker, ResourceLocation lootTable) {
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
			if(hero instanceof ServerPlayer serverPlayer && hero.rpm$endCollectingMaterial(this.lootTable())) {
				serverPlayer.serverLevel().getServer().getLootData().getLootTable(this.lootTable())
						.getRandomItems(
								new LootParams.Builder(serverPlayer.serverLevel()).create(LootContextParamSets.EMPTY),
								itemStack -> serverPlayer.level().addFreshEntity(
										new ItemEntity(serverPlayer.level(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), itemStack)
								)
						);
			}
		};
	}

	public ResourceLocation lootTable() {
		return this.lootTable;
	}
}
