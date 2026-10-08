package mcjty.lib.client;

import net.minecraft.world.phys.Vec3;

public abstract class AbstractDynamicBakedModel {
   protected static Vec3 v(double x, double y, double z) {
      return new Vec3(x, y, z);
   }
}
