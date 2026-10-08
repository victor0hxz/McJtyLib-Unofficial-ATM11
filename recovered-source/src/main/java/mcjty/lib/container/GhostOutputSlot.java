package mcjty.lib.container;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GhostOutputSlot extends SlotItemHandler {
   public GhostOutputSlot(IItemHandler inventory, int index, int x, int y) {
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
      return 64;
   }

   public boolean mayPlace(@Nonnull ItemStack stack) {
      return false;
   }
}
