package mcjty.lib.varia;

import java.util.HashMap;
import java.util.Map;

public class StringRegister {
   public static final StringRegister STRINGS = new StringRegister();
   private int lastId = 1;
   private final Map<String, Integer> stringToId = new HashMap<>();
   private final Map<Integer, String> idToString = new HashMap<>();

   public int get(String s) {
      if (this.stringToId.containsKey(s)) {
         return this.stringToId.get(s);
      } else {
         int id = this.lastId++;
         this.stringToId.put(s, id);
         this.idToString.put(id, s);
         return id;
      }
   }

   public String get(Integer id) {
      return this.idToString.get(id);
   }
}
