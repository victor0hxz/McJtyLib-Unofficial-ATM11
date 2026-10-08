package mcjty.lib.varia;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import mcjty.lib.api.fluids.ItemFluids;
import mcjty.lib.setup.Registration;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class CustomTank implements IFluidHandler, IFluidTank {
   @Nonnull
   protected FluidStack fluid = FluidStack.EMPTY;
   protected int capacity;

   public CustomTank(int capacity) {
      this.capacity = capacity;
   }

   public CustomTank setCapacity(int capacity) {
      this.capacity = capacity;
      return this;
   }

   public boolean isFluidValid(FluidStack stack) {
      return true;
   }

   public int getCapacity() {
      return this.capacity;
   }

   @Nonnull
   public FluidStack getFluid() {
      return this.fluid;
   }

   public int getFluidAmount() {
      return this.fluid.getAmount();
   }

   public void load(CompoundTag tag, String tagName, Provider provider) {
      if (tag.contains(tagName)) {
         CompoundTag nbt = (CompoundTag)tag.getCompound(tagName).orElseGet(CompoundTag::new);
         FluidStack fluid = FluidStack.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), nbt).result().orElse(FluidStack.EMPTY);
         this.setFluid(fluid);
      }
   }

   public void save(CompoundTag tag, String tagName, Provider provider) {
      if (!this.fluid.isEmpty()) {
         FluidStack.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.fluid).result().ifPresent(saved -> tag.put(tagName, saved));
      }
   }

   public void load(ValueInput input, String tagName) {
      this.setFluid(input.read(tagName, FluidStack.CODEC).orElse(FluidStack.EMPTY));
   }

   public void save(ValueOutput output, String tagName) {
      if (this.fluid.isEmpty()) {
         output.discard(tagName);
      } else {
         output.store(tagName, FluidStack.CODEC, this.fluid);
      }
   }

   public void applyImplicitComponents(ItemFluids itemFluids) {
      if (itemFluids != null && !itemFluids.fluids().isEmpty()) {
         this.fluid = itemFluids.fluids().get(0);
      }
   }

   public void collectImplicitComponents(Builder builder) {
      List<FluidStack> fluidStacks = new ArrayList<>();
      fluidStacks.add(this.fluid);
      builder.set(Registration.ITEM_FLUIDS, new ItemFluids(fluidStacks));
   }

   public int getTanks() {
      return 1;
   }

   @Nonnull
   public FluidStack getFluidInTank(int tank) {
      return this.getFluid();
   }

   public int getTankCapacity(int tank) {
      return this.getCapacity();
   }

   public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
      return this.isFluidValid(stack);
   }

   public int fill(FluidStack resource, FluidAction action) {
      if (resource.isEmpty() || !this.isFluidValid(resource)) {
         return 0;
      } else if (action.simulate()) {
         if (this.fluid.isEmpty()) {
            return Math.min(this.capacity, resource.getAmount());
         } else {
            return !FluidStack.isSameFluidSameComponents(this.fluid, resource) ? 0 : Math.min(this.capacity - this.fluid.getAmount(), resource.getAmount());
         }
      } else if (this.fluid.isEmpty()) {
         this.onContentsChanged();
         this.fluid = new FluidStack(resource.typeHolder(), Math.min(this.capacity, resource.getAmount()), resource.getComponentsPatch());
         return this.fluid.getAmount();
      } else if (!FluidStack.isSameFluidSameComponents(this.fluid, resource)) {
         return 0;
      } else {
         int filled = this.capacity - this.fluid.getAmount();
         if (resource.getAmount() < filled) {
            this.onContentsChanged();
            this.fluid.grow(resource.getAmount());
            filled = resource.getAmount();
         } else {
            this.onContentsChanged();
            this.fluid.setAmount(this.capacity);
         }

         return filled;
      }
   }

   @Nonnull
   public FluidStack drain(FluidStack resource, FluidAction action) {
      return !resource.isEmpty() && FluidStack.isSameFluidSameComponents(resource, this.fluid) ? this.drain(resource.getAmount(), action) : FluidStack.EMPTY;
   }

   @Nonnull
   public FluidStack drain(int maxDrain, FluidAction action) {
      int drained = maxDrain;
      if (this.fluid.getAmount() < maxDrain) {
         drained = this.fluid.getAmount();
      }

      FluidStack stack = new FluidStack(this.fluid.typeHolder(), drained, this.fluid.getComponentsPatch());
      if (action.execute()) {
         this.onContentsChanged();
         this.fluid.shrink(drained);
      }

      if (this.fluid.getAmount() <= 0) {
         this.fluid = FluidStack.EMPTY;
      }

      return stack;
   }

   protected void onContentsChanged() {
   }

   public void setFluid(@Nonnull FluidStack stack) {
      this.fluid = stack;
   }

   public boolean isEmpty() {
      return this.fluid.isEmpty();
   }

   public int getSpace() {
      return Math.max(0, this.capacity - this.fluid.getAmount());
   }
}
