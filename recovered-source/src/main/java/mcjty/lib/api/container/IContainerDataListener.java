package mcjty.lib.api.container;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

public interface IContainerDataListener {
   Identifier getId();

   boolean isDirtyAndClear();

   void toBytes(RegistryFriendlyByteBuf var1);

   void readBuf(RegistryFriendlyByteBuf var1);
}
