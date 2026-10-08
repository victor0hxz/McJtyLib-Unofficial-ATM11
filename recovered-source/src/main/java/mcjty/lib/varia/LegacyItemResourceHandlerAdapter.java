package mcjty.lib.varia;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class LegacyItemResourceHandlerAdapter extends SnapshotJournal<List<ItemStack>> implements ResourceHandler<ItemResource> {
   private final IItemHandler handler;
   private final IItemHandlerModifiable modifiable;

   public LegacyItemResourceHandlerAdapter(IItemHandler handler) {
      this.handler = Objects.requireNonNull(handler);
      if (handler instanceof IItemHandlerModifiable modifiable) {
         this.modifiable = modifiable;
      } else {
         throw new IllegalArgumentException("RFTools native item bridge requires IItemHandlerModifiable");
      }
   }

   public int size() {
      return this.handler.getSlots();
   }

   public ItemResource getResource(int index) {
      Objects.checkIndex(index, this.size());
      return ItemResource.of(this.handler.getStackInSlot(index));
   }

   public long getAmountAsLong(int index) {
      Objects.checkIndex(index, this.size());
      return this.handler.getStackInSlot(index).getCount();
   }

   public long getCapacityAsLong(int index, ItemResource resource) {
      Objects.checkIndex(index, this.size());
      if (resource.isEmpty()) {
         return this.handler.getSlotLimit(index);
      } else {
         return !this.isValid(index, resource) ? 0L : Math.min(this.handler.getSlotLimit(index), resource.getMaxStackSize());
      }
   }

   public boolean isValid(int index, ItemResource resource) {
      Objects.checkIndex(index, this.size());
      TransferPreconditions.checkNonEmpty(resource);
      return this.handler.isItemValid(index, resource.toStack());
   }

   public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
      Objects.checkIndex(index, this.size());
      TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
      if (amount == 0) {
         return 0;
      } else {
         int tryAmount = Math.min(amount, Math.min(this.handler.getSlotLimit(index), resource.getMaxStackSize()));
         if (tryAmount <= 0) {
            return 0;
         } else {
            ItemStack requested = resource.toStack(tryAmount);
            ItemStack remaining = this.handler.insertItem(index, requested, true);
            int insertable = tryAmount - remaining.getCount();
            if (insertable <= 0) {
               return 0;
            } else {
               this.updateSnapshots(transaction);
               ItemStack actualRemaining = this.handler.insertItem(index, resource.toStack(insertable), false);
               return insertable - actualRemaining.getCount();
            }
         }
      }
   }

   public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
      Objects.checkIndex(index, this.size());
      TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
      if (amount == 0) {
         return 0;
      } else {
         ItemStack current = this.handler.getStackInSlot(index);
         if (!current.isEmpty() && resource.matches(current)) {
            ItemStack simulated = this.handler.extractItem(index, amount, true);
            if (!simulated.isEmpty() && resource.matches(simulated)) {
               int extractable = Math.min(amount, simulated.getCount());
               if (extractable <= 0) {
                  return 0;
               } else {
                  this.updateSnapshots(transaction);
                  ItemStack extracted = this.handler.extractItem(index, extractable, false);
                  return !extracted.isEmpty() && resource.matches(extracted) ? extracted.getCount() : 0;
               }
            } else {
               return 0;
            }
         } else {
            return 0;
         }
      }
   }

   protected List<ItemStack> createSnapshot() {
      List<ItemStack> snapshot = new ArrayList<>(this.handler.getSlots());

      for (int i = 0; i < this.handler.getSlots(); i++) {
         snapshot.add(this.handler.getStackInSlot(i).copy());
      }

      return snapshot;
   }

   protected void revertToSnapshot(List<ItemStack> snapshot) {
      int slots = Math.min(this.modifiable.getSlots(), snapshot.size());

      for (int i = 0; i < slots; i++) {
         this.modifiable.setStackInSlot(i, snapshot.get(i).copy());
      }

      for (int i = slots; i < this.modifiable.getSlots(); i++) {
         this.modifiable.setStackInSlot(i, ItemStack.EMPTY);
      }
   }
}
