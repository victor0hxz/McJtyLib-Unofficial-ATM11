package mcjty.lib.builder;

import mcjty.lib.compat.theoneprobe.McJtyLibTOPDriver;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.gui.ManualEntry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;

public class BlockBuilder {
   @Deprecated
   public static final Properties STANDARD_IRON = standardIron();
   private Properties properties = standardIron();
   private boolean infusable = false;
   private final TooltipBuilder tooltipBuilder = new TooltipBuilder();
   private BlockEntitySupplier<BlockEntity> tileEntitySupplier;
   private TOPDriver topDriver = McJtyLibTOPDriver.DRIVER;
   private ManualEntry manualEntry = ManualEntry.EMPTY;

   public static Properties standardIron() {
      return Properties.of().mapColor(MapColor.METAL).strength(2.0F).requiresCorrectToolForDrops().sound(SoundType.METAL);
   }

   public Properties getProperties() {
      return this.properties;
   }

   public ManualEntry getManualEntry() {
      return this.manualEntry;
   }

   public boolean isInfusable() {
      return this.infusable;
   }

   public TooltipBuilder getTooltipBuilder() {
      return this.tooltipBuilder;
   }

   public BlockEntitySupplier<BlockEntity> getTileEntitySupplier() {
      return this.tileEntitySupplier;
   }

   public TOPDriver getTopDriver() {
      return this.topDriver;
   }

   public BlockBuilder properties(Properties properties) {
      this.properties = properties;
      return this;
   }

   public BlockBuilder topDriver(TOPDriver driver) {
      this.topDriver = driver;
      return this;
   }

   public BlockBuilder infusable() {
      this.infusable = true;
      return this;
   }

   public BlockBuilder manualEntry(ManualEntry manualEntry) {
      this.manualEntry = manualEntry;
      return this;
   }

   public BlockBuilder info(InfoLine... lines) {
      this.tooltipBuilder.info(lines);
      return this;
   }

   public BlockBuilder infoShift(InfoLine... lines) {
      this.tooltipBuilder.infoShift(lines);
      return this;
   }

   public BlockBuilder infoAdvanced(InfoLine... lines) {
      this.tooltipBuilder.infoAdvanced(lines);
      return this;
   }

   public BlockBuilder tileEntitySupplier(BlockEntitySupplier<BlockEntity> supplier) {
      this.tileEntitySupplier = supplier;
      return this;
   }
}
