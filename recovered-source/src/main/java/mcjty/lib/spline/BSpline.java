package mcjty.lib.spline;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BSpline<T> extends Spline<T> {
   private int idx;
   private float t;

   public BSpline(Supplier<T> supplier, BiFunction<T, T, T> subtract, BiFunction<T, T, T> add, BiFunction<T, Float, T> scale) {
      super(supplier, subtract, add, scale);
   }

   protected float baseFunction(int i, float t) {
      return switch (i) {
         case -2 -> (((-t + 3.0F) * t - 3.0F) * t + 1.0F) / 6.0F;
         case -1 -> ((3.0F * t - 6.0F) * t * t + 4.0F) / 6.0F;
         case 0 -> (((-3.0F * t + 3.0F) * t + 3.0F) * t + 1.0F) / 6.0F;
         case 1 -> t * t * t / 6.0F;
         default -> 0.0F;
      };
   }

   @Override
   public void calculate(float time) {
      for (this.idx = 0; this.idx < this.points.size() - 1; this.idx++) {
         if (time >= this.times.get(this.idx) && time <= this.times.get(this.idx + 1)) {
            this.t = 1.0F - (this.times.get(this.idx + 1) - time) / (this.times.get(this.idx + 1) - this.times.get(this.idx));
            return;
         }
      }

      this.t = 1.0F;
   }

   @Override
   public T getInterpolated() {
      T val = this.supplier.get();

      for (int j = -2; j <= 1; j++) {
         int id = this.idx + j + 1;
         T pp;
         if (id == -1) {
            pp = this.subtract.apply(this.points.get(0), this.subtract.apply(this.points.get(1), this.points.get(0)));
         } else if (id == -2) {
            pp = this.subtract.apply(this.points.get(0), this.scale.apply(this.subtract.apply(this.points.get(1), this.points.get(0)), 2.0F));
         } else if (id >= this.points.size()) {
            pp = this.subtract
               .apply(
                  this.points.get(this.points.size() - 1),
                  this.subtract.apply(this.points.get(this.points.size() - 2), this.points.get(this.points.size() - 1))
               );
         } else {
            pp = this.points.get(id);
         }

         float base = this.baseFunction(j, this.t);
         val = this.add.apply(val, this.scale.apply(pp, base));
      }

      return val;
   }
}
