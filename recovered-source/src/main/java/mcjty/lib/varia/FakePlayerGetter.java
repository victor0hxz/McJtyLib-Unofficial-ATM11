package mcjty.lib.varia;

import com.mojang.authlib.GameProfile;
import java.util.Objects;
import java.util.UUID;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class FakePlayerGetter {
   private final GenericTileEntity te;
   private final String fakeName;
   private ServerPlayer harvester = null;

   public FakePlayerGetter(GenericTileEntity te, String fakeName) {
      this.te = te;
      this.fakeName = fakeName;
   }

   public ServerPlayer get() {
      ServerPlayer playerEntity = this.getFakeHarvester();
      UUID owner = this.te.getOwnerUUID();
      if (owner != null) {
         ServerPlayer player = this.te.getLevel().getServer().getPlayerList().getPlayer(owner);
         if (player != null && !Objects.equals(playerEntity.getGameProfile().name(), player.getGameProfile().name())) {
            this.harvester = null;
            playerEntity = this.getFakeHarvester();
         }
      }

      return playerEntity;
   }

   private ServerPlayer getFakeHarvester() {
      if (this.harvester == null) {
         UUID owner = this.te.getOwnerUUID();
         if (owner == null) {
            owner = UUID.nameUUIDFromBytes("rftools_builder".getBytes());
         }

         ServerLevel serverLevel = (ServerLevel)this.te.getLevel();
         this.harvester = FakePlayerFactory.get(serverLevel, new GameProfile(owner, this.getName()));
         BlockPos worldPosition = this.te.getBlockPos();
         this.harvester.setPos(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
      }

      return this.harvester;
   }

   private String getName() {
      UUID owner = this.te.getOwnerUUID();
      if (owner == null) {
         return this.fakeName;
      } else {
         ServerPlayer player = this.te.getLevel().getServer().getPlayerList().getPlayer(owner);
         return player == null ? this.fakeName : player.getGameProfile().name();
      }
   }
}
