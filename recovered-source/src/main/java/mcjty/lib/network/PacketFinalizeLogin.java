package mcjty.lib.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketFinalizeLogin() implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "finalize_login");
   public static final Type<PacketFinalizeLogin> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, PacketFinalizeLogin> CODEC = StreamCodec.unit(new PacketFinalizeLogin());

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      this.finalizeClientLogin();
   }

   private void finalizeClientLogin() {
   }
}
