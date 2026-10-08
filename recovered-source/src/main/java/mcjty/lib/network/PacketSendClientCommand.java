package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSendClientCommand(String modid, String command, TypedMap arguments) implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "sendclientcommand");
   public static final Type<PacketSendClientCommand> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, PacketSendClientCommand> CODEC = StreamCodec.composite(
      ByteBufCodecs.STRING_UTF8,
      PacketSendClientCommand::modid,
      ByteBufCodecs.STRING_UTF8,
      PacketSendClientCommand::command,
      TypedMap.STREAM_CODEC,
      PacketSendClientCommand::arguments,
      PacketSendClientCommand::new
   );

   public static PacketSendClientCommand create(String modid, String cmdFlashEndergenic, TypedMap build) {
      return new PacketSendClientCommand(modid, cmdFlashEndergenic, build);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public String getModid() {
      return this.modid;
   }

   public String getCommand() {
      return this.command;
   }

   public TypedMap getArguments() {
      return this.arguments;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> ClientCommandHandlerHelper.handle(this));
   }
}
