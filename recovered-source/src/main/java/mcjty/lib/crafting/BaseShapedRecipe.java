package mcjty.lib.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.CraftingRecipe.CraftingBookInfo;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;

public class BaseShapedRecipe extends ShapedRecipe {
   public BaseShapedRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack stack) {
      super(new CommonInfo(false), new CraftingBookInfo(category, group), pattern, ItemStackTemplate.fromNonEmptyStack(stack));
   }

   public ItemStack assemble(CraftingInput input) {
      return super.assemble(input);
   }
}
