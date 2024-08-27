package com.hexagram2021.real_peaceful_mode.common.crafting.menu;

import com.hexagram2021.real_peaceful_mode.RealPeacefulMode;
import com.hexagram2021.real_peaceful_mode.common.crafting.ClientSideMessagedChat;
import com.hexagram2021.real_peaceful_mode.common.crafting.MessagedChat;
import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.AbstractChatMessage;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.hexagram2021.real_peaceful_mode.common.register.RPMMenuTypes;
import com.hexagram2021.real_peaceful_mode.network.ClientboundChatSelectionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ChatMessageMenu extends AbstractContainerMenu implements IMessageMenu {
	private MessagedChat chat;
	private AbstractChatMessage current;

	@Nullable
	private List<ChatSelection> cachedSelections = null;

	public ChatMessageMenu(int counter, Inventory inventory) {
		this(counter, new ClientSideMessagedChat(inventory.player));
	}

	public ChatMessageMenu(int counter, MessagedChat chat) {
		super(RPMMenuTypes.CHAT_MESSAGE_MENU.get(), counter);
		this.chat = chat;
		this.current = chat.message();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.chat.player().closerThan(this.chat.npc(), 24.0D);
	}

	public static final int GO_NEXT_BUTTON = 0x71;
	public static final int GET_SELECTION_BUTTON = 0x72;
	@Override
	public boolean clickMenuButton(Player player, int index) {
		if(index == GO_NEXT_BUTTON) {
			return this.goNext();
		}
		if(index == GET_SELECTION_BUTTON) {
			List<ChatSelection> chatSelections = this.current.getSelections();
			if(chatSelections != null) {
				this.doCacheSelections();
				return true;
			}
			return false;
		}
		List<ChatSelection> chatSelections = this.getCurrentChatSelections();
		if(chatSelections != null && index < chatSelections.size()) {
			this.current = chatSelections.get(index).getNext();
			this.cachedSelections = null;
			return true;
		}
		return super.clickMenuButton(player, index);
	}

	public MessagedChat getChat() {
		return this.chat;
	}

	@OnlyIn(Dist.CLIENT)
	public void setChat(MessagedChat chat) {
		this.chat = chat;
		this.current = chat.message();
	}

	public String getCurrentChatMessageKey() {
		return this.current.messageKey();
	}
	public Speaker getCurrentChatSpeaker() {
		return this.current.speaker();
	}

	@Nullable
	public List<ChatSelection> getCurrentChatSelections() {
		return this.cachedSelections;
	}

	public boolean goNext() {
		AbstractChatMessage next = this.current.getNext();
		if(next != null) {
			this.current = next;
			return true;
		}
		return false;
	}

	public void doCacheSelections() {
		if(this.chat.player() instanceof ServerPlayer serverPlayer) {
			if (this.current.getSelections() != null) {
				this.cachedSelections = this.current.getSelections().stream().filter(selection -> selection.canShowFor(serverPlayer)).collect(Collectors.toList());
			} else {
				this.cachedSelections = null;
			}
			RealPeacefulMode.packetHandler.send(
					PacketDistributor.PLAYER.with(() -> serverPlayer),
					new ClientboundChatSelectionPacket(this.cachedSelections)
			);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void setCachedSelections(@Nullable List<ChatSelection> chatSelections) {
		this.cachedSelections = chatSelections;
	}

	public LivingEntity getSpeaker(Speaker speaker) {
		return switch (speaker) {
			case PLAYER -> this.chat.player();
			case NPC -> this.chat.npc();
		};
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
	}
}
