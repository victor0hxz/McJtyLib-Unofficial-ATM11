package mcjty.lib.crafting;

import javax.annotation.Nonnull;
import mcjty.lib.setup.Registration;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CopyComponentsRecipe extends AbstractRecipeAdaptor {
   public CopyComponentsRecipe(ShapedRecipe recipe) {
      super(recipe);
   }

   public ItemStack assemble(CraftingInput inv) {
      ItemStack result = this.getRecipe().assemble(inv);

      for (int i = 0; i < inv.size(); i++) {
         ItemStack stack = inv.getItem(i);
         IComponentsToPreserve components = null;
         if (stack.getItem() instanceof IComponentsToPreserve ingredient) {
            components = ingredient;
         } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IComponentsToPreserve ingredient) {
            components = ingredient;
         }

         if (components != null) {
            for (DataComponentType type : components.getComponentsToPreserve()) {
               Object value = stack.get(type);
               if (value != null) {
                  result.set(type, value);
               }
            }
         }
      }

      return result;
   }

   @Nonnull
   public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
      return Registration.COPYNBT_SERIALIZER.get();
   }
}
