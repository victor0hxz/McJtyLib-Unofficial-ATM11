package mcjty.lib.crafting;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;

public abstract class AbstractRecipeAdaptor implements CraftingRecipe {
   private final ShapedRecipe recipe;

   public AbstractRecipeAdaptor(ShapedRecipe recipe) {
      this.recipe = recipe;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return this.recipe.getWidth() <= width && this.recipe.getHeight() <= height;
   }

   @Nonnull
   public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInput inv) {
      return this.recipe.getRemainingItems(inv);
   }

   @Nonnull
   public List<Optional<Ingredient>> getIngredients() {
      return this.recipe.getIngredients();
   }

   public boolean isSpecial() {
      return this.recipe.isSpecial();
   }

   @Nonnull
   public String group() {
      return this.recipe.group();
   }

   @Nonnull
   public String getGroup() {
      return this.group();
   }

   public ShapedRecipe getRecipe() {
      return this.recipe;
   }

   public boolean matches(@Nonnull CraftingInput inv, @Nonnull Level worldIn) {
      return this.recipe.matches(inv, worldIn);
   }

   public boolean showNotification() {
      return this.recipe.showNotification();
   }

   public CraftingBookCategory category() {
      return this.recipe.category();
   }

   public PlacementInfo placementInfo() {
      return this.recipe.placementInfo();
   }

   public List<RecipeDisplay> display() {
      return this.recipe.display();
   }

   public RecipeBookCategory recipeBookCategory() {
      return this.recipe.recipeBookCategory();
   }

   @Nonnull
   public RecipeType<CraftingRecipe> getType() {
      return this.recipe.getType();
   }
}
