package mcjty.lib.container;

import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class AutomationFilterItemHander implements IItemHandlerModifiable {
   private final GenericItemHandler wrapped;

   public AutomationFilterItemHander(GenericItemHandler wrapped) {
      this.wrapped = wrapped;
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.wrapped.setStackInSlot(slot, stack);
   }

   public int getSlots() {
      return this.wrapped.getSlots();
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return this.wrapped.getStackInSlot(slot);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      return !this.canAutomationInsert(slot) ? stack : this.wrapped.insertItem(slot, stack, simulate);
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return !this.canAutomationExtract(slot) ? ItemStack.EMPTY : this.wrapped.extractItem(slot, amount, simulate);
   }

   public int getSlotLimit(int slot) {
      return this.wrapped.getSlotLimit(slot);
   }

   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return this.wrapped.isItemValid(slot, stack);
   }

   public boolean canAutomationInsert(int slot) {
      return this.wrapped.getContainerFactory().isInputSlot(slot);
   }

   public boolean canAutomationExtract(int slot) {
      return this.wrapped.getContainerFactory().isOutputSlot(slot);
   }

   public CompoundTag serializeNBT(Provider provider) {
      return this.wrapped.serializeNBT(provider);
   }

   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      this.wrapped.deserializeNBT(provider, nbt);
   }
}
