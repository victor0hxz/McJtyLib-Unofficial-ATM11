package mcjty.lib.multiblock;

import net.minecraft.resources.Identifier;

public interface IMultiblockConnector {
   Identifier getId();

   int getMultiblockId();

   void setMultiblockId(int var1);
}
