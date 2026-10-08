package mcjty.lib.compat;

import mcjty.lib.compat.theoneprobe.TOPCompatibility;
import net.neoforged.fml.ModList;

public class MainCompatHandler {
   public static void registerWaila() {
   }

   public static void registerTOP() {
      if (ModList.get().isLoaded("theoneprobe")) {
         TOPCompatibility.register();
      }
   }
}
