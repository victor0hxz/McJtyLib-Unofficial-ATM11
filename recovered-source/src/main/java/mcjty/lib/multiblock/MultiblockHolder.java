package mcjty.lib.multiblock;

import net.minecraft.nbt.CompoundTag;

public class MultiblockHolder<T extends IMultiblock> {
   private final T mb;

   public MultiblockHolder(T mb) {
      this.mb = mb;
   }

   public T getMb() {
      return this.mb;
   }

   public void load(CompoundTag tagCompound) {
   }

   public CompoundTag save(CompoundTag tagCompound) {
      return tagCompound;
   }
}
