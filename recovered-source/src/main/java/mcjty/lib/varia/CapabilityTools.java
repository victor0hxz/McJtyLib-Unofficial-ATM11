package mcjty.lib.varia;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities.Item;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class CapabilityTools {
   @Nullable
   public static IItemHandler getItemCapability(Level level, BlockPos pos, @Nullable Direction side) {
      if (level == null) {
         return null;
      } else {
         IItemHandler legacy = (IItemHandler)level.getCapability(LegacyCapabilities.ITEM_BLOCK, pos, side);
         if (legacy != null) {
            return legacy;
         } else {
            ResourceHandler<ItemResource> modern = (ResourceHandler<ItemResource>)level.getCapability(Item.BLOCK, pos, side);
            return modern == null ? null : IItemHandler.of(modern);
         }
      }
   }

   @Nullable
   public static IItemHandler getItemCapabilitySafe(BlockEntity tileEntity) {
      if (tileEntity != null) {
         try {
            return getItemCapability(tileEntity.getLevel(), tileEntity.getBlockPos(), null);
         } catch (RuntimeException var2) {
            reportWrongBlock(tileEntity, var2);
         }
      }

      return null;
   }

   @Nullable
   public static IFluidHandler getFluidCapability(Level level, BlockPos pos, @Nullable Direction side) {
      if (level == null) {
         return null;
      } else {
         IFluidHandler legacy = (IFluidHandler)level.getCapability(LegacyCapabilities.FLUID_BLOCK, pos, side);
         if (legacy != null) {
            return legacy;
         } else {
            ResourceHandler<FluidResource> modern = (ResourceHandler<FluidResource>)level.getCapability(Fluid.BLOCK, pos, side);
            return modern == null ? null : IFluidHandler.of(modern);
         }
      }
   }

   @Nullable
   public static IFluidHandler getFluidCapabilitySafe(BlockEntity tileEntity) {
      if (tileEntity != null) {
         try {
            return getFluidCapability(tileEntity.getLevel(), tileEntity.getBlockPos(), null);
         } catch (RuntimeException var2) {
            reportWrongBlock(tileEntity, var2);
         }
      }

      return null;
   }

   private static void reportWrongBlock(BlockEntity tileEntity, Exception e) {
      if (tileEntity != null) {
         Identifier name = Tools.getId(tileEntity.getLevel().getBlockState(tileEntity.getBlockPos()));
         Logging.logError(
            "Block "
               + name.toString()
               + " at "
               + BlockPosTools.toString(tileEntity.getBlockPos())
               + " does not respect the capability API and crashes on null side."
         );
         Logging.logError("Please report to the corresponding mod. This is not a bug in RFTools!");
      }

      if (e != null) {
         Logging.logError("Exception", e);
      }
   }
}
