package mcjty.lib.varia;

import java.util.HashMap;

public class Counter<T> extends HashMap<T, Integer> {
   public void increment(T key) {
      this.increment(key, 1);
   }

   public void increment(T key, int amount) {
      if (this.containsKey(key)) {
         Integer a = this.get(key);
         this.put(key, a + amount);
      } else {
         this.put(key, amount);
      }
   }
}
