package mcjty.lib.varia;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Cached<T> extends Supplier<T> {
   static <T> Cached<T> of(@Nonnull Supplier<T> supplier) {
      return new Cached.CachedImplementation<>(supplier);
   }

   void clear();

   public static final class CachedImplementation<T> implements Cached<T> {
      private final Supplier<T> supplier;
      private T instance;

      private CachedImplementation(Supplier<T> supplier) {
         this.supplier = supplier;
      }

      @Nullable
      @Override
      public T get() {
         if (this.instance == null) {
            this.instance = this.supplier.get();
         }

         return this.instance;
      }

      @Override
      public void clear() {
         this.instance = null;
      }
   }
}
