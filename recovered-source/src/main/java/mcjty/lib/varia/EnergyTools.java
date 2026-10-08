package mcjty.lib.varia;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;
import mcjty.lib.api.power.IBigPower;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyTools {
   public static boolean isEnergyTE(BlockEntity te, @Nullable Direction side) {
      return te == null ? false : te.getLevel().getCapability(LegacyCapabilities.ENERGY_BLOCK, te.getBlockPos(), side) != null;
   }

   public static boolean isEnergyItem(ItemStack stack) {
      Item item = stack.getItem();
      return item instanceof IEnergyItem ? true : stack.getCapability(LegacyCapabilities.ENERGY_ITEM) != null;
   }

   public static EnergyTools.EnergyLevel getEnergyLevelMulti(BlockEntity tileEntity, @Nullable Direction side) {
      long maxEnergyStored;
      long energyStored;
      if (tileEntity instanceof IBigPower) {
         maxEnergyStored = ((IBigPower)tileEntity).getCapacity();
         energyStored = ((IBigPower)tileEntity).getStoredPower();
      } else if (tileEntity != null) {
         IEnergyStorage capability = (IEnergyStorage)tileEntity.getLevel().getCapability(LegacyCapabilities.ENERGY_BLOCK, tileEntity.getBlockPos(), side);
         if (capability == null) {
            maxEnergyStored = 0L;
            energyStored = 0L;
         } else {
            maxEnergyStored = capability.getMaxEnergyStored();
            energyStored = capability.getEnergyStored();
         }
      } else {
         maxEnergyStored = 0L;
         energyStored = 0L;
      }

      return new EnergyTools.EnergyLevel(energyStored, maxEnergyStored);
   }

   public static EnergyTools.EnergyLevel getEnergyLevel(BlockEntity tileEntity, @Nullable Direction side) {
      AtomicLong maxEnergyStored = new AtomicLong();
      AtomicLong energyStored = new AtomicLong();
      if (tileEntity != null) {
         IEnergyStorage handler = (IEnergyStorage)tileEntity.getLevel().getCapability(LegacyCapabilities.ENERGY_BLOCK, tileEntity.getBlockPos(), side);
         if (handler != null) {
            maxEnergyStored.set(handler.getMaxEnergyStored());
            energyStored.set(handler.getEnergyStored());
         }
      } else {
         maxEnergyStored.set(0L);
         energyStored.set(0L);
      }

      return new EnergyTools.EnergyLevel(energyStored.get(), maxEnergyStored.get());
   }

   public static long receiveEnergy(BlockEntity tileEntity, Direction from, long maxReceive) {
      if (tileEntity != null) {
         IEnergyStorage handler = (IEnergyStorage)tileEntity.getLevel().getCapability(LegacyCapabilities.ENERGY_BLOCK, tileEntity.getBlockPos(), from);
         return handler != null ? handler.receiveEnergy(unsignedClampToInt(maxReceive), false) : 0L;
      } else {
         return 0L;
      }
   }

   public static long receiveEnergy(ItemStack stack, long maxReceive) {
      Item item = stack.getItem();
      if (item instanceof IEnergyItem) {
         return ((IEnergyItem)item).receiveEnergyL(stack, maxReceive, false);
      } else {
         IEnergyStorage capability = (IEnergyStorage)stack.getCapability(LegacyCapabilities.ENERGY_ITEM);
         return capability != null ? capability.receiveEnergy(unsignedClampToInt(maxReceive), false) : 0L;
      }
   }

   public static int unsignedClampToInt(long l) {
      return l > 2147483647L ? Integer.MAX_VALUE : (int)l;
   }

   public static int getIntEnergyStored(long energyStored, long maxEnergyStored) {
      return unsignedClampToInt(energyStored);
   }

   public static void handleSendingEnergy(Level world, BlockPos pos, long storedPower, long sendPerTick, GenericEnergyStorage storage) {
      for (Direction facing : OrientationTools.DIRECTION_VALUES) {
         BlockPos p = pos.relative(facing);
         BlockEntity te = world.getBlockEntity(p);
         Direction opposite = facing.getOpposite();
         if (isEnergyTE(te, opposite)) {
            long rfToGive = Math.min(sendPerTick, storedPower);
            long received = receiveEnergy(te, opposite, rfToGive);
            storage.consumeEnergy(received);
            storedPower -= received;
            if (storedPower <= 0L) {
               break;
            }
         }
      }
   }

   public record EnergyLevel(long energy, long maxEnergy) {
   }
}
