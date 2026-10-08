package mcjty.lib.varia;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForge;

public class SpawnCanceler {
   public static void registerSpawnCanceler(Predicate<Entity> entityConsumer) {
      NeoForge.EVENT_BUS.addListener(event -> {
         LevelAccessor world = event.getLevel();
         if (world instanceof Level) {
            Entity entity = event.getEntity();
            if (entityConsumer.test(entity)) {
               event.setSpawnCancelled(true);
            }
         }
      });
   }
}
