package mcjty.lib.api.infusable;

import mcjty.lib.base.GeneralConfig;

public interface IInfusable {
   int getInfused();

   void setInfused(int var1);

   default float getInfusedFactor() {
      return (float)this.getInfused() / ((Integer)GeneralConfig.maxInfuse.get()).intValue();
   }
}
