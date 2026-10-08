package mcjty.lib.api.container;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Sync;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.IItemHandler;

public class DefaultContainerProvider<C extends IGenericContainer> implements MenuProvider {
   private final String name;
   private BiFunction<Integer, Player, C> containerSupplier;
   private Supplier<? extends IItemHandler> itemHandler = () -> null;
   private Supplier<? extends GenericEnergyStorage> energyHandler = () -> null;
   private final List<DataSlot> integerListeners = new ArrayList<>();
   private final List<DataSlot> shortListeners = new ArrayList<>();
   private final List<IContainerDataListener> containerDataListeners = new ArrayList<>();
   private final List<IGenericContainer.DataListener<?, ?>> dataListeners = new ArrayList<>();

   public static BiFunction<Integer, Player, GenericContainer> empty(@Nonnull Supplier<MenuType<GenericContainer>> type, GenericTileEntity te) {
      return (windowId, player) -> new GenericContainer(type, windowId, ContainerFactory.EMPTY, te, player);
   }

   public static BiFunction<Integer, Player, GenericContainer> container(
      @Nonnull Supplier<MenuType<GenericContainer>> type, @Nonnull Supplier<ContainerFactory> factory, GenericTileEntity te
   ) {
      return (windowId, player) -> new GenericContainer(type, windowId, factory, te, player);
   }

   public DefaultContainerProvider(String name) {
      this.name = name;
   }

   @Nonnull
   public Component getDisplayName() {
      return ComponentFactory.literal(this.name);
   }

   public DefaultContainerProvider<C> containerSupplier(BiFunction<Integer, Player, C> containerSupplier) {
      this.containerSupplier = containerSupplier;
      return this;
   }

   public DefaultContainerProvider<C> itemHandler(Supplier<? extends IItemHandler> itemHandler) {
      this.itemHandler = itemHandler;
      return this;
   }

   public DefaultContainerProvider<C> energyHandler(Supplier<? extends GenericEnergyStorage> energyHandler) {
      this.energyHandler = energyHandler;
      return this;
   }

   public DefaultContainerProvider<C> dataListener(IContainerDataListener dataListener) {
      this.containerDataListeners.add(dataListener);
      return this;
   }

   public DefaultContainerProvider<C> integerListener(DataSlot holder) {
      this.integerListeners.add(holder);
      return this;
   }

   public DefaultContainerProvider<C> shortListener(DataSlot holder) {
      this.shortListeners.add(holder);
      return this;
   }

   public <T> DefaultContainerProvider<C> data(Supplier<AttachmentType<T>> type, StreamCodec<? extends ByteBuf, T> streamCodec, Codec<T> codec) {
      this.dataListeners.add(new IGenericContainer.DataListener(type.get(), streamCodec, codec));
      return this;
   }

   public DefaultContainerProvider<C> setupSync(GenericTileEntity te) {
      this.dataListener(Sync.values(Identifier.fromNamespaceAndPath("mcjtylib", "data"), te));
      return this;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
      C container = this.containerSupplier.apply(windowId, playerEntity);
      IItemHandler itemHandler = this.itemHandler.get();
      container.setupInventories(itemHandler, playerInventory);
      GenericEnergyStorage energyHandler = this.energyHandler.get();
      if (energyHandler != null) {
         energyHandler.addIntegerListeners(container);
      }

      for (DataSlot listener : this.integerListeners) {
         container.addIntegerListener(listener);
      }

      for (DataSlot listener : this.shortListeners) {
         container.addShortListener(listener);
      }

      for (IContainerDataListener dataListener : this.containerDataListeners) {
         container.addContainerDataListener(dataListener);
      }

      this.dataListeners.forEach(container::addDataListener);
      if (container instanceof GenericContainer) {
         ((GenericContainer)container).forceBroadcast();
      }

      return container.getAsContainer();
   }

   public void writeExtraData(RegistryFriendlyByteBuf buf, BlockEntity te) {
      this.dataListeners.forEach(pair -> correctType(pair).streamCodec().encode(buf, te.getData(correctType(pair).type())));
   }

   public static <T> IGenericContainer.DataListener<RegistryFriendlyByteBuf, T> correctType(IGenericContainer.DataListener dl) {
      return dl;
   }
}
