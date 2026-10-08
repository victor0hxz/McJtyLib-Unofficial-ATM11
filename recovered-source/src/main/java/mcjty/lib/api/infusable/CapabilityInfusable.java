package mcjty.lib.api.infusable;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilityInfusable {
   public static final BlockCapability<IInfusable, @Nullable Direction> INFUSABLE_CAPABILITY = BlockCapability.createSided(
      Identifier.fromNamespaceAndPath("mcjtylib", "infusable"), IInfusable.class
   );
}
