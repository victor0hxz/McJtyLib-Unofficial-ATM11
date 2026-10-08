package mcjty.lib.tileentity;

import java.util.Objects;
import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.api.power.ItemEnergy;
import mcjty.lib.setup.Registration;
import mcjty.lib.varia.EnergyTools;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class GenericEnergyStorage extends SnapshotJournal<Long> implements IEnergyStorage, EnergyHandler {
   private final GenericTileEntity tileEntity;
   private final boolean isReceiver;
   private long energy;
   private final long capacity;
   private final long maxReceive;

   public GenericEnergyStorage(GenericTileEntity tileEntity, boolean isReceiver, long capacity, long maxReceive) {
      this.tileEntity = tileEntity;
      this.isReceiver = isReceiver;
      this.capacity = Math.max(0L, capacity);
      long modernFloor = Math.min(this.capacity, 1000000L);
      this.maxReceive = Math.max(Math.max(0L, maxReceive), modernFloor);
      this.energy = 0L;
   }

   public long getAmountAsLong() {
      return this.energy;
   }

   public long getCapacityAsLong() {
      return this.capacity;
   }

   public int insert(int amount, TransactionContext transaction) {
      TransferPreconditions.checkNonNegative(amount);
      if (this.isReceiver && amount != 0) {
         int inserted = (int)Math.min((long)amount, Math.min(this.maxReceive, this.capacity - this.energy));
         if (inserted > 0) {
            this.updateSnapshots(transaction);
            this.energy += inserted;
         }

         return inserted;
      } else {
         return 0;
      }
   }

   public int extract(int amount, TransactionContext transaction) {
      TransferPreconditions.checkNonNegative(amount);
      return 0;
   }

   protected Long createSnapshot() {
      return this.energy;
   }

   protected void revertToSnapshot(Long snapshot) {
      this.energy = snapshot;
   }

   protected void onRootCommit(Long originalState) {
      this.tileEntity.markDirtyQuick();
   }

   public int receiveEnergy(int maxReceive, boolean simulate) {
      if (this.isReceiver) {
         if (!this.canReceive()) {
            return 0;
         } else {
            long energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, (long)maxReceive));
            if (!simulate) {
               this.energy += energyReceived;
               this.tileEntity.markDirtyQuick();
            }

            return (int)energyReceived;
         }
      } else {
         return 0;
      }
   }

   public int extractEnergy(int maxExtract, boolean simulate) {
      return 0;
   }

   public int getEnergyStored() {
      return EnergyTools.getIntEnergyStored(this.energy, this.capacity);
   }

   public long getEnergy() {
      return this.energy;
   }

   public long getCapacity() {
      return this.capacity;
   }

   public void applyImplicitComponents(ItemEnergy energy) {
      if (energy != null) {
         this.setEnergy(energy.energy());
      }
   }

   public void collectImplicitComponents(Builder builder) {
      builder.set(Registration.ITEM_ENERGY, new ItemEnergy(this.getEnergy()));
   }

   public void setEnergy(long s) {
      this.energy = s;
   }

   public void consumeEnergy(long energy) {
      this.energy -= energy;
      if (this.energy < 0L) {
         this.energy = 0L;
      } else if (this.energy > this.capacity) {
         this.energy = this.capacity;
      }

      this.tileEntity.markDirtyQuick();
   }

   public void produceEnergy(long energy) {
      this.energy += energy;
      if (this.energy < 0L) {
         this.energy = 0L;
      } else if (this.energy > this.capacity) {
         this.energy = this.capacity;
      }

      this.tileEntity.markDirtyQuick();
   }

   public LongTag serializeNBT(Provider provider) {
      return LongTag.valueOf(this.energy);
   }

   public void deserializeNBT(Provider provider, LongTag nbt) {
      this.energy = nbt.longValue();
   }

   public int getMaxEnergyStored() {
      return EnergyTools.unsignedClampToInt(this.capacity);
   }

   public boolean canExtract() {
      return false;
   }

   public boolean canReceive() {
      return this.isReceiver;
   }

   public void addIntegerListeners(IGenericContainer container) {
      container.addIntegerListener(new DataSlot() {
         {
            Objects.requireNonNull(GenericEnergyStorage.this);
         }

         public int get() {
            return (int)GenericEnergyStorage.this.getEnergy();
         }

         public void set(int i) {
            long orig = GenericEnergyStorage.this.getEnergy() & -4294967296L;
            orig |= i;
            GenericEnergyStorage.this.setEnergy(orig);
         }
      });
      container.addIntegerListener(new DataSlot() {
         {
            Objects.requireNonNull(GenericEnergyStorage.this);
         }

         public int get() {
            return (int)(GenericEnergyStorage.this.getEnergy() >> 32);
         }

         public void set(int i) {
            long orig = GenericEnergyStorage.this.getEnergy() & 4294967295L;
            orig |= (long)i << 32;
            GenericEnergyStorage.this.setEnergy(orig);
         }
      });
   }

   public void save(ValueOutput output, String tagName) {
      output.putLong(tagName, this.energy);
   }

   public void load(ValueInput input, String tagName) {
      this.energy = input.getLongOr(tagName, 0L);
   }

   public void save(CompoundTag tag, String tagName, Provider provider) {
      tag.put(tagName, this.serializeNBT(provider));
   }

   public void load(CompoundTag tag, String tagName, Provider provider) {
      if (tag.contains(tagName)) {
         this.deserializeNBT(provider, (LongTag)tag.get(tagName));
      }
   }
}
