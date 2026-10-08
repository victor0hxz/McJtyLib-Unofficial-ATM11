package mcjty.lib.tileentity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.IRunnable;
import mcjty.lib.blockcommands.IRunnableWithList;
import mcjty.lib.blockcommands.IRunnableWithListResult;
import mcjty.lib.blockcommands.IRunnableWithResult;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.setup.Registration;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;

public class GenericTileEntity extends BlockEntity {
   public static final Key<Integer> VALUE_RSMODE = new Key<>("rsmode", Type.INTEGER);
   protected byte powerLevel;
   @ServerCommand
   public static final Command<?> COMMAND_SYNC_BINDING = Command.create("generic.syncBinding", (te, playerEntity, params) -> te.syncBinding(params));
   @ServerCommand
   public static final Command<?> CMD_RSMODE = Command.create(
      "mcjtylib.setRsMode", (te, playerEntity, params) -> te.setRSMode(RedstoneMode.values()[params.get(ImageChoiceLabel.PARAM_CHOICE_IDX)])
   );

   public GenericTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
      AnnotationHolder holder = this.getAnnotationHolder();
      if (holder == null) {
         AnnotationTools.createAnnotationHolder((Class<? extends GenericTileEntity>)this.getClass(), null);
      }
   }

   public void markDirtyClient() {
      this.setChanged();
      if (this.getLevel() != null) {
         BlockState state = this.getLevel().getBlockState(this.getBlockPos());
         this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
      }
   }

   public void markDirtyQuick() {
      if (this.getLevel() != null) {
         this.getLevel().blockEntityChanged(this.worldPosition);
      }
   }

   public Map<String, ValueHolder<?, ?>> getValueMap() {
      AnnotationHolder holder = this.getAnnotationHolder();
      return holder.valueMap;
   }

   public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
   }

   public void onReplaced(Level world, BlockPos pos, BlockState state, BlockState newstate) {
   }

   public void onPartAdded(PartSlot slot, BlockState state, BlockEntity multipartTile) {
   }

   public void onDataChanged(AttachmentType<?> type, Object oldData, Object newData) {
   }

   public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult result) {
      return InteractionResult.PASS;
   }

   protected boolean needsRedstoneMode() {
      return false;
   }

   public void checkRedstone(Level world, BlockPos pos) {
      int powered = world.getBestNeighborSignal(pos);
      this.setPowerInput(powered);
   }

   public void setPowerInput(int powered) {
      if (this.powerLevel != powered) {
         this.powerLevel = (byte)powered;
         this.setChanged();
      }
   }

   public int getPowerLevel() {
      return this.powerLevel;
   }

   public RedstoneMode getRSMode() {
      return ((BaseBEData)this.getData(Registration.BASE_BE_DATA)).rsMode();
   }

   public void setRSMode(RedstoneMode redstoneMode) {
      BaseBEData data = ((BaseBEData)this.getData(Registration.BASE_BE_DATA)).withRedstoneMode(redstoneMode);
      this.setData(Registration.BASE_BE_DATA, data);
      this.markDirtyClient();
   }

   public void setRSModeInt(int i) {
      this.setRSMode(RedstoneMode.values()[i]);
   }

   public int getRSModeInt() {
      return this.getRSMode().ordinal();
   }

   public boolean isMachineEnabled() {
      BaseBEData data = (BaseBEData)this.getData(Registration.BASE_BE_DATA);
      if (data.rsMode() != RedstoneMode.REDSTONE_IGNORED) {
         boolean rs = this.powerLevel > 0;
         if (data.rsMode() == RedstoneMode.REDSTONE_OFFREQUIRED && rs) {
            return false;
         }

         if (data.rsMode() == RedstoneMode.REDSTONE_ONREQUIRED && !rs) {
            return false;
         }
      }

      return true;
   }

   public void onSlotChanged(int index, ItemStack stack) {
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      CompoundTag tag = new CompoundTag();
      this.saveClientDataToNBT(tag, this.level.registryAccess());
      return ClientboundBlockEntityDataPacket.create(this, (entity, access) -> tag);
   }

   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = super.getUpdateTag(provider);
      this.saveClientDataToNBT(tag, provider);
      return tag;
   }

   public void onDataPacket(Connection net, ValueInput input) {
      super.onDataPacket(net, input);
   }

   public void handleUpdateTag(ValueInput input) {
      super.handleUpdateTag(input);
   }

   public boolean canPlayerAccess(Player player) {
      return !this.isRemoved()
         && player.distanceToSqr(new Vec3(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ()).add(new Vec3(0.5, 0.5, 0.5)))
            <= 64.0;
   }

   protected void saveAdditional(ValueOutput output) {
      super.saveAdditional(output);
      output.putByte("power", this.powerLevel);
   }

   protected void loadAdditional(ValueInput input) {
      super.loadAdditional(input);
      this.powerLevel = input.getByteOr("power", (byte)0);
   }

   @Deprecated
   protected void loadAdditional(CompoundTag tag, Provider provider) {
      this.loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, provider, tag));
   }

   @Deprecated
   protected void saveAdditional(CompoundTag tag, Provider provider) {
      TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
      this.saveAdditional(output);
      tag.merge(output.buildResult());
   }

   public void saveClientDataToNBT(CompoundTag tagCompound, Provider provider) {
   }

   public void loadClientDataFromNBT(CompoundTag tagCompound, Provider provider) {
   }

   protected void collectImplicitComponents(Builder components) {
      super.collectImplicitComponents(components);
      components.set(Registration.ITEM_BASE_BE_DATA, (BaseBEData)this.getData(Registration.BASE_BE_DATA));
   }

   protected void applyImplicitComponents(DataComponentGetter input) {
      super.applyImplicitComponents(input);
      BaseBEData data = (BaseBEData)input.get(Registration.ITEM_BASE_BE_DATA);
      if (data != null) {
         this.setData(Registration.BASE_BE_DATA, data);
      }
   }

   public boolean setOwner(Player player) {
      if (!(Boolean)GeneralConfig.manageOwnership.get()) {
         return false;
      } else {
         BaseBEData data = (BaseBEData)this.getData(Registration.BASE_BE_DATA);
         if (data.ownerUUID() != null) {
            return false;
         } else {
            data = data.withOwner(player.getGameProfile().id(), player.getName().getString());
            this.setData(Registration.BASE_BE_DATA, data);
            this.markDirtyClient();
            return true;
         }
      }
   }

   public void clearOwner() {
      if ((Boolean)GeneralConfig.manageOwnership.get()) {
         BaseBEData data = (BaseBEData)this.getData(Registration.BASE_BE_DATA);
         data = data.withOwner(null, "");
         data = data.withSecurityChannel(-1);
         this.setData(Registration.BASE_BE_DATA, data);
         this.markDirtyClient();
      }
   }

   public void setSecurityChannel(int id) {
      BaseBEData data = (BaseBEData)this.getData(Registration.BASE_BE_DATA);
      data = data.withSecurityChannel(id);
      this.setData(Registration.BASE_BE_DATA, data);
      this.markDirtyClient();
   }

   public int getSecurityChannel() {
      return ((BaseBEData)this.getData(Registration.BASE_BE_DATA)).securityChannel();
   }

   public String getOwnerName() {
      return ((BaseBEData)this.getData(Registration.BASE_BE_DATA)).ownerName();
   }

   public UUID getOwnerUUID() {
      return ((BaseBEData)this.getData(Registration.BASE_BE_DATA)).ownerUUID();
   }

   public boolean checkAccess(Player player) {
      return false;
   }

   public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
      return -1;
   }

   public void getDrops(NonNullList<ItemStack> drops, BlockGetter world, BlockPos pos, BlockState metadata, int fortune) {
   }

   public void rotateBlock(Rotation axis) {
   }

   public boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
      return false;
   }

   private <T extends GenericTileEntity, V> BiConsumer<T, V> findSetter(Key<V> key) {
      for (Entry<String, ValueHolder<?, ?>> entry : this.getValueMap().entrySet()) {
         ValueHolder value = entry.getValue();
         if (key.name().equals(value.key().name())) {
            return value.setter();
         }
      }

      return null;
   }

   public ResourceKey<Level> getDimension() {
      return this.level.dimension();
   }

   public boolean executeClientCommand(String command, Player player, @Nonnull TypedMap params) {
      AnnotationHolder holder = this.getAnnotationHolder();
      IRunnable clientCommand = holder.clientCommands.get(command);
      if (clientCommand != null) {
         clientCommand.run(this, player, params);
         return true;
      } else {
         return false;
      }
   }

   public IRunnable<?> findServerCommand(String command) {
      AnnotationHolder holder = this.getAnnotationHolder();
      return holder.serverCommands.get(command);
   }

   public boolean executeServerCommand(String command, Player player, @Nonnull TypedMap params) {
      AnnotationHolder holder = this.getAnnotationHolder();
      IRunnable serverCommand = holder.serverCommands.get(command);
      if (serverCommand != null) {
         serverCommand.run(this, player, params);
         return true;
      } else {
         return false;
      }
   }

   public <T> List<T> executeServerCommandList(String command, Player player, @Nonnull TypedMap params, @Nonnull Class<T> type) {
      AnnotationHolder holder = this.getAnnotationHolder();
      IRunnableWithListResult cmd = holder.serverCommandsWithListResult.get(command);
      return cmd != null ? cmd.run(this, player, params) : Collections.emptyList();
   }

   public <T> boolean handleListFromServer(String command, Player player, @Nonnull TypedMap params, @Nonnull List<T> list) {
      AnnotationHolder holder = this.getAnnotationHolder();
      IRunnableWithList cmd = holder.clientCommandsWithList.get(command);
      if (cmd != null) {
         cmd.run(this, player, params, list);
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public TypedMap executeServerCommandWR(String command, Player player, @Nonnull TypedMap params) {
      AnnotationHolder holder = this.getAnnotationHolder();
      IRunnableWithResult serverCommand = holder.serverCommandsWithResult.get(command);
      return serverCommand != null ? serverCommand.run(this, player, params) : null;
   }

   private AnnotationHolder getAnnotationHolder() {
      return AnnotationHolder.annotations.get(this.getClass());
   }

   private <T> void syncBindingHelper(TypedMap params, Key<T> bkey) {
      T o = params.get(bkey);
      this.findSetter(bkey).accept((T)this, o);
   }

   private void syncBinding(TypedMap params) {
      for (Key<?> key : params.getKeys()) {
         this.syncBindingHelper(params, key);
      }

      this.markDirtyClient();
   }
}
