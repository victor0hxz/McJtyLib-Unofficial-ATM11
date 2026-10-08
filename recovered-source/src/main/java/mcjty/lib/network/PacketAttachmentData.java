package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.container.GenericContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketAttachmentData(Identifier attachmentTypeId, RegistryFriendlyByteBuf buffer) implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "attachmentdata");
   public static final Type<PacketAttachmentData> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, PacketAttachmentData> CODEC = StreamCodec.of((buf, packet) -> {
      buf.writeIdentifier(packet.attachmentTypeId);
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
      return new PacketAttachmentData(containerId, buffer);
   });

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static PacketAttachmentData create(Identifier id, RegistryFriendlyByteBuf buffer) {
      return new PacketAttachmentData(id, buffer);
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         if (ctx.player().containerMenu instanceof GenericContainer gc) {
            gc.receiveData(this.attachmentTypeId, this.buffer);
         }
      });
   }
}
