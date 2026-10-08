package mcjty.lib.network;

import java.util.Optional;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketServerCommandTyped(BlockPos pos, ResourceKey<Level> dimensionId, String command, TypedMap params) implements CustomPacketPayload {
   public static final Identifier ID = Identifier.fromNamespaceAndPath("mcjtylib", "servercommandtyped");
   public static final Type<PacketServerCommandTyped> TYPE = new Type(ID);
   public static final StreamCodec<RegistryFriendlyByteBuf, PacketServerCommandTyped> CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      PacketServerCommandTyped::pos,
      ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs::optional),
      s -> Optional.ofNullable(s.dimensionId),
      ByteBufCodecs.STRING_UTF8,
      PacketServerCommandTyped::command,
      TypedMap.STREAM_CODEC,
      PacketServerCommandTyped::params,
      PacketServerCommandTyped::new
   );

   private PacketServerCommandTyped(BlockPos pos, Optional<ResourceKey<Level>> dimensionId, String command, TypedMap params) {
      this(pos, dimensionId.orElse(null), command, params);
   }

   public static PacketServerCommandTyped create(BlockPos blockPos, ResourceKey<Level> dimension, String command, TypedMap params) {
      return new PacketServerCommandTyped(blockPos, dimension, command, params);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player player = ctx.player();
         Level world;
         if (this.dimensionId == null) {
            world = player.level();
         } else {
            world = LevelTools.getLevel(player.level(), this.dimensionId);
         }

         if (world != null) {
            if (world.hasChunkAt(this.pos)) {
               if (world.getBlockEntity(this.pos) instanceof GenericTileEntity generic && generic.executeServerCommand(this.command, player, this.params)) {
                  return;
               }

               Logging.log("Command " + this.command + " was not handled!");
            }
         }
      });
   }
}
