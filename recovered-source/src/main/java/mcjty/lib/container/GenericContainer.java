package mcjty.lib.container;

import com.google.common.collect.Range;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketAttachmentData;
import mcjty.lib.network.PacketContainerDataToClient;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class GenericContainer extends AbstractContainerMenu implements IGenericContainer {
   protected final Map<String, IItemHandler> inventories = new HashMap<>();
   private final Map<Identifier, IContainerDataListener> containerData = new HashMap<>();
   private final List<IGenericContainer.DataListener<?, ?>> dataListeners = new ArrayList<>();
   private final Map<AttachmentType<?>, Object> previousData = new HashMap<>();
   private final ContainerFactory factory;
   protected final BlockPos pos;
   protected final GenericTileEntity be;
   private final List<DataSlot> intReferenceHolders = new ArrayList<>();
   private boolean doForce = true;
   private final Player player;
   @Nullable
   private Inventory playerInventory;
   private final Map<AttachmentType<?>, Object> attachmentData = new HashMap<>();

   public GenericContainer(@Nullable MenuType<?> type, int id, ContainerFactory factory, BlockPos pos, @Nullable GenericTileEntity be, @Nonnull Player player) {
      super(type, id);
      this.factory = factory;
      this.pos = pos;
      this.be = be;
      this.player = player;
   }

   public GenericContainer(
      @Nonnull Supplier<MenuType<GenericContainer>> type,
      int id,
      @Nonnull Supplier<ContainerFactory> factory,
      @Nullable GenericTileEntity be,
      @Nonnull Player player
   ) {
      super(type.get(), id);
      this.factory = factory.get();
      this.pos = be.getBlockPos();
      this.be = be;
      this.player = player;
   }

   @Override
   public AbstractContainerMenu getAsContainer() {
      return this;
   }

   public GenericTileEntity getBe() {
      return this.be;
   }

   public Player getPlayer() {
      return this.player;
   }

   public <T> T getAttachmentData(AttachmentType<T> type) {
      return (T)this.attachmentData.get(type);
   }

   @Nonnull
   protected DataSlot addDataSlot(@Nonnull DataSlot holder) {
      this.intReferenceHolders.add(holder);
      return super.addDataSlot(holder);
   }

   @Override
   public void addShortListener(DataSlot holder) {
      this.addDataSlot(holder);
   }

   @Override
   public void addIntegerListener(final DataSlot holder) {
      this.addDataSlot(new DataSlot() {
         private int lastKnown;

         {
            Objects.requireNonNull(GenericContainer.this);
         }

         public int get() {
            return holder.get() & 65535;
         }

         public void set(int val) {
            int full = holder.get();
            holder.set(full & -65536 | val & 65535);
         }

         public boolean checkAndClearUpdateFlag() {
            int i = this.get();
            boolean flag = i != this.lastKnown;
            this.lastKnown = i;
            return flag;
         }
      });
      this.addDataSlot(new DataSlot() {
         private int lastKnown;

         {
            Objects.requireNonNull(GenericContainer.this);
         }

         public int get() {
            return holder.get() >> 16 & 65535;
         }

         public void set(int val) {
            int full = holder.get();
            holder.set(full & 65535 | (val & 65535) << 16);
         }

         public boolean checkAndClearUpdateFlag() {
            int i = this.get();
            boolean flag = i != this.lastKnown;
            this.lastKnown = i;
            return flag;
         }
      });
   }

   @Override
   public void addContainerDataListener(IContainerDataListener data) {
      this.containerData.put(data.getId(), data);
   }

   @Override
   public void addDataListener(IGenericContainer.DataListener<?, ?> dataListener) {
      this.dataListeners.add(dataListener);
   }

   public void addInventory(String name, @Nullable IItemHandler inventory) {
      if (inventory != null) {
         this.inventories.put(name, inventory);
      }
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public IItemHandler getInventory(String name) {
      return this.inventories.get(name);
   }

   public boolean stillValid(@Nonnull Player player) {
      return this.be == null || this.be.canPlayerAccess(player);
   }

   public SlotType getSlotType(int index) {
      return this.factory.getSlotType(index);
   }

   @Nullable
   public Slot getSlotByInventoryAndIndex(String name, int index) {
      IItemHandler inv = this.inventories.get(name);
      if (inv == null) {
         return null;
      } else {
         for (Slot slot : this.slots) {
            if ("player".equals(name) && this.playerInventory != null && slot.container == this.playerInventory && slot.getSlotIndex() == index) {
               return slot;
            }

            if (slot instanceof SlotItemHandler) {
               IItemHandler itemHandler = ((SlotItemHandler)slot).getItemHandler();
               if (itemHandler == inv && slot.getSlotIndex() == index) {
                  return slot;
               }
            }
         }

         return null;
      }
   }

   @Override
   public void setupInventories(IItemHandler itemHandler, Inventory inventory) {
      this.addInventory("container", itemHandler);
      this.addInventory("player", new InvWrapper(inventory));
      this.playerInventory = inventory;
      this.generateSlots(inventory.player);
   }

   public void generateSlots(Player player) {
      for (SlotFactory slotFactory : this.factory.getSlots()) {
         IItemHandler inventory = this.inventories.get(slotFactory.inventoryName());
         int index = slotFactory.index();
         int x = slotFactory.x();
         int y = slotFactory.y();
         SlotType slotType = slotFactory.getSlotType();
         Slot slot = this.createSlot(slotFactory, player, inventory, index, x, y, slotType);
         this.addSlot(slot);
      }
   }

   protected Slot createSlot(SlotFactory slotFactory, Player playerEntity, IItemHandler inventory, int index, int x, int y, SlotType slotType) {
      if ("player".equals(slotFactory.inventoryName()) && this.playerInventory != null) {
         return new Slot(this.playerInventory, index, x, y);
      } else {
         Slot slot;
         if (slotType == SlotType.SLOT_GHOST) {
            slot = new GhostSlot(inventory, index, x, y);
         } else if (slotType == SlotType.SLOT_GHOSTOUT) {
            slot = new GhostOutputSlot(inventory, index, x, y);
         } else if (slotType == SlotType.SLOT_SPECIFICITEM) {
            final SlotDefinition slotDefinition = slotFactory.slotDefinition();
            slot = new SlotItemHandler(inventory, index, x, y) {
               {
                  Objects.requireNonNull(GenericContainer.this);
               }

               public boolean mayPlace(@Nonnull ItemStack stack) {
                  return slotDefinition.itemStackMatches(stack);
               }
            };
         } else if (slotType == SlotType.SLOT_CRAFTRESULT) {
            slot = new CraftingSlot(playerEntity, inventory, this.be, index, x, y).onCraft(slotFactory.slotDefinition().getOnCraft());
         } else {
            slot = new BaseSlot(inventory, this.be, index, x, y);
         }

         return slot;
      }
   }

   private boolean mergeItemStacks(ItemStack itemStack, SlotType slotType, boolean reverse) {
      return slotType == SlotType.SLOT_SPECIFICITEM
         ? this.mergeItemStacks(itemStack, definition -> definition.isSpecific() && definition.itemStackMatches(itemStack), reverse)
         : this.mergeItemStacks(itemStack, definition -> definition.getType() == slotType, reverse);
   }

   private boolean mergeItemStacks(ItemStack itemStack, Predicate<SlotDefinition> slotType, boolean reverse) {
      SlotRanges ranges = this.factory.getRanges(slotType);
      Set<Range<Integer>> set = ranges.asRanges();
      if (set.isEmpty()) {
         return false;
      } else {
         for (Range<Integer> r : ranges.asRanges()) {
            Integer start = (Integer)r.lowerEndpoint();
            int end = (Integer)r.upperEndpoint();
            if (this.moveItemStackTo(itemStack, start, end, reverse)) {
               return true;
            }
         }

         return false;
      }
   }

   @Nonnull
   public ItemStack quickMoveStack(@Nonnull Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack origStack = slot.getItem();
         itemstack = origStack.copy();
         if (this.factory.isSpecificItemSlot(index)) {
            if (!this.mergeItemStacks(origStack, SlotType.SLOT_PLAYERINV, true) && !this.mergeItemStacks(origStack, SlotType.SLOT_PLAYERHOTBAR, false)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(origStack, itemstack);
         } else if (!this.factory.isOutputSlot(index) && !this.factory.isInputSlot(index) && !this.factory.isGenericSlot(index)) {
            if (this.factory.isGhostSlot(index) || this.factory.isGhostOutputSlot(index)) {
               return ItemStack.EMPTY;
            }

            if (this.factory.isPlayerInventorySlot(index)) {
               if (!this.mergeItemStacks(origStack, SlotType.SLOT_SPECIFICITEM, false)
                  && !this.mergeItemStacks(origStack, SlotDefinition::isInput, false)
                  && !this.mergeItemStacks(origStack, SlotType.SLOT_PLAYERHOTBAR, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.factory.isPlayerHotbarSlot(index)) {
               if (!this.mergeItemStacks(origStack, SlotType.SLOT_SPECIFICITEM, false)
                  && !this.mergeItemStacks(origStack, SlotDefinition::isInput, false)
                  && !this.mergeItemStacks(origStack, SlotType.SLOT_PLAYERINV, false)) {
                  return ItemStack.EMPTY;
               }
            } else {
               Logging.log("Weird slot at index: " + index);
            }
         } else {
            if (!this.mergeItemStacks(origStack, SlotType.SLOT_SPECIFICITEM, false)
               && !this.mergeItemStacks(origStack, SlotType.SLOT_PLAYERINV, true)
               && !this.mergeItemStacks(origStack, SlotType.SLOT_PLAYERHOTBAR, false)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(origStack, itemstack);
         }

         if (origStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (origStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, origStack);
      }

      return itemstack;
   }

   protected boolean moveItemStackTo(@Nonnull ItemStack par1ItemStack, int fromIndex, int toIndex, boolean reverseOrder) {
      boolean result = false;
      int checkIndex = fromIndex;
      if (reverseOrder) {
         checkIndex = toIndex - 1;
      }

      ItemStack itemstack1 = ItemStack.EMPTY;
      if (par1ItemStack.isStackable()) {
         while (!par1ItemStack.isEmpty() && (!reverseOrder && checkIndex < toIndex || reverseOrder && checkIndex >= fromIndex)) {
            Slot slot = (Slot)this.slots.get(checkIndex);
            itemstack1 = slot.getItem();
            if (!itemstack1.isEmpty()
               && itemstack1.getItem() == par1ItemStack.getItem()
               && par1ItemStack.getDamageValue() == itemstack1.getDamageValue()
               && ItemStack.isSameItemSameComponents(par1ItemStack, itemstack1)
               && slot.mayPlace(par1ItemStack)) {
               int mergedSize = itemstack1.getCount() + par1ItemStack.getCount();
               int maxStackSize = Math.min(par1ItemStack.getMaxStackSize(), slot.getMaxStackSize());
               if (mergedSize <= maxStackSize) {
                  par1ItemStack.setCount(0);
                  itemstack1.setCount(Math.max(mergedSize, 0));
                  slot.setChanged();
                  result = true;
               } else if (itemstack1.getCount() < maxStackSize) {
                  int amount = -(maxStackSize - itemstack1.getCount());
                  par1ItemStack.grow(amount);
                  itemstack1.setCount(Math.max(maxStackSize, 0));
                  slot.setChanged();
                  result = true;
               }
            }

            if (reverseOrder) {
               checkIndex--;
            } else {
               checkIndex++;
            }
         }
      }

      if (!par1ItemStack.isEmpty()) {
         if (reverseOrder) {
            checkIndex = toIndex - 1;
         } else {
            checkIndex = fromIndex;
         }

         while (!reverseOrder && checkIndex < toIndex || reverseOrder && checkIndex >= fromIndex) {
            Slot slotx = (Slot)this.slots.get(checkIndex);
            itemstack1 = slotx.getItem();
            if (itemstack1.isEmpty() && slotx.mayPlace(par1ItemStack)) {
               ItemStack in = par1ItemStack.copy();
               int amount1 = Math.min(in.getCount(), slotx.getMaxStackSize());
               in.setCount(Math.max(amount1, 0));
               slotx.set(in);
               slotx.setChanged();
               if (in.getCount() >= par1ItemStack.getCount()) {
                  par1ItemStack.setCount(0);
               } else {
                  int amount = -in.getCount();
                  par1ItemStack.grow(amount);
               }

               result = true;
               break;
            }

            if (reverseOrder) {
               checkIndex--;
            } else {
               checkIndex++;
            }
         }
      }

      return result;
   }

   public void clicked(int index, int button, @Nonnull ContainerInput mode, @Nonnull Player player) {
      if (this.factory.isGhostSlot(index)) {
         Slot slot = this.getSlot(index);
         if (slot.hasItem()) {
            slot.set(ItemStack.EMPTY);
         }

         ItemStack clickedWith = this.getCarried();
         if (!clickedWith.isEmpty()) {
            ItemStack copy = clickedWith.copy();
            copy.setCount(1);
            slot.set(copy);
         }

         this.broadcastChanges();
      } else {
         super.clicked(index, button, mode, player);
      }
   }

   public IContainerDataListener getListener(Identifier id) {
      return this.containerData.get(id);
   }

   private void broadcast() {
      for (int i = 0; i < this.intReferenceHolders.size(); i++) {
         DataSlot holder = this.intReferenceHolders.get(i);

         for (ContainerListener listener : this.containerListeners) {
            listener.dataChanged(this, i, holder.get());
         }
      }

      if (this.player instanceof ServerPlayer serverPlayer) {
         for (IContainerDataListener data : this.containerData.values()) {
            ByteBuf newbuf = Unpooled.buffer();
            RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, serverPlayer.registryAccess(), ConnectionType.OTHER);
            data.toBytes(buffer);
            PacketContainerDataToClient packet = PacketContainerDataToClient.create(data.getId(), buffer);
            Networking.sendToPlayer(packet, serverPlayer);
         }

         this.dataListeners.forEach(pair -> {
            ByteBuf newbufx = Unpooled.buffer();
            RegistryFriendlyByteBuf bufferx = new RegistryFriendlyByteBuf(newbufx, serverPlayer.registryAccess(), ConnectionType.OTHER);
            Object data = this.be.getData(pair.type());
            this.previousData.put(pair.type(), data);
            pair.streamCodec().encode(bufferx, data);
            PacketAttachmentData packetx = PacketAttachmentData.create(NeoForgeRegistries.ATTACHMENT_TYPES.getKey(pair.type()), bufferx);
            Networking.sendToPlayer(packetx, serverPlayer);
         });
      }
   }

   public <O> StreamCodec<RegistryFriendlyByteBuf, O> getStreamCodecForType(AttachmentType<O> type) {
      for (IGenericContainer.DataListener<?, ?> dataListener : this.dataListeners) {
         if (dataListener.type() == type) {
            return (StreamCodec<RegistryFriendlyByteBuf, O>)dataListener.streamCodec();
         }
      }

      return null;
   }

   public <O> Codec<O> getCodecForType(AttachmentType<O> type) {
      for (IGenericContainer.DataListener<?, ?> dataListener : this.dataListeners) {
         if (dataListener.type() == type) {
            return (Codec<O>)dataListener.codec();
         }
      }

      return null;
   }

   public void receiveData(Identifier containerId, RegistryFriendlyByteBuf buffer) {
      AttachmentType<?> type = (AttachmentType<?>)NeoForgeRegistries.ATTACHMENT_TYPES.getValue(containerId);
      if (type == null) {
         Logging.log("Unknown container id: " + containerId);
      } else {
         StreamCodec codec = this.getStreamCodecForType(type);
         if (codec == null) {
            Logging.log("No codec for container id: " + containerId);
         } else {
            Object oldData = this.be.getData(type);
            Object newData = codec.decode(buffer);
            this.be.setData(type, newData);
            this.be.onDataChanged(type, oldData, newData);
            this.attachmentData.put(type, this.be.getData(type));
         }
      }
   }

   public void forceBroadcast() {
      this.doForce = true;
   }

   public void broadcastChanges() {
      if (this.doForce) {
         this.broadcast();
         this.doForce = false;
      }

      super.broadcastChanges();
      if (this.player instanceof ServerPlayer serverPlayer) {
         for (IContainerDataListener data : this.containerData.values()) {
            if (data.isDirtyAndClear()) {
               ByteBuf newbuf = Unpooled.buffer();
               RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, serverPlayer.registryAccess(), ConnectionType.OTHER);
               data.toBytes(buffer);
               PacketContainerDataToClient packet = PacketContainerDataToClient.create(data.getId(), buffer);
               Networking.sendToPlayer(packet, serverPlayer);
            }
         }

         this.dataListeners.forEach(pair -> {
            Object datax = this.be.getData(pair.type());
            Object oldData = this.previousData.get(pair.type());
            if (!Objects.equals(datax, oldData)) {
               this.previousData.put(pair.type(), datax);
               ByteBuf newbufx = Unpooled.buffer();
               RegistryFriendlyByteBuf bufferx = new RegistryFriendlyByteBuf(newbufx, serverPlayer.registryAccess(), ConnectionType.OTHER);
               pair.streamCodec().encode(bufferx, datax);
               PacketAttachmentData packetx = PacketAttachmentData.create(NeoForgeRegistries.ATTACHMENT_TYPES.getKey(pair.type()), bufferx);
               Networking.sendToPlayer(packetx, serverPlayer);
            }
         });
      }
   }

   public static <T extends AbstractContainerMenu> MenuType<T> createContainerType() {
      return IMenuTypeExtension.create((windowId, inv, data) -> {
         BlockPos pos = data.readBlockPos();
         BlockEntity te = inv.player.level().getBlockEntity(pos);
         if (te == null) {
            throw new IllegalStateException("Something went wrong getting the GUI");
         } else {
            MenuProvider capability = (MenuProvider)te.getLevel().getCapability(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY, pos, null);
            if (capability == null) {
               throw new IllegalStateException("Something went wrong getting the GUI");
            } else {
               AbstractContainerMenu menu = capability.createMenu(windowId, inv, inv.player);
               if (menu instanceof GenericContainer gc) {
                  gc.readExtraData(data);
               }

               return menu;
            }
         }
      });
   }

   private void readExtraData(RegistryFriendlyByteBuf buf) {
      this.dataListeners.forEach(pair -> {
         Object decoded = DefaultContainerProvider.correctType(pair).streamCodec().decode(buf);
         this.attachmentData.put(DefaultContainerProvider.correctType(pair).type(), decoded);
      });
   }

   public static <T extends GenericContainer, E extends GenericTileEntity> MenuType<T> createRemoteContainerType(
      BiFunction<ResourceKey<Level>, BlockPos, E> dummyTEFactory, GenericContainer.ContainerSupplier<T, E> containerFactory, int slots
   ) {
      return IMenuTypeExtension.create((windowId, inv, data) -> {
         BlockPos pos = data.readBlockPos();
         E te = dummyTEFactory.apply(LevelTools.getId(data.readIdentifier()), pos);
         CompoundTag compound = data.readNbt();
         RegistryAccess access = inv.player.level().registryAccess();
         te.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, access, compound));
         T container = containerFactory.create(windowId, pos, te, inv.player);
         container.setupInventories(new ItemStackHandler(slots), inv);
         return container;
      });
   }

   public interface ContainerSupplier<T extends GenericContainer, E extends GenericTileEntity> {
      T create(int var1, BlockPos var2, E var3, Player var4);
   }
}
