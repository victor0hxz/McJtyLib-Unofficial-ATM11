package mcjty.lib.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class UndoableItemHandler implements IItemHandlerModifiable {
   private final IItemHandlerModifiable handler;
   private final Map<Integer, ItemStack> undo = new HashMap<>();

   public UndoableItemHandler(IItemHandlerModifiable handler) {
      this.handler = handler;
   }

   public void remember(int slot) {
      if (!this.undo.containsKey(slot)) {
         this.undo.put(slot, this.handler.getStackInSlot(slot).copy());
      }
   }

   public void restore() {
      for (Entry<Integer, ItemStack> entry : this.undo.entrySet()) {
         this.handler.setStackInSlot(entry.getKey(), entry.getValue());
      }

      this.undo.clear();
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.remember(slot);
      this.handler.setStackInSlot(slot, stack);
   }

   public int getSlots() {
      return this.handler.getSlots();
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return this.handler.getStackInSlot(slot);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (!simulate) {
         this.remember(slot);
      }

      return this.handler.insertItem(slot, stack, simulate);
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (!simulate) {
         this.remember(slot);
      }

      return this.handler.extractItem(slot, amount, simulate);
   }

   public int getSlotLimit(int slot) {
      return this.handler.getSlotLimit(slot);
   }

   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return this.handler.isItemValid(slot, stack);
   }
}
