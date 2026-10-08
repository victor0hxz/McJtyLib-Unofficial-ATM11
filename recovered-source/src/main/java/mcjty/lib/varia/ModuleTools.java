package mcjty.lib.varia;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.api.modules.ItemModule;
import mcjty.lib.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class ModuleTools {
   public static boolean hasModuleTarget(ItemStack stack) {
      return stack.get(Registration.ITEM_MODULE) != null;
   }

   public static void setPositionInModule(ItemStack stack, ResourceKey<Level> dimension, BlockPos pos, String name) {
      stack.set(Registration.ITEM_MODULE, new ItemModule(GlobalPos.of(dimension, pos), name));
   }

   public static void clearPositionInModule(ItemStack stack) {
      stack.remove(Registration.ITEM_MODULE);
   }

   @Nonnull
   public static BlockPos getPositionFromModule(ItemStack stack) {
      ItemModule module = (ItemModule)stack.get(Registration.ITEM_MODULE);
      return module != null ? module.pos().pos() : BlockPos.ZERO;
   }

   @Nullable
   public static ResourceKey<Level> getDimensionFromModule(ItemStack stack) {
      ItemModule module = (ItemModule)stack.get(Registration.ITEM_MODULE);
      return module != null ? module.pos().dimension() : null;
   }

   public static String getTargetString(ItemStack stack) {
      ItemModule module = (ItemModule)stack.get(Registration.ITEM_MODULE);
      if (module != null) {
         String name = module.name();
         GlobalPos pos = module.pos();
         return getTargetString(name, pos);
      } else {
         return "<unset>";
      }
   }

   public static String getTargetString(String name, GlobalPos pos) {
      return BlockPosTools.isValid(pos.pos())
         ? name + " (at " + pos.pos().getX() + "," + pos.pos().getY() + "," + pos.pos().getZ() + ", " + pos.dimension().identifier() + ")"
         : "<unset>";
   }

   public static boolean installModule(Player player, ItemStack heldItem, InteractionHand hand, BlockPos pos, int start, int stop) {
      Level world = player.level();
      BlockEntity te = world.getBlockEntity(pos);
      if (te == null) {
         return false;
      } else {
         IItemHandler inventory = (IItemHandler)world.getCapability(LegacyCapabilities.ITEM_BLOCK, pos, null);
         if (inventory != null) {
            for (int i = start; i <= stop; i++) {
               if (inventory.getStackInSlot(i).isEmpty()) {
                  ItemStack copy = heldItem.copy();
                  copy.setCount(1);
                  if (inventory instanceof IItemHandlerModifiable) {
                     ((IItemHandlerModifiable)inventory).setStackInSlot(i, copy);
                     heldItem.shrink(1);
                     if (heldItem.isEmpty()) {
                        player.setItemInHand(hand, ItemStack.EMPTY);
                     }

                     if (world.isClientSide()) {
                        player.sendSystemMessage(ComponentFactory.literal("Installed module"));
                     }

                     return true;
                  }

                  throw new IllegalStateException("Not an IItemHandlerModifiable!");
               }
            }
         }

         return false;
      }
   }
}
