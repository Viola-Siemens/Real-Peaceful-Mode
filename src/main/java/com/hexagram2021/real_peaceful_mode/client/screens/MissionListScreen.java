package com.hexagram2021.real_peaceful_mode.client.screens;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.real_peaceful_mode.common.mission.MissionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class MissionListScreen extends Screen {
	private static final int MAX_MISSIONS_PER_SCREEN = 6;

	protected final int imageWidth = 176;
	protected final int imageHeight = 166;
	protected final int titleLabelX = 16;
	protected final int titleLabelY = 6;
	protected int leftPos;
	protected int topPos;

	private int beginIndex = 0;
	private float scrollOffs;
	private boolean showFinished = false;
	private boolean scrolling = false;

	public static final ResourceLocation BG_LOCATION = new ResourceLocation(MODID, "textures/gui/mission_list.png");

	private final List<MissionManager.Mission> activeMissions;
	private final List<MissionManager.Mission> finishedMissions;

	private List<MissionManager.Mission> shadows;

	public MissionListScreen(List<MissionManager.Mission> activeMissions, List<MissionManager.Mission> finishedMissions) {
		super(Component.translatable("title.real_peaceful_mode.menu.mission_list"));
		this.activeMissions = activeMissions;
		this.finishedMissions = finishedMissions;
		this.shadows = List.copyOf(this.activeMissions);
	}

	@Override
	protected void init() {
		this.leftPos = (this.width - this.imageWidth) / 2;
		this.topPos = (this.height - this.imageHeight) / 2;
	}

	@Override
	public void render(GuiGraphics transform, int x, int y, float ticks) {
		transform.drawString(this.font, this.title, this.leftPos + this.titleLabelX, this.topPos + this.titleLabelY, 0x404040);
		this.renderBg(transform, x, y);
		super.render(transform, x, y, ticks);
	}

	protected void renderBg(GuiGraphics transform, int x, int y) {
		transform.blit(BG_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		this.renderButtons(transform, x, y);
		this.renderMissions(transform);
	}

	private void renderButtons(GuiGraphics transform, int x, int y) {
		int buttonX = this.leftPos + 116;
		int buttonY = this.topPos + 6;
		boolean xInRange = x >= buttonX && x < buttonX + 54;
		boolean yInRange = y >= buttonY && y < buttonY + 18;
		int buttonHeight = (xInRange && yInRange) ? this.imageHeight + 36 : this.imageHeight;
		if(this.showFinished) {
			buttonHeight += 18;
		}
		transform.blit(BG_LOCATION, buttonX, buttonY, 0, buttonHeight, 54, 18);
	}

	private void renderMissions(GuiGraphics transform) {
		int bound = Math.min(this.shadows.size(), MAX_MISSIONS_PER_SCREEN);
		if(bound == 0) {
			transform.drawString(this.font, Component.translatable("gui.real_peaceful_mode.menu.mission_list.no_mission"), this.leftPos + 6, this.topPos + 38, 0xa0a0a0);
		} else {
			for (int i = 0; i < bound; ++i) {
				transform.blit(BG_LOCATION, this.leftPos + 6, this.topPos + 38 + 18 * i, 54, 166, 140, 18);
				ResourceLocation id = this.shadows.get(this.beginIndex + i).id();
				transform.drawString(this.font, Component.translatable("mission.%s.%s.name".formatted(id.getNamespace(), id.getPath())), this.leftPos + 8, this.topPos + 38 + 18 * i, 0xffffff);
				FormattedCharSequence description = Component.translatable("mission.%s.%s.description".formatted(id.getNamespace(), id.getPath())).getVisualOrderText();
				transform.drawString(this.font, description, this.leftPos + 6, this.topPos + 47 + 18 * i, 0xa0a0a0);
			}
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		this.scrolling = false;
		int buttonX = this.leftPos + 116;
		int buttonY = this.topPos + 6;
		if(y >= buttonY && y < buttonY + 18.0D) {
			if(x >= buttonX && x < buttonX + 54.0D) {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
				this.showFinished = !this.showFinished;
				if(this.showFinished) {
					ImmutableList.Builder<MissionManager.Mission> builder = ImmutableList.builder();
					builder.addAll(this.activeMissions);
					builder.addAll(this.finishedMissions);
					this.shadows = builder.build();
				} else {
					this.shadows = List.copyOf(this.activeMissions);
				}
				return true;
			}
		}
		buttonX = this.leftPos + 144;
		buttonY = this.topPos + 38;
		if (x >= buttonX && x < buttonX + 12 && y >= buttonY && y < buttonY + 108) {
			this.scrolling = true;
		}
		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean mouseDragged(double fromX, double fromY, int activeButton, double toX, double toY) {
		if (this.scrolling && this.isScrollBarActive()) {
			int i = this.topPos + 14;
			int j = i + 54;
			this.scrollOffs = ((float)fromY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
			this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
			this.beginIndex = (int)(this.scrollOffs * this.getScreenTotalScrollRows());
			return true;
		}
		return super.mouseDragged(fromX, fromY, activeButton, toX, toY);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double delta) {
		if (this.isScrollBarActive()) {
			int totalRows = this.getScreenTotalScrollRows();
			float f = (float)delta / totalRows;
			this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
			this.beginIndex = (int)(this.scrollOffs * totalRows);
		}

		return true;
	}

	private boolean isScrollBarActive() {
		return this.shadows.size() > MAX_MISSIONS_PER_SCREEN;
	}

	private int getScreenTotalScrollRows() {
		return this.shadows.size() - 5;
	}
}
