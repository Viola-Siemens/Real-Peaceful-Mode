package com.hexagram2021.real_peaceful_mode.common.crafting.compat.jei;

import com.hexagram2021.real_peaceful_mode.common.crafting.recipe.CultureTableShadowRecipe;
import com.hexagram2021.real_peaceful_mode.common.register.RPMBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import static com.hexagram2021.real_peaceful_mode.RealPeacefulMode.MODID;

public class CultureTableShadowRecipeCategory implements IRecipeCategory<CultureTableShadowRecipe> {
	public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MODID, "shadow/culture_table");
	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/jei/culture_table.png");

	private final IDrawable background;
	private final IDrawable icon;

	public CultureTableShadowRecipeCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 106, 42);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RPMBlocks.WorkStation.CULTURE_TABLE));
	}

	@Override
	public Component getTitle() {
		return Component.translatable("container.culture_table");
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
	public RecipeType<CultureTableShadowRecipe> getRecipeType() {
		return JEIShadowPlugin.RPMJEIRecipeTypes.CULTURE_TABLE;
	}

	private static final ItemStack NECESSARY_INGREDIENT = new ItemStack(Items.GUNPOWDER);
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CultureTableShadowRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.mix1());
		builder.addSlot(RecipeIngredientRole.INPUT, 25, 1).addIngredients(recipe.mix2());
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 25).addItemStack(NECESSARY_INGREDIENT);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 19).addIngredients(recipe.result());
	}
}
