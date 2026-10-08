package mcjty.lib.api.infusable;

import mcjty.lib.setup.Registration;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DefaultInfusable implements IInfusable {
   private final BlockEntity owner;
   private int infused = 0;

   public DefaultInfusable(BlockEntity owner) {
      this.owner = owner;
   }

   public void applyImplicitComponents(ItemInfusable infusable) {
      if (infusable != null) {
         this.setInfused(infusable.infused());
      }
   }

   public void collectImplicitComponents(Builder builder) {
      builder.set((DataComponentType)Registration.ITEM_INFUSABLE.get(), new ItemInfusable(this.getInfused()));
   }

   @Override
   public int getInfused() {
      return this.infused;
   }

   @Override
   public void setInfused(int i) {
      this.infused = i;
      this.owner.setChanged();
   }

   public void save(ValueOutput output, String tagName) {
      output.putInt(tagName, this.infused);
   }

   public void load(ValueInput input, String tagName) {
      this.infused = input.getIntOr(tagName, 0);
   }

   public void save(CompoundTag tag, String tagName) {
      tag.putInt(tagName, this.infused);
   }

   public void load(CompoundTag tag, String tagName) {
      this.infused = tag.getInt(tagName).orElse(0);
   }
}
