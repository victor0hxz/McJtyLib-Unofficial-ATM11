package mcjty.lib.container;

import javax.annotation.Nonnull;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BaseSlot extends SlotItemHandler {
   private final GenericTileEntity te;

   public BaseSlot(IItemHandler inventory, GenericTileEntity te, int index, int x, int y) {
      super(inventory, index, x, y);
      this.te = te;
   }

   public void set(@Nonnull ItemStack stack) {
      if (this.te != null) {
         this.te.onSlotChanged(this.getSlotIndex(), stack);
      }

      super.set(stack);
   }

   public GenericTileEntity getTe() {
      return this.te;
   }
}
