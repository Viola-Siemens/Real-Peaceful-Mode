package com.hexagram2021.real_peaceful_mode.client.screens;

import com.hexagram2021.real_peaceful_mode.common.crafting.menu.MissionMessageMenu;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@OnlyIn(Dist.CLIENT)
public class MissionMessageScreen extends AbstractContainerScreen<MissionMessageMenu> {
	private static final ResourceLocation BG_LOCATION = new ResourceLocation(MODID, "textures/gui/mission_message.png");

	private int messageIndex = 0;
	private int deltaIndex = 0;

	private List<FormattedCharSequence> cachedText;

	public MissionMessageScreen(MissionMessageMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		--this.titleLabelY;
		this.loadCachedText();
	}

	private void loadCachedText() {
		List<MissionManager.Mission.Message> messages = this.menu.getMission().messages();
		if(messages.size() > 0) {
			this.cachedText = this.font.split(Component.translatable(messages.get(this.messageIndex).messageKey()), 140);
		}
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
		List<MissionManager.Mission.Message> messages = this.menu.getMission().messages();
		if(messages.size() <= 0) {
			return;
		}
		MissionManager.Mission.Message message = messages.get(this.messageIndex);
		LivingEntity currentSpeaker = this.menu.getSpeaker(message.speaker());
		if(currentSpeaker != null) {
			FormattedCharSequence name = currentSpeaker.getDisplayName().getVisualOrderText();
			transform.drawString(this.font, name, i + 116 - this.font.width(name), j + 88, 0xa0a0a0);
			InventoryScreen.renderEntityInInventoryFollowsMouse(transform, i + 143, j + 151, 24, x, y, currentSpeaker);
		}
		if(this.cachedText == null || this.cachedText.size() <= 0) {
			this.loadCachedText();
		}
		for(int l = 0; l < this.cachedText.size(); ++l) {
			transform.drawString(this.font, this.cachedText.get(l), i + 16, j + 16 + l * 9, 0x404040, false);
		}
		this.renderButtons(transform, x, y);
	}

	private void renderButtons(GuiGraphics transform, int x, int y) {
		int buttonX1 = this.leftPos + 13;
		int buttonX2 = this.leftPos + 49;
		int buttonY = this.topPos + 78;
		boolean x1InRange = x >= buttonX1 && x < buttonX1 + 18;
		boolean x2InRange = x >= buttonX2 && x < buttonX2 + 18;
		boolean yInRange = y >= buttonY && y < buttonY + 18;
		int buttonHeightLeft = (x1InRange && yInRange) ? this.imageHeight + 36 : this.imageHeight;
		int buttonHeightRight = (x2InRange && yInRange) ? this.imageHeight + 36 : this.imageHeight;
		switch(this.deltaIndex) {
			case -1 -> buttonHeightLeft = this.imageHeight + 18;
			case 1 -> buttonHeightRight = this.imageHeight + 18;
		}
		transform.blit(BG_LOCATION, buttonX1, buttonY, 0, buttonHeightLeft, 18, 18);
		transform.blit(BG_LOCATION, buttonX2, buttonY, 18, buttonHeightRight, 18, 18);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
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
		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		double buttonX1 = this.leftPos + 13;
		double buttonX2 = this.leftPos + 49;
		double buttonY = this.topPos + 78;
		if(y >= buttonY && y < buttonY + 18.0D) {
			if((x >= buttonX1 && x < buttonX1 + 18.0D && this.deltaIndex == -1) ||
					(x >= buttonX2 && x < buttonX2 + 18.0D && this.deltaIndex == 1)) {
				this.messageIndex += this.deltaIndex;
				this.deltaIndex = 0;
				if(this.messageIndex < 0) {
					this.messageIndex = 0;
				} else if(this.messageIndex >= this.menu.getMission().messages().size()) {
					this.onClose();
					return true;
				}
				this.loadCachedText();
				return true;
			}
		}
		this.deltaIndex = 0;
		return super.mouseReleased(x, y, button);
	}
}
