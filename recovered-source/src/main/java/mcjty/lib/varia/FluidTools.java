package mcjty.lib.varia;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidTools {
   @Nonnull
   public static ItemStack convertFluidToBucket(@Nonnull FluidStack fluidStack) {
      return FluidUtil.getFluidHandler(new ItemStack(Items.BUCKET)).map(handler -> {
         handler.fill(fluidStack, FluidAction.EXECUTE);
         return handler.getContainer();
      }).orElse(ItemStack.EMPTY);
   }

   @Nonnull
   public static FluidStack convertBucketToFluid(@Nonnull ItemStack bucket) {
      return FluidUtil.getFluidHandler(bucket).map(handler -> {
         for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack contents = handler.getFluidInTank(i);
            if (!contents.isEmpty()) {
               return contents;
            }
         }

         return FluidStack.EMPTY;
      }).orElse(FluidStack.EMPTY);
   }

   public static boolean isEmptyContainer(@Nonnull ItemStack itemStack) {
      return FluidUtil.getFluidHandler(itemStack).map(handler -> {
         for (int i = 0; i < handler.getTanks(); i++) {
            if (handler.getTankCapacity(i) > 0) {
               FluidStack contents = handler.getFluidInTank(i);
               if (contents.isEmpty()) {
                  return true;
               }

               if (contents.getAmount() > 0) {
                  return false;
               }
            }
         }

         return false;
      }).orElse(false);
   }

   public static boolean isFilledContainer(@Nonnull ItemStack itemStack) {
      return FluidUtil.getFluidHandler(itemStack).map(handler -> {
         for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack contents = handler.getFluidInTank(i);
            if (contents.isEmpty() || contents.getAmount() < handler.getTankCapacity(i)) {
               return false;
            }
         }

         return true;
      }).orElse(false);
   }

   @Nonnull
   public static ItemStack drainContainer(@Nonnull ItemStack container) {
      ItemStack empty = container.copy();
      empty.setCount(1);
      return FluidUtil.getFluidHandler(empty)
         .map(handler -> !handler.drain(Integer.MAX_VALUE, FluidAction.EXECUTE).isEmpty() ? handler.getContainer() : ItemStack.EMPTY)
         .orElse(ItemStack.EMPTY);
   }

   @Nonnull
   public static ItemStack fillContainer(@Nonnull FluidStack fluidStack, @Nonnull ItemStack itemStack) {
      return FluidUtil.getFluidHandler(itemStack.copy()).map(handler -> {
         int filled = handler.fill(fluidStack, FluidAction.EXECUTE);
         return filled == 0 ? ItemStack.EMPTY : handler.getContainer();
      }).orElse(ItemStack.EMPTY);
   }

   @Nonnull
   public static FluidStack pickupFluidBlock(Level world, BlockPos pos, @Nonnull Predicate<FluidStack> action, @Nonnull Runnable clearBlock) {
      BlockState blockstate = world.getBlockState(pos);
      FluidState fluidstate = world.getFluidState(pos);
      Fluid fluid = fluidstate.getType();
      if (blockstate.getBlock() instanceof LiquidBlock) {
         FluidStack stack = new FluidStack(fluid, 1000);
         if (action.test(stack)) {
            clearBlock.run();
         }

         return stack;
      } else if (blockstate.getBlock() instanceof BucketPickup && fluid != Fluids.EMPTY) {
         FluidStack stack = new FluidStack(fluid, 1000);
         if (action.test(stack)) {
            ItemStack fluidBucket = ((BucketPickup)blockstate.getBlock()).pickupBlock(null, world, pos, blockstate);
            return FluidUtil.getFluidContained(fluidBucket).map(f -> new FluidStack(f.typeHolder(), 1000, f.getComponentsPatch())).orElse(FluidStack.EMPTY);
         } else {
            return stack;
         }
      } else if (blockstate.getBlock() instanceof BucketPickup) {
         FluidStack stack = new FluidStack(Fluids.WATER, 1000);
         if (action.test(stack)) {
            BlockEntity tileentity = blockstate.getBlock() instanceof EntityBlock ? world.getBlockEntity(pos) : null;
            Block.dropResources(blockstate, world, pos, tileentity);
            clearBlock.run();
         }

         return stack;
      } else {
         return FluidStack.EMPTY;
      }
   }
}
