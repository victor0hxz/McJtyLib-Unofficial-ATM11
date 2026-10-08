package mcjty.lib.varia;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Broadcaster {
   private static final Map<String, Long> messages = Maps.newHashMap();

   public static void broadcast(Level worldObj, int x, int y, int z, String message, float radius) {
      long time = System.currentTimeMillis();
      if (messages.containsKey(message)) {
         long t = messages.get(message);
         if (time - t <= 2000L) {
            return;
         }

         messages.remove(message);
      }

      messages.put(message, time);

      for (Player player : worldObj.players()) {
         double sqdist = player.distanceToSqr(x + 0.5, y + 0.5, z + 0.5);
         if (sqdist < radius) {
            Logging.warn(player, message);
         }
      }
   }
}
