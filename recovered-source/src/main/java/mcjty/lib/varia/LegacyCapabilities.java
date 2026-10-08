package mcjty.lib.varia;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jspecify.annotations.Nullable;

public final class LegacyCapabilities {
   public static final BlockCapability<IItemHandler, @Nullable Direction> ITEM_BLOCK = BlockCapability.createSided(
      Identifier.fromNamespaceAndPath("mcjtylib", "legacy_item_handler"), IItemHandler.class
   );
   public static final BlockCapability<IFluidHandler, @Nullable Direction> FLUID_BLOCK = BlockCapability.createSided(
      Identifier.fromNamespaceAndPath("mcjtylib", "legacy_fluid_handler"), IFluidHandler.class
   );
   public static final BlockCapability<IEnergyStorage, @Nullable Direction> ENERGY_BLOCK = BlockCapability.createSided(
      Identifier.fromNamespaceAndPath("mcjtylib", "legacy_energy_storage"), IEnergyStorage.class
   );
   public static final ItemCapability<IEnergyStorage, @Nullable Void> ENERGY_ITEM = ItemCapability.createVoid(
      Identifier.fromNamespaceAndPath("mcjtylib", "legacy_energy_storage"), IEnergyStorage.class
   );

   private LegacyCapabilities() {
   }
}
