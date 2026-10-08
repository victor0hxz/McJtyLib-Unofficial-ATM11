package mcjty.lib.api.container;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.IItemHandler;

public interface IGenericContainer {
   void addShortListener(DataSlot var1);

   void addIntegerListener(DataSlot var1);

   void addContainerDataListener(IContainerDataListener var1);

   void addDataListener(IGenericContainer.DataListener<?, ?> var1);

   void setupInventories(@Nullable IItemHandler var1, Inventory var2);

   AbstractContainerMenu getAsContainer();

   public record DataListener<B extends ByteBuf, T>(AttachmentType<T> type, StreamCodec<B, T> streamCodec, Codec<T> codec) {
   }
}
