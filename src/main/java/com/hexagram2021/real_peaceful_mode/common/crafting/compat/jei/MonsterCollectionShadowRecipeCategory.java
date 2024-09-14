package com.hexagram2021.real_peaceful_mode.common.crafting.compat.jei;

import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.MonsterCollectionShadowRecipe;
import com.hexagram2021.real_peaceful_mode.common.register.RPMItems;
import com.hexagram2021.real_peaceful_mode.common.util.RPMLogger;
import com.hexagram2021.real_peaceful_mode.common.util.RegistryHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class MonsterCollectionShadowRecipeCategory implements IRecipeCategory<MonsterCollectionShadowRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(MODID, "shadow/monster_collection");
	public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/gui/jei/monster_collection.png");

	protected static final int ENTITY_X = 116;
	protected static final int ENTITY_Y = 6;
	protected static final int ENTITY_W = 47;
	protected static final int ENTITY_H = 71;

	private final IDrawable background;
	private final IDrawable icon;

	public MonsterCollectionShadowRecipeCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 166, 120);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RPMItems.DebugItems.CREEPERS_WISH));
	}

	@Override
	public Component getTitle() {
		return Component.translatable("container.monster_collection");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public RecipeType<MonsterCollectionShadowRecipe> getRecipeType() {
		return JEIShadowPlugin.RPMJEIRecipeTypes.MONSTER_COLLECTION;
	}

	@Override
	public void draw(MonsterCollectionShadowRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics transform, double mouseX, double mouseY) {
		if(recipe.getRenderEntity() instanceof LivingEntity living) {
			renderEntityInCategory(transform.pose(), ENTITY_X, ENTITY_Y, ENTITY_W, ENTITY_H, mouseX, mouseY, living);
			this.drawName(living.getName().getString(), transform);
		} else {
			RPMLogger.warn("Failed to create render entity for " + RegistryHelper.getRegistryName(recipe.entityType()) + ".");
		}
	}

	protected void drawName(String name, GuiGraphics transform) {
		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		int stringWidth = fontRenderer.width(name);
		transform.drawString(fontRenderer, name, 108 - stringWidth, 60, 0xFF808080);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MonsterCollectionShadowRecipe recipe, IFocusGroup focuses) {
		List<Item> results = recipe.results();
		for(int i = 0; i < 3; ++i) {
			if(i >= results.size()) {
				return;
			}
			builder.addSlot(RecipeIngredientRole.OUTPUT, 9 + 26 * i, 9).addItemStack(new ItemStack(results.get(i)));
		}
		for(int i = 0; i < 3; ++i) {
			if(i + 3 >= results.size()) {
				return;
			}
			builder.addSlot(RecipeIngredientRole.OUTPUT, 9 + 26 * i, 35).addItemStack(new ItemStack(results.get(i + 3)));
		}
	}

	@SuppressWarnings("deprecation")
	public static void renderEntityInCategory(PoseStack transform, int x, int y, int w, int h, double mouseX, double mouseY, LivingEntity livingEntity) {
		float scale = h / 2.4f;
		int cx = x + (w / 2);
		int cy = y + (h / 2);
		int by = y + h;
		int offsetY = 4;

		float headYaw = (float) Math.atan((cx - mouseX) / 40.0F) * 40.0F;
		float yaw = (float) Math.atan((cx - mouseX) / 40.0F) * 20.0F;
		float pitch = (float) Math.atan((cy - offsetY - mouseY) / 40.0F) * 20.0F;

		double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
		double[] xyzTranslation = getGLTranslation(transform, guiScale);
		x = (int) (x * guiScale);
		y = (int) (y * guiScale);
		w = (int) (w * guiScale);
		h = (int) (h * guiScale);
		int scissorX = Math.round(Math.round(xyzTranslation[0] + x));
		int scissorY = Math.round(Math.round(Minecraft.getInstance().getWindow().getScreenHeight() - y - h - xyzTranslation[1]));
		RenderSystem.enableScissor(scissorX, scissorY, w, h);

		final Minecraft mc = Minecraft.getInstance();
		transform.pushPose();
		transform.translate(cx, by - offsetY, 1050.0F);
		transform.scale(1.0F, 1.0F, -1.0F);
		transform.translate(0.0D, 0.0D, 1000.0D);
		transform.scale(scale, scale, scale);
		Quaternionf pitchRotation = Axis.XP.rotationDegrees(pitch);
		transform.mulPose(Axis.ZP.rotationDegrees(180.0F));
		transform.mulPose(pitchRotation);
		float oldYawOffset = livingEntity.yBodyRot;
		float oldYaw = livingEntity.getYRot();
		float oldPitch = livingEntity.getXRot();
		float oldPrevYawHead = livingEntity.yHeadRotO;
		float oldYawHead = livingEntity.yHeadRot;
		livingEntity.yBodyRot = 180.0F + yaw;
		livingEntity.setYRot(180.0F + headYaw);
		livingEntity.setXRot(-pitch);
		livingEntity.yHeadRot = livingEntity.getYRot();
		livingEntity.yHeadRotO = livingEntity.getYRot();

		EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
		pitchRotation = pitchRotation.conjugate();
		dispatcher.overrideCameraOrientation(pitchRotation);
		dispatcher.setRenderShadow(false);

		MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> dispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, transform, buffers, 0x00F000F0));
		buffers.endBatch();
		dispatcher.setRenderShadow(true);

		livingEntity.yBodyRot = oldYawOffset;
		livingEntity.setYRot(oldYaw);
		livingEntity.setXRot(oldPitch);
		livingEntity.yHeadRotO = oldPrevYawHead;
		livingEntity.yHeadRot = oldYawHead;
		transform.popPose();

		RenderSystem.disableScissor();
	}

	private static double[] getGLTranslation(PoseStack transform, double scale) {
		final Matrix4f matrix = transform.last().pose();
		final FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		matrix.get(buf);
		return new double[] {
				buf.get(getIndexFloatBuffer(0, 3)) * scale,
				buf.get(getIndexFloatBuffer(1, 3)) * scale,
				buf.get(getIndexFloatBuffer(2, 3)) * scale
		};
	}

	@SuppressWarnings("SameParameterValue")
	private static int getIndexFloatBuffer(int x, int y) {
		return y * 4 + x;
	}
}
