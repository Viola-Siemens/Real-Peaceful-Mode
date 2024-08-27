package com.hexagram2021.real_peaceful_mode.network;

import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.client.ScreenManager;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ClientboundChatSelectionPacket implements IRPMPacket {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private final Optional<List<ChatSelection>> chatSelections;

	public ClientboundChatSelectionPacket(@Nullable List<ChatSelection> chatSelections) {
		this.chatSelections = Optional.ofNullable(chatSelections);
	}

	public ClientboundChatSelectionPacket(FriendlyByteBuf buf) {
		this.chatSelections = buf.readOptional(buf1 -> buf1.readCollection(
				Lists::newArrayListWithCapacity,
				buf2 -> ChatSelection.CODEC.parse(NbtOps.INSTANCE, buf2.readNbt()).getOrThrow(false, RPMLogger::error)
		));
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeOptional(this.chatSelections, (buf1, selections) -> buf1.writeCollection(
				selections,
				(buf2, selection) -> buf2.writeNbt((CompoundTag) ChatSelection.CODEC.encode(selection, NbtOps.INSTANCE, new CompoundTag()).getOrThrow(false, RPMLogger::error))
		));
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ScreenManager.updateChatSelections(this.chatSelections.orElse(null));
	}
}
