package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketContainerDataToClient(Identifier containerId, RegistryFriendlyByteBuf buffer) implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "containerdata");
   public static final Type<PacketContainerDataToClient> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, PacketContainerDataToClient> CODEC = StreamCodec.of((buf, packet) -> {
      buf.writeIdentifier(packet.containerId);
      byte[] array = packet.buffer().array();
      buf.writeInt(array.length);
      buf.writeBytes(array);
   }, buf -> {
      Identifier containerId = buf.readIdentifier();
      int l = buf.readInt();
      ByteBuf newbuf = Unpooled.buffer(l);
      byte[] bytes = new byte[l];
      buf.readBytes(bytes);
      newbuf.writeBytes(bytes);
      RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, buf.registryAccess(), ConnectionType.OTHER);
      return new PacketContainerDataToClient(containerId, buffer);
   });

   private static RegistryFriendlyByteBuf createBuffer(RegistryAccess provider, byte[] buf) {
      ByteBuf newbuf = Unpooled.buffer();
      RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, provider, ConnectionType.OTHER);
      buffer.writeBytes(buf);
      return buffer;
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static PacketContainerDataToClient create(Identifier id, RegistryFriendlyByteBuf buffer) {
      return new PacketContainerDataToClient(id, buffer);
   }

   public static PacketContainerDataToClient create(RegistryFriendlyByteBuf buf) {
      Identifier containerId = buf.readIdentifier();
      int l = buf.readInt();
      ByteBuf newbuf = Unpooled.buffer(l);
      byte[] bytes = new byte[l];
      buf.readBytes(bytes);
      newbuf.writeBytes(bytes);
      RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, buf.registryAccess(), ConnectionType.OTHER);
      return new PacketContainerDataToClient(containerId, buffer);
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         if (SafeClientTools.getClientPlayer().containerMenu instanceof GenericContainer gc) {
            IContainerDataListener listener = gc.getListener(this.containerId);
            if (listener != null) {
               listener.readBuf(this.buffer);
            }
         }
      });
   }
}
