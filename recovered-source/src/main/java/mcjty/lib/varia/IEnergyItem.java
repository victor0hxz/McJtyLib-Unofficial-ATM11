package mcjty.lib.varia;

import net.minecraft.world.item.ItemStack;

public interface IEnergyItem {
   long receiveEnergyL(ItemStack var1, long var2, boolean var4);

   long extractEnergyL(ItemStack var1, long var2, boolean var4);

   long getEnergyStoredL(ItemStack var1);

   long getMaxEnergyStoredL(ItemStack var1);

   default int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
      return (int)this.receiveEnergyL(container, maxReceive, simulate);
   }

   default int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
      return (int)this.extractEnergyL(container, maxExtract, simulate);
   }

   default int getEnergyStored(ItemStack container) {
      return EnergyTools.getIntEnergyStored(this.getEnergyStoredL(container), this.getMaxEnergyStoredL(container));
   }

   default int getMaxEnergyStored(ItemStack container) {
      return EnergyTools.unsignedClampToInt(this.getMaxEnergyStoredL(container));
   }
}
