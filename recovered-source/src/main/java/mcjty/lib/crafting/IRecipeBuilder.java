package mcjty.lib.crafting;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public interface IRecipeBuilder<T extends IRecipeBuilder<T>> {
   T define(Character var1, TagKey<Item> var2);

   T define(Character var1, ItemLike var2);

   T define(Character var1, Ingredient var2);

   T patternLine(String var1);

   T setGroup(String var1);

   void build(RecipeOutput var1);

   void build(RecipeOutput var1, String var2);

   void build(RecipeOutput var1, Identifier var2);
}
