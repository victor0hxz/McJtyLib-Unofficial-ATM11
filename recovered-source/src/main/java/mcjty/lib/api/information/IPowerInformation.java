package mcjty.lib.api.information;

import javax.annotation.Nullable;

public interface IPowerInformation {
   long getEnergyDiffPerTick();

   @Nullable
   String getEnergyUnitName();

   boolean isMachineActive();

   boolean isMachineRunning();

   @Nullable
   String getMachineStatus();
}
