package mcjty.lib.varia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class ExplosionTools {
   public static void explodeFullDestroy(Level level, BlockPos location, float radius) {
      level.explode(null, location.getX(), location.getY(), location.getZ(), radius, false, ExplosionInteraction.TNT);
   }
}
