package mcjty.lib.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import mcjty.lib.api.container.ItemInventory;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class GenericItemHandler implements IItemHandlerModifiable {
   private final GenericTileEntity tileEntity;
   private final ContainerFactory containerFactory;
   private final ItemStackHandler stacks;

   public static BiPredicate<Integer, ItemStack> slot(int s) {
      return (slot, stack) -> slot == s;
   }

   public static BiPredicate<Integer, ItemStack> notslot(int s) {
      return (slot, stack) -> slot != s;
   }

   public static BiPredicate<Integer, ItemStack> match(Supplier<? extends Item> itemSupplier) {
      Item item = itemSupplier.get();
      return (slot, stack) -> stack.getItem() == item;
   }

   public static BiPredicate<Integer, ItemStack> match(Item item) {
      return (slot, stack) -> stack.getItem() == item;
   }

   public static BiPredicate<Integer, ItemStack> no() {
      return (slot, stack) -> false;
   }

   public static BiPredicate<Integer, ItemStack> yes() {
      return (slot, stack) -> true;
   }

   public static GenericItemHandler.Builder create(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier) {
      return new GenericItemHandler.Builder(te, factorySupplier);
   }

   public static GenericItemHandler basic(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier) {
      return new GenericItemHandler(te, factorySupplier.get());
   }

   protected void onUpdate(int index, ItemStack stack) {
      this.tileEntity.markDirtyQuick();
   }

   public GenericItemHandler(GenericTileEntity te, ContainerFactory factory) {
      this.tileEntity = te;
      this.containerFactory = factory;
      this.stacks = new ItemStackHandler(this.containerFactory.getContainerSlots());
   }

   public void applyImplicitComponents(ItemInventory inventory) {
      if (inventory != null) {
         this.setStacks(inventory.items());
      }
   }

   public void collectImplicitComponents(net.minecraft.core.component.DataComponentMap.Builder builder) {
      List<ItemStack> stacks = new ArrayList<>();

      for (int i = 0; i < this.getSlots(); i++) {
         stacks.add(this.getStackInSlot(i));
      }

      builder.set(Registration.ITEM_INVENTORY, new ItemInventory(stacks));
   }

   public ItemStackHandler getStacks() {
      return this.stacks;
   }

   public void setStacks(List<ItemStack> items) {
      for (int i = 0; i < this.stacks.getSlots(); i++) {
         if (i < items.size()) {
            this.stacks.setStackInSlot(i, items.get(i));
         } else {
            this.stacks.setStackInSlot(i, ItemStack.EMPTY);
         }
      }
   }

   public int getSlots() {
      return this.stacks.getSlots();
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return slot >= this.stacks.getSlots() ? ItemStack.EMPTY : this.stacks.getStackInSlot(slot);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else if (!this.isItemInsertable(slot, stack)) {
         return stack;
      } else {
         ItemStack stackInSlot = this.getStackInSlot(slot);
         if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), this.getSlotLimit(slot))) {
               return stack;
            } else if (!ItemStack.isSameItemSameComponents(stack, stackInSlot)) {
               return stack;
            } else if (!this.isItemValid(slot, stack)) {
               return stack;
            } else {
               int m = Math.min(stack.getMaxStackSize(), this.getSlotLimit(slot)) - stackInSlot.getCount();
               if (stack.getCount() <= m) {
                  if (!simulate) {
                     ItemStack copy = stack.copy();
                     copy.grow(stackInSlot.getCount());
                     this.setInventorySlotContents(copy.getMaxStackSize(), slot, copy);
                     this.onUpdate(slot, copy);
                  }

                  return ItemStack.EMPTY;
               } else {
                  stack = stack.copy();
                  if (!simulate) {
                     ItemStack copy = stack.split(m);
                     copy.grow(stackInSlot.getCount());
                     this.setInventorySlotContents(copy.getMaxStackSize(), slot, copy);
                     this.onUpdate(slot, copy);
                     return stack;
                  } else {
                     stack.shrink(m);
                     return stack;
                  }
               }
            }
         } else if (!this.isItemValid(slot, stack)) {
            return stack;
         } else {
            int m = Math.min(stack.getMaxStackSize(), this.getSlotLimit(slot));
            if (m < stack.getCount()) {
               stack = stack.copy();
               if (!simulate) {
                  ItemStack split = stack.split(m);
                  this.setInventorySlotContents(stack.getMaxStackSize(), slot, split);
                  this.onUpdate(slot, split);
                  return stack;
               } else {
                  stack.shrink(m);
                  return stack;
               }
            } else {
               if (!simulate) {
                  this.setInventorySlotContents(stack.getMaxStackSize(), slot, stack);
                  this.onUpdate(slot, stack);
               }

               return ItemStack.EMPTY;
            }
         }
      }
   }

   public void setInventorySlotContents(int stackLimit, int index, ItemStack stack) {
      if (index < this.stacks.getSlots()) {
         if (this.containerFactory.isGhostSlot(index)) {
            if (!stack.isEmpty()) {
               ItemStack stack1 = stack.copy();
               if (index < 9) {
                  stack1.setCount(1);
               }

               this.stacks.setStackInSlot(index, stack1);
            } else {
               this.stacks.setStackInSlot(index, ItemStack.EMPTY);
            }
         } else if (this.containerFactory.isGhostOutputSlot(index)) {
            if (!stack.isEmpty()) {
               this.stacks.setStackInSlot(index, stack.copy());
            } else {
               this.stacks.setStackInSlot(index, ItemStack.EMPTY);
            }
         } else {
            this.stacks.setStackInSlot(index, stack);
            if (!stack.isEmpty() && stack.getCount() > stackLimit) {
               stack.setCount(Math.max(stackLimit, 0));
            }

            this.tileEntity.setChanged();
         }
      }
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (amount == 0) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stackInSlot = this.getStackInSlot(slot);
         if (stackInSlot.isEmpty()) {
            return ItemStack.EMPTY;
         } else if (!this.isItemExtractable(slot, stackInSlot)) {
            return ItemStack.EMPTY;
         } else if (simulate) {
            if (stackInSlot.getCount() < amount) {
               return stackInSlot.copy();
            } else {
               ItemStack copy = stackInSlot.copy();
               copy.setCount(amount);
               return copy;
            }
         } else {
            int m = Math.min(stackInSlot.getCount(), amount);
            ItemStack decrStackSize = this.decrStackSize(slot, m);
            this.onUpdate(slot, decrStackSize);
            return decrStackSize;
         }
      }
   }

   public ItemStack decrStackSize(int index, int amount) {
      if (index >= this.stacks.getSlots()) {
         return ItemStack.EMPTY;
      } else if (this.containerFactory.isGhostSlot(index) || this.containerFactory.isGhostOutputSlot(index)) {
         ItemStack old = this.stacks.getStackInSlot(index);
         this.stacks.setStackInSlot(index, ItemStack.EMPTY);
         if (old.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            old.setCount(0);
            return old;
         }
      } else if (!this.stacks.getStackInSlot(index).isEmpty()) {
         if (this.stacks.getStackInSlot(index).getCount() <= amount) {
            ItemStack old = this.stacks.getStackInSlot(index);
            this.stacks.setStackInSlot(index, ItemStack.EMPTY);
            this.tileEntity.setChanged();
            return old;
         } else {
            ItemStack its = this.stacks.getStackInSlot(index).split(amount);
            if (this.stacks.getStackInSlot(index).isEmpty()) {
               this.stacks.setStackInSlot(index, ItemStack.EMPTY);
            }

            this.tileEntity.setChanged();
            return its;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.stacks.setStackInSlot(slot, stack);
      this.onUpdate(slot, stack);
   }

   public int getSlotLimit(int slot) {
      return 64;
   }

   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return true;
   }

   public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
      return this.isItemValid(slot, stack);
   }

   public boolean isItemExtractable(int slot, @Nonnull ItemStack stack) {
      return true;
   }

   public ContainerFactory getContainerFactory() {
      return this.containerFactory;
   }

   public CompoundTag serializeNBT(Provider provider) {
      TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
      this.stacks.serialize(output);
      return output.buildResult();
   }

   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      this.stacks.deserialize(TagValueInput.create(ProblemReporter.DISCARDING, provider, nbt));
   }

   public void save(ValueOutput output, String tagName) {
      List<ItemStack> items = new ArrayList<>();

      for (int i = 0; i < this.getSlots(); i++) {
         items.add(this.getStackInSlot(i));
      }

      output.store(tagName, ItemInventory.ITEM_INVENTORY_CODEC, new ItemInventory(items));
   }

   public void load(ValueInput input, String tagName) {
      input.read(tagName, ItemInventory.ITEM_INVENTORY_CODEC).ifPresent(this::applyImplicitComponents);
   }

   public void save(CompoundTag tag, String tagName, Provider provider) {
      tag.put(tagName, this.serializeNBT(provider));
   }

   public void load(CompoundTag tag, String tagName, Provider provider) {
      if (tag.contains(tagName)) {
         this.deserializeNBT(provider, (CompoundTag)tag.getCompound(tagName).orElseGet(CompoundTag::new));
      }
   }

   public static class Builder {
      private final GenericTileEntity te;
      private final Supplier<ContainerFactory> factorySupplier;
      private BiPredicate<Integer, ItemStack> itemValid = (slot, stack) -> true;
      private BiPredicate<Integer, ItemStack> insertable = null;
      private BiPredicate<Integer, ItemStack> extractable = (slot, stack) -> true;
      private BiConsumer<Integer, ItemStack> onUpdate = (slot, stack) -> {};
      private Function<Integer, Integer> slotLimit = s -> 64;

      public Builder(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier) {
         this.te = te;
         this.factorySupplier = factorySupplier;
      }

      public GenericItemHandler.Builder slotLimit(int slotLimit) {
         this.slotLimit = s -> slotLimit;
         return this;
      }

      public GenericItemHandler.Builder slotLimit(Function<Integer, Integer> slotLimit) {
         this.slotLimit = slotLimit;
         return this;
      }

      public GenericItemHandler.Builder itemValid(BiPredicate<Integer, ItemStack> itemValid) {
         this.itemValid = itemValid;
         return this;
      }

      public GenericItemHandler.Builder insertable(BiPredicate<Integer, ItemStack> insertable) {
         this.insertable = insertable;
         return this;
      }

      public GenericItemHandler.Builder extractable(BiPredicate<Integer, ItemStack> extractable) {
         this.extractable = extractable;
         return this;
      }

      public GenericItemHandler.Builder onUpdate(BiConsumer<Integer, ItemStack> onUpdate) {
         this.onUpdate = onUpdate;
         return this;
      }

      public GenericItemHandler build() {
         return new GenericItemHandler(this.te, this.factorySupplier.get()) {
            {
               Objects.requireNonNull(Builder.this);
            }

            @Override
            public int getSlotLimit(int slot) {
               return Builder.this.slotLimit.apply(slot);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
               return Builder.this.itemValid.test(slot, stack);
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
               return Builder.this.insertable == null ? this.isItemValid(slot, stack) : Builder.this.insertable.test(slot, stack);
            }

            @Override
            public boolean isItemExtractable(int slot, @Nonnull ItemStack stack) {
               return Builder.this.extractable.test(slot, stack);
            }

            @Override
            protected void onUpdate(int index, ItemStack stack) {
               super.onUpdate(index, stack);
               Builder.this.onUpdate.accept(index, stack);
            }
         };
      }
   }
}
