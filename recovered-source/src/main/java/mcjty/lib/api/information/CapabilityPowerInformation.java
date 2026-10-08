package mcjty.lib.api.information;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilityPowerInformation {
   public static final BlockCapability<IPowerInformation, @Nullable Direction> POWER_INFORMATION_CAPABILITY = BlockCapability.createSided(
      Identifier.fromNamespaceAndPath("mcjtylib", "power_information"), IPowerInformation.class
   );
}
