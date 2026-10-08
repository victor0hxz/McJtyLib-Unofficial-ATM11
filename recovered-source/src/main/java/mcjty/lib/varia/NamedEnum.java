package mcjty.lib.varia;

import java.util.Objects;
import net.minecraft.util.StringRepresentable;

public interface NamedEnum<T extends NamedEnum> extends StringRepresentable {
   String getName();

   String[] getDescription();

   static <T extends NamedEnum<T>> T getEnumByName(String name, T[] values) {
      for (T value : values) {
         if (Objects.equals(name, value.getName())) {
            return value;
         }
      }

      return null;
   }
}
