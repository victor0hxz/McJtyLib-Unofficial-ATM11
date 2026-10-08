package mcjty.lib.varia;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class InventoryTools {
   public static int getInventorySize(BlockEntity tileEntity) {
      if (tileEntity == null) {
         return 0;
      } else {
         IItemHandler handler = CapabilityTools.getItemCapability(tileEntity.getLevel(), tileEntity.getBlockPos(), null);
         return handler != null ? handler.getSlots() : 0;
      }
   }

   public static boolean isInventory(BlockEntity te) {
      return te != null && te.getLevel().getCapability(LegacyCapabilities.ITEM_BLOCK, te.getBlockPos(), null) != null;
   }

   public static Stream<ItemStack> getItems(BlockEntity tileEntity, Predicate<ItemStack> predicate) {
      Builder<ItemStack> builder = Stream.builder();
      if (tileEntity != null) {
         IItemHandler handler = CapabilityTools.getItemCapability(tileEntity.getLevel(), tileEntity.getBlockPos(), null);
         if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
               ItemStack itemStack = handler.getStackInSlot(i);
               if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                  builder.add(itemStack);
               }
            }
         }
      }

      return builder.build();
   }

   @Nonnull
   public static ItemStack getFirstMatchingItem(BlockEntity tileEntity, Predicate<ItemStack> predicate) {
      if (tileEntity != null) {
         IItemHandler handler = CapabilityTools.getItemCapability(tileEntity.getLevel(), tileEntity.getBlockPos(), null);
         if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
               ItemStack itemStack = handler.getStackInSlot(i);
               if (!itemStack.isEmpty() && predicate.test(itemStack)) {
                  return itemStack;
               }
            }

            return ItemStack.EMPTY;
         }
      }

      return ItemStack.EMPTY;
   }

   @Nonnull
   public static ItemStack insertItem(Level world, BlockPos pos, Direction direction, @Nonnull ItemStack s) {
      BlockEntity te = world.getBlockEntity(direction == null ? pos : pos.relative(direction));
      if (te != null) {
         Direction opposite = direction == null ? null : direction.getOpposite();
         IItemHandler handler = CapabilityTools.getItemCapability(te.getLevel(), te.getBlockPos(), opposite);
         return handler != null ? ItemHandlerHelper.insertItem(handler, s, false) : s;
      } else {
         return s;
      }
   }

   public static boolean isItemStackConsideredEqual(ItemStack result, ItemStack itemstack1) {
      return !itemstack1.isEmpty()
         && itemstack1.getItem() == result.getItem()
         && result.getDamageValue() == itemstack1.getDamageValue()
         && ItemStack.isSameItemSameComponents(result, itemstack1);
   }

   @Nonnull
   public static ItemStack insertItemRanged(IItemHandler dest, @Nonnull ItemStack stack, int start, int stop, boolean simulate) {
      if (dest != null && !stack.isEmpty()) {
         for (int i = start; i < stop; i++) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
               return ItemStack.EMPTY;
            }
         }

         return stack;
      } else {
         return stack;
      }
   }
}
