package mcjty.lib.container;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GhostSlot extends SlotItemHandler {
   public GhostSlot(IItemHandler inventory, int index, int x, int y) {
      super(inventory, index, x, y);
   }

   public boolean mayPickup(Player player) {
      return false;
   }

   @Nonnull
   public ItemStack remove(int amount) {
      return ItemStack.EMPTY;
   }

   public int getMaxStackSize() {
      return 0;
   }

   public int getMaxStackSize(@Nonnull ItemStack stack) {
      return 1;
   }

   public boolean mayPlace(@Nonnull ItemStack stack) {
      return true;
   }

   public void set(ItemStack stack) {
      if (!stack.isEmpty()) {
         stack.setCount(1);
      }

      super.set(stack);
   }
}
