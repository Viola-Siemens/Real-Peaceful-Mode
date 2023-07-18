package com.hexagram2021.real_peaceful_mode.client.screens;

import com.hexagram2021.real_peaceful_mode.common.crafting.menu.CultureTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class CultureTableScreen extends AbstractContainerScreen<CultureTableMenu> {
	private static final ResourceLocation CULTURE_TABLE_LOCATION = new ResourceLocation(MODID, "textures/gui/culture_table.png");

	public CultureTableScreen(CultureTableMenu menu, Inventory inventory, Component component) {
		super(menu, inventory, component);
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	public void render(GuiGraphics transform, int x, int y, float partialTicks) {
		this.renderBackground(transform);
		super.render(transform, x, y, partialTicks);
		this.renderTooltip(transform, x, y);
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partialTicks, int x, int y) {
		int left = (this.width - this.imageWidth) / 2;
		int top = (this.height - this.imageHeight) / 2;
		transform.blit(CULTURE_TABLE_LOCATION, left, top, 0, 0, this.imageWidth, this.imageHeight);
		int boneMealAmount = this.menu.getBoneMealAmount();
		if (boneMealAmount > 0) {
			transform.blit(CULTURE_TABLE_LOCATION, left + 118, top + 20, 176, 0, boneMealAmount, 4);
		}

		int analyzeProgress = this.menu.getAnalyzeProgress();
		if (analyzeProgress > 0) {
			transform.blit(CULTURE_TABLE_LOCATION, left + 79, top + 34, 176, 4, analyzeProgress, 17);
		}
	}
}
