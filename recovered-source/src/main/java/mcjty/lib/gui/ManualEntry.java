package mcjty.lib.gui;

import net.minecraft.resources.Identifier;

public record ManualEntry(Identifier manual, Identifier entry, int page) {
   public static final ManualEntry EMPTY = new ManualEntry(null, null);

   public ManualEntry(Identifier manual, Identifier entry) {
      this(manual, entry, 0);
   }
}
