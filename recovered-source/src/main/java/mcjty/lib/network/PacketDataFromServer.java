package mcjty.lib.network;

import java.util.Optional;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketDataFromServer(BlockPos pos, String command, TypedMap result) implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "datafromserver");
   public static final Type<PacketDataFromServer> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, PacketDataFromServer> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional),
      s -> Optional.ofNullable(s.pos),
      ByteBufCodecs.STRING_UTF8,
      PacketDataFromServer::command,
      TypedMap.STREAM_CODEC,
      PacketDataFromServer::result,
      (pos, command, result) -> new PacketDataFromServer((BlockPos)pos.orElse(null), command, result)
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         BlockEntity te;
         if (this.pos == null) {
            GenericContainer container = getOpenContainer();
            if (container == null) {
               Logging.log("Container is missing!");
               return;
            }

            te = container.getBe();
         } else {
            te = SafeClientTools.getClientWorld().getBlockEntity(this.pos);
         }

         if (!(te instanceof GenericTileEntity generic && generic.executeClientCommand(this.command, SafeClientTools.getClientPlayer(), this.result))) {
            Logging.log("Command " + this.command + " was not handled!");
         }
      });
   }

   private static GenericContainer getOpenContainer() {
      AbstractContainerMenu container = SafeClientTools.getClientPlayer().containerMenu;
      return container instanceof GenericContainer ? (GenericContainer)container : null;
   }
}
