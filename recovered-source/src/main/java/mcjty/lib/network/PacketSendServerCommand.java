package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSendServerCommand(String modid, String command, TypedMap arguments) implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "sendservercommand");
   public static final Type<PacketSendServerCommand> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, PacketSendServerCommand> CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      PacketSendServerCommand::modid,
      ByteBufCodecs.STRING_UTF8,
      PacketSendServerCommand::command,
      TypedMap.STREAM_CODEC,
      PacketSendServerCommand::arguments,
      PacketSendServerCommand::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static PacketSendServerCommand create(String modid, String command, TypedMap arguments) {
      return new PacketSendServerCommand(modid, command, arguments);
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player player = ctx.player();

         try {
            boolean result = McJtyLib.handleCommand(this.modid, this.command, player, this.arguments);
            if (!result) {
               Logging.logError("Error handling command '" + this.command + "' for mod '" + this.modid + "'!");
            }
         } catch (Exception var4) {
            var4.printStackTrace();
            throw new RuntimeException(var4);
         }
      });
   }
}
