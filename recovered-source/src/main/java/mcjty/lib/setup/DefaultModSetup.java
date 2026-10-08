package mcjty.lib.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import mcjty.lib.api.ITabExpander;
import mcjty.lib.blocks.RBlockRegistry;
import mcjty.lib.tileentity.AnnotationHolder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.LegacyCapabilities;
import mcjty.lib.varia.LegacyItemResourceHandlerAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.Energy;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class DefaultModSetup {
   private Logger logger;
   protected CreativeModeTab creativeTab;
   private List<Supplier<ItemStack>> tabItems = new ArrayList<>();

   public void init(FMLCommonSetupEvent e) {
      this.logger = LogManager.getLogger();
      this.setupModCompat();
   }

   protected abstract void setupModCompat();

   public void populateTab(Output output) {
      this.tabItems.forEach(s -> {
         boolean todo = true;
         ItemStack st = s.get();
         if (st.getItem() instanceof ITabExpander expander) {
            List<ItemStack> itemsForTab = expander.getItemsForTab();
            if (!itemsForTab.isEmpty()) {
               todo = false;
               itemsForTab.forEach(output::accept);
            }
         }

         if (todo) {
            output.accept(st);
         }
      });
   }

   public Logger getLogger() {
      return this.logger;
   }

   public Properties defaultProperties() {
      return RegistrationContext.prepareItemProperties(new Properties());
   }

   public CreativeModeTab getTab() {
      return this.creativeTab;
   }

   public void addTabItem(Supplier<ItemStack> item) {
      this.tabItems.add(item);
   }

   public <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
      Lazy<T> lazyItem = Lazy.of(supplier);
      this.tabItems.add(() -> new ItemStack((ItemLike)lazyItem.get()));
      return lazyItem;
   }

   public Consumer<RegisterCapabilitiesEvent> getBlockCapabilityRegistrar(RBlockRegistry registry) {
      return event -> {
         for (AnnotationHolder holder : registry.getHolders()) {
            for (int i = 0; i < holder.getCapSize(); i++) {
               AnnotationHolder.CapHolder<Object, Object> hd = holder.getCapHolder(i);
               BlockCapability<Object, Object> bc = hd.capability();
               final Function<? super GenericTileEntity, Object> function = hd.function();
               DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> beType = registry.getBlockEntityType(hd.block());
               if (beType != null) {
                  registerBlockEntityCapability(event, bc, (BlockEntityType)beType.get(), function);
               } else {
                  event.registerBlock(bc, new IBlockCapabilityProvider<Object, Object>() {
                     {
                        Objects.requireNonNull(DefaultModSetup.this);
                     }

                     @Nullable
                     public Object getCapability(Level level, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, Object o) {
                        return blockEntity instanceof GenericTileEntity be ? function.apply(be) : null;
                     }
                  }, new Block[]{(Block)hd.block().get()});
               }

               if (bc == LegacyCapabilities.ENERGY_BLOCK) {
                  if (beType != null) {
                     registerNativeEnergyCapability(event, (BlockEntityType)beType.get(), function);
                  } else {
                     event.registerBlock(
                        Energy.BLOCK,
                        (level, blockPos, blockState, blockEntity, side) -> blockEntity instanceof GenericTileEntity be
                              && function.apply(be) instanceof EnergyHandler handler
                           ? handler
                           : null,
                        new Block[]{(Block)hd.block().get()}
                     );
                  }
               }

               if (bc == LegacyCapabilities.ITEM_BLOCK) {
                  if (beType != null) {
                     registerNativeItemCapability(event, (BlockEntityType)beType.get(), function);
                  } else {
                     event.registerBlock(
                        net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK,
                        (level, blockPos, blockState, blockEntity, side) -> blockEntity instanceof GenericTileEntity be
                              && function.apply(be) instanceof IItemHandler handler
                           ? new LegacyItemResourceHandlerAdapter(handler)
                           : null,
                        new Block[]{(Block)hd.block().get()}
                     );
                  }
               }
            }
         }
      };
   }

   private static void registerBlockEntityCapability(
      RegisterCapabilitiesEvent event, BlockCapability capability, BlockEntityType blockEntityType, Function<? super GenericTileEntity, Object> function
   ) {
      event.registerBlockEntity(capability, blockEntityType, (blockEntity, context) -> blockEntity instanceof GenericTileEntity be ? function.apply(be) : null);
   }

   private static void registerNativeEnergyCapability(
      RegisterCapabilitiesEvent event, BlockEntityType blockEntityType, Function<? super GenericTileEntity, Object> function
   ) {
      event.registerBlockEntity(
         Energy.BLOCK,
         blockEntityType,
         (blockEntity, side) -> blockEntity instanceof GenericTileEntity be && function.apply(be) instanceof EnergyHandler handler ? handler : null
      );
   }

   private static void registerNativeItemCapability(
      RegisterCapabilitiesEvent event, BlockEntityType blockEntityType, Function<? super GenericTileEntity, Object> function
   ) {
      event.registerBlockEntity(
         net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK,
         blockEntityType,
         (blockEntity, side) -> blockEntity instanceof GenericTileEntity be && function.apply(be) instanceof IItemHandler handler
            ? new LegacyItemResourceHandlerAdapter(handler)
            : null
      );
   }
}
