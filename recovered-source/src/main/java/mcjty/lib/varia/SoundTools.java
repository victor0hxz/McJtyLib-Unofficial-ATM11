package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class SoundTools {
   public static SoundEvent createSoundEvent(Identifier id) {
      return SoundEvent.createVariableRangeEvent(id);
   }

   public static SoundEvent findSound(Identifier name) {
      String var1 = name.toString();

      return switch (var1) {
         case "minecraft:block.note_block.bell" -> (SoundEvent)SoundEvents.NOTE_BLOCK_BELL.value();
         case "minecraft:block.note_block.pling" -> (SoundEvent)SoundEvents.NOTE_BLOCK_PLING.value();
         default -> SoundEvents.EXPERIENCE_ORB_PICKUP;
      };
   }

   public static void playSound(Level worldObj, SoundEvent soundName, double x, double y, double z, double volume, double pitch) {
      ClientboundSoundPacket soundEffect = new ClientboundSoundPacket(Holder.direct(soundName), SoundSource.BLOCKS, x, y, z, (float)volume, (float)pitch, 0L);

      for (int j = 0; j < worldObj.players().size(); j++) {
         ServerPlayer player = (ServerPlayer)worldObj.players().get(j);
         BlockPos chunkcoordinates = player.blockPosition();
         double xx = x - chunkcoordinates.getX();
         double yy = y - chunkcoordinates.getY();
         double zz = z - chunkcoordinates.getZ();
         double sqDist = xx * xx + yy * yy + zz * zz;
         if (sqDist <= 256.0) {
            player.connection.send(soundEffect);
         }
      }
   }
}
