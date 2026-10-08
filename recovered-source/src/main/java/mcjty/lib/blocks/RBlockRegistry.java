package mcjty.lib.blocks;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import mcjty.lib.tileentity.AnnotationHolder;
import mcjty.lib.tileentity.AnnotationTools;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RBlockRegistry {
   private final DeferredBlocks BLOCKS;
   private final DeferredItems ITEMS;
   private final DeferredRegister<BlockEntityType<?>> TILES;
   private final Consumer<Supplier<ItemStack>> tab;
   private final List<AnnotationHolder> holders = new ArrayList<>();
   private final Map<DeferredBlock<?>, DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>>> blockEntityTypes = new IdentityHashMap<>();

   public RBlockRegistry(String modid, Consumer<Supplier<ItemStack>> tab) {
      this.BLOCKS = DeferredBlocks.create(modid);
      this.ITEMS = DeferredItems.create(modid);
      this.TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modid);
      this.tab = tab;
   }

   public void register(IEventBus bus) {
      this.BLOCKS.register(bus);
      this.ITEMS.register(bus);
      this.TILES.register(bus);
   }

   public List<AnnotationHolder> getHolders() {
      return this.holders;
   }

   @Nullable
   public DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> getBlockEntityType(DeferredBlock<?> block) {
      return this.blockEntityTypes.get(block);
   }

   public <B extends BaseBlock, I extends BlockItem, E extends GenericTileEntity> RBlock<B, I, E> registerBlock(
      String name,
      Class<E> clazz,
      Supplier<B> blockSupplier,
      @Nullable Function<Supplier<? extends Block>, I> itemSupplier,
      BlockEntitySupplier<E> tileSupplier
   ) {
      DeferredBlock<B> block = this.BLOCKS.register(name, blockSupplier);
      DeferredItem<I> item;
      if (itemSupplier != null) {
         item = this.ITEMS.register(name, () -> itemSupplier.apply(block));
         this.tab.accept(() -> new ItemStack((ItemLike)item.get()));
      } else {
         item = null;
      }

      DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> tile = this.TILES
         .register(name, () -> new BlockEntityType(tileSupplier, new Block[]{(Block)block.get()}));
      this.blockEntityTypes.put(block, tile);
      AnnotationHolder holder = AnnotationTools.createAnnotationHolder(clazz, block);
      this.holders.add(holder);
      return new RBlock<>(block, item, tile);
   }

   public <B extends BaseBlock, I extends BlockItem, E extends GenericTileEntity> RBlock<B, I, E> registerBlockWIP(
      String name, Class<E> clazz, Supplier<B> blockSupplier, Function<Supplier<? extends Block>, I> itemSupplier, BlockEntitySupplier<E> tileSupplier
   ) {
      DeferredBlock<B> block = this.BLOCKS.register(name, blockSupplier);
      DeferredItem<I> item = this.ITEMS.register(name, () -> itemSupplier.apply(block));
      DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> tile = this.TILES
         .register(name, () -> new BlockEntityType(tileSupplier, new Block[]{(Block)block.get()}));
      this.blockEntityTypes.put(block, tile);
      AnnotationHolder holder = AnnotationTools.createAnnotationHolder(clazz, block);
      this.holders.add(holder);
      return new RBlock<>(block, item, tile);
   }

   public <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> itemSupplier) {
      DeferredItem<T> item = this.ITEMS.register(name, itemSupplier);
      this.tab.accept(() -> new ItemStack((ItemLike)item.get()));
      return item;
   }

   public <T extends Item> DeferredItem<T> registerItemWIP(String name, Supplier<T> itemSupplier) {
      return this.ITEMS.register(name, itemSupplier);
   }
}
