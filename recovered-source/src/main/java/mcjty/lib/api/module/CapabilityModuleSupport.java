package mcjty.lib.api.module;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilityModuleSupport {
   public static final BlockCapability<IModuleSupport, @Nullable Direction> MODULE_CAPABILITY = BlockCapability.createSided(
      Identifier.fromNamespaceAndPath("mcjtylib", "module_support"), IModuleSupport.class
   );
}
