package mcjty.lib.network;

import java.util.List;
import javax.annotation.Nonnull;
import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketGetListFromServer(ResourceKey<Level> dimension, BlockPos pos, String command, TypedMap params) implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "getlistfromserver");
   public static final Type<PacketGetListFromServer> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, PacketGetListFromServer> CODEC = StreamCodec.composite(
      ResourceKey.streamCodec(Registries.DIMENSION),
      PacketGetListFromServer::dimension,
      BlockPos.STREAM_CODEC,
      PacketGetListFromServer::pos,
      ByteBufCodecs.STRING_UTF8,
      PacketGetListFromServer::command,
      TypedMap.STREAM_CODEC,
      PacketGetListFromServer::params,
      PacketGetListFromServer::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static PacketGetListFromServer create(BlockPos pos, String cmd, @Nonnull TypedMap params) {
      ResourceKey<Level> dimension = SafeClientTools.getWorld().dimension();
      return new PacketGetListFromServer(dimension, pos, cmd, params);
   }

   public static PacketGetListFromServer create(BlockPos pos, String cmd) {
      ResourceKey<Level> dimension = SafeClientTools.getWorld().dimension();
      return new PacketGetListFromServer(dimension, pos, cmd, TypedMap.EMPTY);
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player player = ctx.player();
         ServerLevel world = LevelTools.getLevel(player.level(), this.dimension);
         if (world.hasChunkAt(this.pos)) {
            if (world.getBlockEntity(this.pos) instanceof GenericTileEntity generic) {
               CommandInfo<?> info = McJtyLib.getCommandInfo(this.command);
               if (info == null) {
                  throw new IllegalStateException("Command '" + this.command + "' is not registered!");
               }

               Class type = info.type();
               List list = generic.executeServerCommandList(this.command, player, this.params, type);
               Networking.sendToPlayer(new PacketSendResultToClient(this.pos, this.command, list), player);
            } else {
               Logging.logError("Command '" + this.command + "' not handled!");
            }
         }
      });
   }
}
