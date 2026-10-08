package mcjty.lib.tooltips;

import mcjty.lib.gui.ManualEntry;

public interface ITooltipSettings {
   default int getMaxWidth() {
      return 200;
   }

   default ManualEntry getManualEntry() {
      return ManualEntry.EMPTY;
   }
}
