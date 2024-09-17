package com.hexagram2021.real_peaceful_mode.common.manager.chat;

import com.hexagram2021.real_peaceful_mode.common.entity.IMonsterHero;
import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class MaterialCollectionStartChatMessage extends EndChatMessage {
	public static final MapCodec<MaterialCollectionStartChatMessage> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(MaterialCollectionStartChatMessage::messageKey),
			Speaker.CODEC.optionalFieldOf("speaker", Speaker.NPC).forGetter(MaterialCollectionStartChatMessage::speaker),
			ResourceLocation.CODEC.fieldOf("loot_table").forGetter(MaterialCollectionStartChatMessage::lootTable),
			Codec.LONG.fieldOf("duration").forGetter(MaterialCollectionStartChatMessage::duration)
	).apply(instance, MaterialCollectionStartChatMessage::new));

	final ResourceLocation lootTable;
	final long duration;

	public MaterialCollectionStartChatMessage(String messageKey, Speaker speaker, ResourceLocation lootTable, long duration) {
		super(messageKey, speaker);
		this.lootTable = lootTable;
		this.duration = duration;
	}

	@Override
	public IChatMessageType type() {
		return ChatMessageTypes.MATERIAL_COLLECTION_START;
	}

	@Override @Nonnull
	public BiConsumer<IMonsterHero, LivingEntity> onFinish() {
		return (hero, npc) -> hero.rpm$startCollectingMaterial(this.lootTable, npc, this.duration);
	}

	public ResourceLocation lootTable() {
		return this.lootTable;
	}
	public long duration() {
		return this.duration;
	}
}
