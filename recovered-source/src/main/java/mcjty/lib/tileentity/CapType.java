package mcjty.lib.tileentity;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.information.CapabilityPowerInformation;
import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import mcjty.lib.varia.LegacyCapabilities;
import net.neoforged.neoforge.capabilities.BlockCapability;

public enum CapType {
   ITEMS(LegacyCapabilities.ITEM_BLOCK),
   ITEMS_AUTOMATION(LegacyCapabilities.ITEM_BLOCK),
   CONTAINER(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY),
   ENERGY(LegacyCapabilities.ENERGY_BLOCK),
   INFUSABLE(CapabilityInfusable.INFUSABLE_CAPABILITY),
   MODULE(CapabilityModuleSupport.MODULE_CAPABILITY),
   POWER_INFO(CapabilityPowerInformation.POWER_INFORMATION_CAPABILITY),
   FLUIDS(LegacyCapabilities.FLUID_BLOCK);

   private final BlockCapability capability;

   private CapType(BlockCapability capability) {
      this.capability = capability;
   }

   public BlockCapability getCapability() {
      return this.capability;
   }
}
