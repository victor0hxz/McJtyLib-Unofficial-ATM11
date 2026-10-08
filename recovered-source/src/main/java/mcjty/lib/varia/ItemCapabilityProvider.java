package mcjty.lib.varia;

import java.util.Objects;
import javax.annotation.Nonnull;
import mcjty.lib.api.power.IBigPower;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class ItemCapabilityProvider implements ICapabilityProvider, IBigPower {
   private final ItemStack itemStack;
   private final IEnergyItem item;
   private final Lazy<IEnergyStorage> energy = Lazy.of(this::createEnergyStorage);

   @Nonnull
   private <T> IEnergyStorage createEnergyStorage() {
      return new IEnergyStorage() {
         {
            Objects.requireNonNull(ItemCapabilityProvider.this);
         }

         public int receiveEnergy(int maxReceive, boolean simulate) {
            return (int)ItemCapabilityProvider.this.item.receiveEnergyL(ItemCapabilityProvider.this.itemStack, maxReceive, simulate);
         }

         public int extractEnergy(int maxExtract, boolean simulate) {
            return (int)ItemCapabilityProvider.this.item.extractEnergyL(ItemCapabilityProvider.this.itemStack, maxExtract, simulate);
         }

         public int getEnergyStored() {
            return EnergyTools.getIntEnergyStored(
               ItemCapabilityProvider.this.item.getEnergyStoredL(ItemCapabilityProvider.this.itemStack),
               ItemCapabilityProvider.this.item.getMaxEnergyStoredL(ItemCapabilityProvider.this.itemStack)
            );
         }

         public int getMaxEnergyStored() {
            return EnergyTools.unsignedClampToInt(ItemCapabilityProvider.this.item.getMaxEnergyStoredL(ItemCapabilityProvider.this.itemStack));
         }

         public boolean canExtract() {
            return true;
         }

         public boolean canReceive() {
            return true;
         }
      };
   }

   public ItemCapabilityProvider(ItemStack itemStack, IEnergyItem item) {
      this.itemStack = itemStack;
      this.item = item;
   }

   @Nullable
   public Object getCapability(Object o, Object o2) {
      return null;
   }

   @Override
   public long getStoredPower() {
      return this.item.getEnergyStoredL(this.itemStack);
   }

   @Override
   public long getCapacity() {
      return this.item.getMaxEnergyStoredL(this.itemStack);
   }
}
