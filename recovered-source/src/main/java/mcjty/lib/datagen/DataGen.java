package mcjty.lib.datagen;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGen {
   public DataGen(String modid, GatherDataEvent event) {
   }

   public void addCodecProvider(String name, String directory, Codec<?> codec) {
   }

   public void add(Object... builders) {
   }

   public void generate() {
   }

   public static <T> T has(Object itemOrTag) {
      return null;
   }

   public static <T> T has(Object bounds, Object item) {
      return null;
   }
}
