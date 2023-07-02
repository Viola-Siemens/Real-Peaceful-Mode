package com.hexagram2021.real_peaceful_mode.client.screens;

import com.hexagram2021.real_peaceful_mode.common.crafting.menus.MissionMessageMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

@OnlyIn(Dist.CLIENT)
public class MissionMessageScreen extends AbstractContainerScreen<MissionMessageMenu> {
	private static final ResourceLocation BG_LOCATION = new ResourceLocation(MODID, "textures/gui/mission_message.png");

	private int messageIndex = 0;
	private int deltaIndex = 0;

	public MissionMessageScreen(MissionMessageMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		--this.titleLabelY;
	}

	@Override
	protected void renderBg(GuiGraphics transform, float ticks, int x, int y) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		transform.blit(BG_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
		FormattedCharSequence name = this.menu.getMission().messages().get(this.messageIndex).entityType().getDescription().getVisualOrderText();
		transform.drawString(this.font, name, 116 - this.font.width(name), 88, 0xa0a0a0);
	}

	private void renderButtons(GuiGraphics transform, int x, int y) {
		int buttonX1 = this.leftPos + 13;
		int buttonX2 = this.leftPos + 49;
		int buttonY = this.topPos + 78;
		boolean x1InRange = x >= buttonX1 && x < buttonX1 + 18;
		boolean x2InRange = x >= buttonX2 && x < buttonX2 + 18;
		boolean yInRange = y >= buttonY && y < buttonY + 18;
		int buttonHeightLeft = (x1InRange && yInRange) ? this.imageHeight + 18 : this.imageHeight;
		int buttonHeightRight = (x2InRange && yInRange) ? this.imageHeight + 18 : this.imageHeight;
		switch(this.deltaIndex) {
			case -1 -> buttonHeightLeft = this.imageHeight + 36;
			case 1 -> buttonHeightRight = this.imageHeight + 36;
		}
		transform.blit(BG_LOCATION, 13, 78, 0, buttonHeightLeft, 18, 18);
		transform.blit(BG_LOCATION, 49, 78, 0, buttonHeightRight, 18, 18);
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
				}
				return true;
			}
		}
		this.deltaIndex = 0;
		return super.mouseReleased(x, y, button);
	}
}
