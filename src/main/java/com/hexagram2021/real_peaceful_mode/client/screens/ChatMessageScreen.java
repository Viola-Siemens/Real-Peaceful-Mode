package com.hexagram2021.real_peaceful_mode.client.screens;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hexagram2021.real_peaceful_mode.common.crafting.ClientSideMessagedChat;
import com.hexagram2021.real_peaceful_mode.common.crafting.menu.ChatMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.manager.Speaker;
import com.hexagram2021.real_peaceful_mode.common.manager.chat.selection.ChatSelection;
import com.hexagram2021.real_peaceful_mode.common.manager.mission.MissionMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@OnlyIn(Dist.CLIENT)
public class ChatMessageScreen extends AbstractContainerScreen<ChatMessageMenu> {
	private static final ResourceLocation BG_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/mission_message.png");

	private final List<MissionMessage> cachedMessages = Lists.newArrayList();
	@Nullable
	private List<String> selections = null;
	private int oldMessageIndex = 0;
	private int deltaIndex = 0;
	private int selectIndex = -1;

	@Nullable
	private List<FormattedCharSequence> cachedText;

	public ChatMessageScreen(ChatMessageMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		--this.titleLabelY;
	}

	private void cacheOldSelection(int index) {
		assert this.selections != null;
		this.cachedMessages.add(new MissionMessage(this.selections.get(index), Speaker.PLAYER));
		this.selections = null;
	}
	private void cacheNewMessage() {
		this.cachedMessages.add(new MissionMessage(this.menu.getCurrentChatMessageKey(), this.menu.getCurrentChatSpeaker()));
		this.selections = null;
	}

	private void loadCachedText() {
		this.cachedText = this.font.split(Component.translatable(this.cachedMessages.get(this.oldMessageIndex).messageKey()), 140);
	}

	@Override
	protected void renderLabels(GuiGraphics transform, int x, int y) {
		transform.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float ticks, int x, int y) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		transform.blit(BG_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
		if(this.selections == null) {
			Collection<ChatSelection> chatSelections = this.menu.getCurrentChatSelections();
			if(chatSelections != null) {
				this.selections = chatSelections.stream().map(ChatSelection::messageKey).collect(Collectors.toList());
			} else if(this.cachedMessages.size() > this.oldMessageIndex) {
				MissionMessage message = this.cachedMessages.get(this.oldMessageIndex);
				LivingEntity currentSpeaker = this.menu.getSpeaker(message.speaker());
				this.drawSpeaker(transform, currentSpeaker, x, y);

				if(this.cachedText != null) {
					for (int l = 0; l < this.cachedText.size(); ++l) {
						transform.drawString(this.font, this.cachedText.get(l), i + 16, j + 16 + l * 9, 0x404040, false);
					}
				}
			} else if(this.cachedText == null && !(this.menu.getChat() instanceof ClientSideMessagedChat)) {
				//First time client receive packet
				this.cacheNewMessage();
				this.loadCachedText();
			}
		}
		if(this.selections != null) {
			for(int l = 0; l < this.selections.size(); ++l) {
				this.drawSelectionButton(transform, x, y, l);
				transform.drawString(this.font, Component.translatable(this.selections.get(l)), i + 16, j + 16 + l * 10, 0x404040, false);
			}
			this.drawSpeaker(transform, this.menu.getChat().player(), x, y);
		}

		this.renderButtons(transform, x, y);
	}

	private void drawSpeaker(GuiGraphics transform, LivingEntity currentSpeaker, int x, int y) {
		FormattedCharSequence name = currentSpeaker.getDisplayName().getVisualOrderText();
		transform.drawString(this.font, name, this.leftPos + 116 - this.font.width(name), this.topPos + 100, 0xa0a0a0);
		InventoryScreen.renderEntityInInventoryFollowsMouse(transform, this.leftPos + 120, this.topPos + 88, this.leftPos + 167, this.topPos + 159, 24, 0.0625F, this.leftPos + 143 - x, this.topPos + 120 - y, currentSpeaker);
	}

	private void drawSelectionButton(GuiGraphics transform, int x, int y, int index) {
		int buttonX = this.leftPos + 16;
		int buttonY = this.topPos + 16 + index * 10;
		boolean xInRange = x >= buttonX && x < buttonX + 140;
		boolean yInRange = y >= buttonY && y < buttonY + 10;
		int buttonBlitHeight = (xInRange && yInRange) ? this.imageHeight + 10 : this.imageHeight;
		if(this.selectIndex == index) {
			buttonBlitHeight = this.imageHeight + 20;
		}
		transform.blit(BG_LOCATION, buttonX, buttonY, 36, buttonBlitHeight, 140, 10);
	}

	private void renderButtons(GuiGraphics transform, int x, int y) {
		int buttonX1 = this.leftPos + 13;
		int buttonX2 = this.leftPos + 49;
		int buttonY = this.topPos + 78;
		boolean x1InRange = x >= buttonX1 && x < buttonX1 + 18;
		boolean x2InRange = x >= buttonX2 && x < buttonX2 + 18;
		boolean yInRange = y >= buttonY && y < buttonY + 18;
		int buttonBlitHeightLeft = (x1InRange && yInRange) ? this.imageHeight + 36 : this.imageHeight;
		int buttonBlitHeightRight = (x2InRange && yInRange) ? this.imageHeight + 36 : this.imageHeight;
		switch(this.deltaIndex) {
			case -1 -> buttonBlitHeightLeft = this.imageHeight + 18;
			case 1 -> buttonBlitHeightRight = this.imageHeight + 18;
		}
		transform.blit(BG_LOCATION, buttonX1, buttonY, 0, buttonBlitHeightLeft, 18, 18);
		transform.blit(BG_LOCATION, buttonX2, buttonY, 18, buttonBlitHeightRight, 18, 18);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		//left/right
		double buttonX1 = this.leftPos + 13;
		double buttonX2 = this.leftPos + 49;
		double buttonY = this.topPos + 78;
		if(y >= buttonY && y < buttonY + 18.0D) {
			if(x >= buttonX1 && x < buttonX1 + 18.0D) {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
				this.deltaIndex = -1;
				return true;
			}
			if(x >= buttonX2 && x < buttonX2 + 18.0D) {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
				this.deltaIndex = 1;
				return true;
			}
		}

		//selection
		if(this.selections != null) {
			int buttonX = this.leftPos + 16;
			if(x >= buttonX && x < buttonX + 140.0D) {
				for (int i = 0; i < this.selections.size(); ++i) {
					buttonY = this.topPos + 16 + i * 10;
					if(y >= buttonY && y < buttonY + 10.0D) {
						Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
						this.selectIndex = i;
						return true;
					}
				}
			}
		}


		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		//left/right
		double buttonX1 = this.leftPos + 13;
		double buttonX2 = this.leftPos + 49;
		double buttonY = this.topPos + 78;
		if(y >= buttonY && y < buttonY + 18.0D) {
			if((x >= buttonX1 && x < buttonX1 + 18.0D && this.deltaIndex == -1) ||
					(x >= buttonX2 && x < buttonX2 + 18.0D && this.deltaIndex == 1)) {
				this.oldMessageIndex += this.deltaIndex;
				this.deltaIndex = 0;
				if(this.oldMessageIndex < 0) {
					this.oldMessageIndex = 0;
				} else {
					while(this.oldMessageIndex >= this.cachedMessages.size()) {
						if (this.menu.clickMenuButton(this.minecraft.player, ChatMessageMenu.GO_NEXT_BUTTON)) {
							this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, ChatMessageMenu.GO_NEXT_BUTTON);
							this.cacheNewMessage();
							this.loadCachedText();
						} else if(this.menu.clickMenuButton(this.minecraft.player, ChatMessageMenu.GET_SELECTION_BUTTON)) {
							this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, ChatMessageMenu.GET_SELECTION_BUTTON);
							this.oldMessageIndex = this.cachedMessages.size();
							this.selectIndex = -1;
							this.cachedText = ImmutableList.of();
							break;
						} else {
							this.onClose();
							return true;
						}
					}
				}
				if(this.oldMessageIndex < this.cachedMessages.size()) {
					this.loadCachedText();
				}
				return true;
			}
		}
		this.deltaIndex = 0;

		//selection
		if(this.selections != null) {
			int buttonX = this.leftPos + 16;
			if(x >= buttonX && x < buttonX + 140.0D) {
				for (int i = 0; i < this.selections.size(); ++i) {
					buttonY = this.topPos + 16 + i * 10;
					if(y >= buttonY && y < buttonY + 10.0D && this.selectIndex == i && this.menu.clickMenuButton(this.minecraft.player, i)) {
						this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
						Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F));
						this.oldMessageIndex += 1;
						this.cacheOldSelection(i);
						this.cacheNewMessage();
						this.loadCachedText();
						return true;
					}
				}
			}
		}

		return super.mouseReleased(x, y, button);
	}
}
