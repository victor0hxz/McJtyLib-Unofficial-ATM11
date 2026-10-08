package mcjty.lib.api.module;

import net.minecraft.world.item.ItemStack;

public interface IModuleSupport {
   boolean isModule(ItemStack var1);

   int getFirstSlot();

   int getLastSlot();
}
