package mcjty.lib.container;

import java.util.HashMap;
import java.util.Map;

public enum SlotType {
   SLOT_UNKNOWN("unknown"),
   SLOT_GHOST("ghost"),
   SLOT_GHOSTOUT("ghostout"),
   SLOT_GENERIC("generic"),
   SLOT_SPECIFICITEM("specificitem"),
   SLOT_PLAYERINV("playerinv"),
   SLOT_PLAYERHOTBAR("playerhotbar"),
   SLOT_CRAFTRESULT("craftresult");

   private final String name;
   private static final Map<String, SlotType> SLOT_TYPE_MAP = new HashMap<>();

   private SlotType(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public static SlotType findByName(String name) {
      return SLOT_TYPE_MAP.get(name);
   }

   static {
      for (SlotType type : values()) {
         SLOT_TYPE_MAP.put(type.getName(), type);
      }
   }
}
