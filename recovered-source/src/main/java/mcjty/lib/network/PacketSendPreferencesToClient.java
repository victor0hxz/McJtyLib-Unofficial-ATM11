package mcjty.lib.network;

import mcjty.lib.gui.BuffStyle;
import mcjty.lib.gui.GuiStyle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSendPreferencesToClient(BuffStyle buffStyle, Integer buffX, Integer buffY, GuiStyle style) implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "sendpreferences");
   public static final Type<PacketSendPreferencesToClient> TYPE = new Type(ID);
   public static final StreamCodec<FriendlyByteBuf, PacketSendPreferencesToClient> CODEC = StreamCodec.composite(
      BuffStyle.STREAM_CODEC,
      PacketSendPreferencesToClient::buffStyle,
      ByteBufCodecs.INT,
      PacketSendPreferencesToClient::buffX,
      ByteBufCodecs.INT,
      PacketSendPreferencesToClient::buffY,
      GuiStyle.STREAM_CODEC,
      PacketSendPreferencesToClient::style,
      PacketSendPreferencesToClient::new
   );

   public static PacketSendPreferencesToClient create(BuffStyle buffStyle, int buffX, int buffY, GuiStyle style) {
      return new PacketSendPreferencesToClient(buffStyle, buffX, buffY, style);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public BuffStyle getBuffStyle() {
      return this.buffStyle;
   }

   public int getBuffX() {
      return this.buffX;
   }

   public int getBuffY() {
      return this.buffY;
   }

   public GuiStyle getStyle() {
      return this.style;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> SendPreferencesToClientHelper.setPreferences(this));
   }
}
