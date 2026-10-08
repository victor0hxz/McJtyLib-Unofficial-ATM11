package mcjty.lib.varia;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ChunkHolder.PlayerProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class LevelTools {
   public static boolean isLoaded(Level world, BlockPos pos) {
      return world != null && pos != null ? world.hasChunkAt(pos) : false;
   }

   public static ServerLevel getOverworld() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      return server.getLevel(Level.OVERWORLD);
   }

   public static ServerLevel getOverworld(Level world) {
      MinecraftServer server = world.getServer();
      return server.getLevel(Level.OVERWORLD);
   }

   public static ServerLevel getLevel(ResourceKey<Level> type) {
      return ServerLifecycleHooks.getCurrentServer().getLevel(type);
   }

   public static ServerLevel getLevel(Level world, ResourceKey<Level> type) {
      return world.getServer().getLevel(type);
   }

   public static ServerLevel getLevel(Level world, Identifier id) {
      return world.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, id));
   }

   public static ResourceKey<Level> getId(Identifier id) {
      return ResourceKey.create(Registries.DIMENSION, id);
   }

   public static ResourceKey<Level> getId(String id) {
      return ResourceKey.create(Registries.DIMENSION, Identifier.parse(id));
   }

   public static Stream<ServerPlayer> getAllPlayersWatchingBlock(Level world, BlockPos pos) {
      if (world instanceof ServerLevel) {
         PlayerProvider playerManager = ((ServerLevel)world).getChunkSource().chunkMap;
         return playerManager.getPlayers(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4), false).stream();
      } else {
         return Stream.empty();
      }
   }
}
