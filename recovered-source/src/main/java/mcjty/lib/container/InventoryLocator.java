package mcjty.lib.container;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.varia.LegacyCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public class InventoryLocator {
   private BlockPos inventoryCoordinate = null;
   private Direction inventorySide = null;

   @Nonnull
   private IItemHandler getItemHandlerAtDirection(Level worldObj, BlockPos thisCoordinate, Direction direction) {
      if (direction == null) {
         return this.inventoryCoordinate != null ? this.getItemHandlerAtCoordinate(worldObj, this.inventoryCoordinate, this.inventorySide) : null;
      } else if (worldObj.getCapability(LegacyCapabilities.ITEM_BLOCK, thisCoordinate, direction.getOpposite()) != null) {
         this.inventoryCoordinate = thisCoordinate.relative(direction);
         this.inventorySide = direction.getOpposite();
         return this.getItemHandlerAtCoordinate(worldObj, this.inventoryCoordinate, this.inventorySide);
      } else {
         return null;
      }
   }

   @Nullable
   private IItemHandler getItemHandlerAtCoordinate(Level worldObj, BlockPos c, Direction direction) {
      return (IItemHandler)worldObj.getCapability(LegacyCapabilities.ITEM_BLOCK, c, direction);
   }

   public void ejectStack(Level worldObj, BlockPos pos, ItemStack stack, BlockPos thisCoordinate, Direction[] directions) {
      for (Direction dir : directions) {
         if (stack.isEmpty()) {
            break;
         }

         IItemHandler itemHandler = this.getItemHandlerAtDirection(worldObj, thisCoordinate, dir);
         if (itemHandler != null) {
            stack = ItemHandlerHelper.insertItem(itemHandler, stack, false);
         }
      }

      if (!stack.isEmpty()) {
         ItemEntity entityItem = new ItemEntity(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
         worldObj.addFreshEntity(entityItem);
      }
   }

   public Direction getInventorySide() {
      return this.inventorySide;
   }
}
