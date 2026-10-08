package mcjty.lib.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSendResultToClient(BlockPos pos, String command, List list) implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "sendresulttoclient");
   public static final Type<PacketSendResultToClient> TYPE = new Type(ID);
   public static final StreamCodec<FriendlyByteBuf, PacketSendResultToClient> CODEC = StreamCodec.of((buf, packet) -> {
      buf.writeBlockPos(packet.pos);
      buf.writeUtf(packet.command);
      CommandInfo<?> info = McJtyLib.getCommandInfo(packet.command);
      if (info == null) {
         throw new IllegalStateException("Command '" + packet.command + "' is not registered!");
      } else {
         BiConsumer<FriendlyByteBuf, Object> serializer = (BiConsumer<FriendlyByteBuf, Object>)info.serializer();
         if (serializer == null) {
            throw new IllegalStateException("Command '" + packet.command + "' is not registered!");
         } else {
            if (packet.list == null) {
               buf.writeInt(-1);
            } else {
               buf.writeInt(packet.list.size());

               for (Object item : packet.list) {
                  serializer.accept(buf, item);
               }
            }
         }
      }
   }, buf -> {
      BlockPos pos = buf.readBlockPos();
      String command = buf.readUtf(32767);
      CommandInfo<?> info = McJtyLib.getCommandInfo(command);
      if (info == null) {
         throw new IllegalStateException("Command '" + command + "' is not registered!");
      } else {
         Function<FriendlyByteBuf, ?> deserializer = info.deserializer();
         int size = buf.readInt();
         List list;
         if (size != -1) {
            list = new ArrayList(size);

            for (int i = 0; i < size; i++) {
               list.add(deserializer.apply(buf));
            }
         } else {
            list = null;
         }

         return new PacketSendResultToClient(pos, command, list);
      }
   });

   public static PacketSendResultToClient create(BlockPos pos, String command, List list) {
      return new PacketSendResultToClient(pos, command, new ArrayList(list));
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         if (SafeClientTools.getClientWorld().getBlockEntity(this.pos) instanceof GenericTileEntity generic) {
            generic.handleListFromServer(this.command, SafeClientTools.getClientPlayer(), TypedMap.EMPTY, this.list);
         } else {
            Logging.logError("Can't handle command '" + this.command + "'!");
         }
      });
   }
}
