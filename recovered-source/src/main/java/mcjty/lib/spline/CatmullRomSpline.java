package mcjty.lib.spline;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CatmullRomSpline<T> extends BSpline<T> {
   private int idx;
   private float t;

   @Override
   protected float baseFunction(int i, float t) {
      return switch (i) {
         case -2 -> ((-t + 2.0F) * t - 1.0F) * t / 2.0F;
         case -1 -> ((3.0F * t - 5.0F) * t * t + 2.0F) / 2.0F;
         case 0 -> ((-3.0F * t + 4.0F) * t + 1.0F) * t / 2.0F;
         case 1 -> (t - 1.0F) * t * t / 2.0F;
         default -> 0.0F;
      };
   }

   public CatmullRomSpline(Supplier<T> supplier, BiFunction<T, T, T> subtract, BiFunction<T, T, T> add, BiFunction<T, Float, T> scale) {
      super(supplier, subtract, add, scale);
   }
}
