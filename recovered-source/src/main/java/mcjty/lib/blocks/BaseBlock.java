package mcjty.lib.blocks;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.api.ITabExpander;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.ItemInfusable;
import mcjty.lib.api.module.CapabilityModuleSupport;
import mcjty.lib.api.module.IModuleSupport;
import mcjty.lib.api.power.ItemEnergy;
import mcjty.lib.api.security.ItemSecurity;
import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.compat.theoneprobe.TOPDriver;
import mcjty.lib.compat.theoneprobe.TOPInfoProvider;
import mcjty.lib.compat.waila.WailaInfoProvider;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.multipart.IPartBlock;
import mcjty.lib.multipart.PartSlot;
import mcjty.lib.setup.Registration;
import mcjty.lib.setup.RegistrationContext;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.ModuleTools;
import mcjty.lib.varia.OrientationTools;
import mcjty.lib.varia.Tools;
import mcjty.lib.varia.WrenchChecker;
import mcjty.lib.varia.WrenchUsage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public class BaseBlock extends Block implements WailaInfoProvider, TOPInfoProvider, IPartBlock, ITooltipSettings, EntityBlock, ITabExpander {
   private final boolean infusable;
   private final BlockEntitySupplier<BlockEntity> tileEntitySupplier;
   private final TooltipBuilder tooltipBuilder;
   private final TOPDriver topDriver;
   private final ManualEntry manualEntry;
   public static final Property<?>[] HORIZ_PROPERTIES = new Property[]{BlockStateProperties.HORIZONTAL_FACING};
   public static final Property<?>[] ROTATING_PROPERTIES = new Property[]{BlockStateProperties.FACING};
   public static final Property<?>[] NONE_PROPERTIES = new Property[0];

   public BaseBlock(BlockBuilder builder) {
      super(RegistrationContext.prepareBlockProperties(builder.getProperties()));
      this.infusable = builder.isInfusable();
      this.tileEntitySupplier = builder.getTileEntitySupplier();
      this.tooltipBuilder = builder.getTooltipBuilder();
      this.topDriver = builder.getTopDriver();
      this.manualEntry = builder.getManualEntry();
   }

   @Override
   public ManualEntry getManualEntry() {
      return this.manualEntry;
   }

   public static void setInfused(ItemStack stack, int infused) {
      stack.set(Registration.ITEM_INFUSABLE, new ItemInfusable(infused));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
      this.intAddInformation(stack, tooltip);
      if (this.tooltipBuilder.isActive()) {
         this.tooltipBuilder.makeTooltip(Tools.getId(this), stack, tooltip, flags);
      }
   }

   private void intAddInformation(ItemStack itemStack, List<Component> list) {
      ItemEnergy itemEnergy = (ItemEnergy)itemStack.get(Registration.ITEM_ENERGY);
      if (itemEnergy != null) {
         list.add(ComponentFactory.literal(ChatFormatting.GREEN + "Energy: " + itemEnergy.energy() + " rf"));
      }

      ItemInfusable itemInfusable = (ItemInfusable)itemStack.get(Registration.ITEM_INFUSABLE);
      if (itemInfusable != null) {
         int infused = itemInfusable.infused();
         int pct = infused * 100 / (Integer)GeneralConfig.maxInfuse.get();
         list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Infused: " + pct + "%"));
      }

      ItemSecurity itemSecurity = (ItemSecurity)itemStack.get(Registration.ITEM_SECURITY);
      if (itemSecurity != null) {
         String owner = itemSecurity.owner();
         int securityChannel = itemSecurity.channel();
         if (securityChannel == -1) {
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Owned by: " + owner));
         } else {
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Owned by: " + owner + " (channel " + securityChannel + ")"));
         }
      }
   }

   public static int getInfused(ItemStack itemStack) {
      ItemInfusable itemInfusable = (ItemInfusable)itemStack.get(Registration.ITEM_INFUSABLE);
      return itemInfusable != null ? itemInfusable.infused() : 0;
   }

   private WrenchUsage testWrenchUsage(BlockPos pos, Player player) {
      ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
      WrenchUsage wrenchUsed = WrenchUsage.NOT;
      if (!itemStack.isEmpty()) {
         Item item = itemStack.getItem();
         wrenchUsed = this.getWrenchUsage(pos, player, itemStack, wrenchUsed, item);
      }

      if (wrenchUsed == WrenchUsage.NORMAL && player.isShiftKeyDown()) {
         wrenchUsed = WrenchUsage.SNEAKING;
      }

      return wrenchUsed;
   }

   protected WrenchUsage getWrenchUsage(BlockPos pos, Player player, ItemStack itemStack, WrenchUsage wrenchUsed, Item item) {
      if (item instanceof SmartWrench wrench) {
         return switch (wrench.getMode(itemStack)) {
            case MODE_WRENCH -> WrenchUsage.NORMAL;
            case MODE_SELECT -> player.isShiftKeyDown() ? WrenchUsage.SNEAK_SELECT : WrenchUsage.SELECT;
         };
      } else {
         return WrenchChecker.isAWrench(item) ? WrenchUsage.NORMAL : wrenchUsed;
      }
   }

   protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult result) {
      InteractionHand hand = player.getUsedItemHand();
      if (world.getBlockEntity(pos) instanceof GenericTileEntity genTileEntity) {
         InteractionResult resultType = genTileEntity.onBlockActivated(state, player, hand, result);
         if (resultType != InteractionResult.PASS) {
            return resultType;
         }
      }

      ItemStack heldItem = player.getItemInHand(hand);
      if (this.handleModule(world, pos, state, player, hand, heldItem, result)) {
         return InteractionResult.SUCCESS;
      } else {
         WrenchUsage wrenchUsed = this.testWrenchUsage(pos, player);

         return (InteractionResult)(switch (wrenchUsed) {
            case NOT -> this.openGui(world, pos.getX(), pos.getY(), pos.getZ(), player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case NORMAL -> this.wrenchUse(world, pos, result.getDirection(), player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case SNEAKING -> this.wrenchSneak(world, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case DISABLED -> this.wrenchDisabled(world, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case SELECT -> this.wrenchSelect(world, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
            case SNEAK_SELECT -> this.wrenchSneakSelect(world, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
         });
      }
   }

   public boolean handleModule(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, ItemStack heldItem, BlockHitResult result) {
      if (!heldItem.isEmpty()) {
         BlockEntity te = world.getBlockEntity(pos);
         if (te != null) {
            IModuleSupport h = (IModuleSupport)world.getCapability(CapabilityModuleSupport.MODULE_CAPABILITY, pos, null);
            if (h != null && h.isModule(heldItem)) {
               return ModuleTools.installModule(player, heldItem, hand, pos, h.getFirstSlot(), h.getLastSlot());
            }
         }
      }

      return false;
   }

   protected boolean wrenchUse(Level world, BlockPos pos, Direction side, Player player) {
      if (world.getBlockEntity(pos) instanceof GenericTileEntity genTileEntity) {
         if (!genTileEntity.wrenchUse(world, pos, side, player)) {
            BlockState state = this.rotate(world.getBlockState(pos), world, pos, Rotation.CLOCKWISE_90);
            world.setBlock(pos, state, 3);
         }
      } else {
         BlockState state = this.rotate(world.getBlockState(pos), world, pos, Rotation.CLOCKWISE_90);
         world.setBlock(pos, state, 3);
      }

      return true;
   }

   protected boolean wrenchSneak(Level world, BlockPos pos, Player player) {
      this.breakAndRemember(world, player, pos);
      return true;
   }

   protected void breakAndRemember(Level world, Player player, BlockPos pos) {
      if (!world.isClientSide()) {
         this.playerDestroy(world, player, pos, world.getBlockState(pos), world.getBlockEntity(pos), ItemStack.EMPTY);
         world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
      }
   }

   protected boolean wrenchDisabled(Level world, BlockPos pos, Player player) {
      return false;
   }

   protected boolean wrenchSelect(Level world, BlockPos pos, Player player) {
      return false;
   }

   protected boolean wrenchSneakSelect(Level world, BlockPos pos, Player player) {
      return false;
   }

   protected boolean openGui(Level world, int x, int y, int z, Player player) {
      BlockEntity te = world.getBlockEntity(new BlockPos(x, y, z));
      if (te == null) {
         return false;
      } else {
         MenuProvider h = (MenuProvider)world.getCapability(CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY, new BlockPos(x, y, z), null);
         if (h != null) {
            if (world.isClientSide()) {
               return true;
            } else if (this.checkAccess(world, player, te)) {
               return true;
            } else {
               player.openMenu(h, buf -> {
                  buf.writeBlockPos(te.getBlockPos());
                  if (h instanceof DefaultContainerProvider<?> provider) {
                     provider.writeExtraData(buf, te);
                  }
               });
               return true;
            }
         } else {
            return false;
         }
      }
   }

   public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
      super.setPlacedBy(world, pos, state, placer, stack);
      if (!world.isClientSide() && (Boolean)GeneralConfig.manageOwnership.get()) {
         this.setOwner(world, pos, placer);
      }

      if (world.getBlockEntity(pos) instanceof GenericTileEntity genericTileEntity) {
         genericTileEntity.onBlockPlacedBy(world, pos, state, placer, stack);
      }

      if (!world.isClientSide()) {
         world.invalidateCapabilities(pos);
      }

      this.checkRedstone(world, pos);
   }

   protected void neighborChanged(
      @Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nullable Orientation orientation, boolean isMoving
   ) {
      super.neighborChanged(state, world, pos, blockIn, orientation, isMoving);
      this.checkRedstone(world, pos);
   }

   protected void checkRedstone(Level world, BlockPos pos) {
      if (world.getBlockEntity(pos) instanceof GenericTileEntity genericTileEntity) {
         genericTileEntity.checkRedstone(world, pos);
      }
   }

   protected void setOwner(Level world, BlockPos pos, LivingEntity entity) {
      if (world.getBlockEntity(pos) instanceof GenericTileEntity genericTileEntity && entity instanceof Player player) {
         genericTileEntity.setOwner(player);
      }
   }

   public boolean triggerEvent(@Nonnull BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, int id, int param) {
      if (!this.hasTileEntitySupplier()) {
         return super.triggerEvent(state, worldIn, pos, id, param);
      } else {
         super.triggerEvent(state, worldIn, pos, id, param);
         BlockEntity tileentity = worldIn.getBlockEntity(pos);
         return tileentity != null && tileentity.triggerEvent(id, param);
      }
   }

   protected boolean checkAccess(Level world, Player player, BlockEntity te) {
      if (te instanceof GenericTileEntity genericTileEntity) {
         genericTileEntity.checkAccess(player);
      }

      return false;
   }

   protected boolean hasTileEntitySupplier() {
      return this.tileEntitySupplier != null;
   }

   public RotationType getRotationType() {
      return RotationType.ROTATION;
   }

   public boolean isInfusable() {
      return this.infusable;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return this.hasTileEntitySupplier() ? this.tileEntitySupplier.create(pos, state) : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState blockState, BlockEntityType<T> blockEntityType) {
      return BaseBlock::runTick;
   }

   public static void runTick(Level world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
      if (blockEntity instanceof TickingTileEntity ticking) {
         ticking.tick();
      }
   }

   @Nonnull
   @Override
   public PartSlot getSlotFromState(Level world, BlockPos pos, BlockState newState) {
      return PartSlot.NONE;
   }

   public void onRemove(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState newstate, boolean isMoving) {
      if (!world.isClientSide() && world.getBlockEntity(pos) instanceof GenericTileEntity genericTileEntity) {
         genericTileEntity.onReplaced(world, pos, state, newstate);
      }
   }

   protected Property<?>[] getProperties() {
      return getProperties(this.getRotationType());
   }

   public static Property<?>[] getProperties(RotationType rotationType) {
      return switch (rotationType) {
         case HORIZROTATION -> HORIZ_PROPERTIES;
         case ROTATION -> ROTATING_PROPERTIES;
         case NONE -> NONE_PROPERTIES;
      };
   }

   protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
      for (Property<?> property : this.getProperties()) {
         builder.add(new Property[]{property});
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Player placer = context.getPlayer();
      BlockPos pos = context.getClickedPos();
      BlockState state = super.getStateForPlacement(context);

      return switch (this.getRotationType()) {
         case HORIZROTATION -> (BlockState)state.setValue(BlockStateProperties.HORIZONTAL_FACING, placer.getDirection().getOpposite());
         case ROTATION -> (BlockState)state.setValue(BlockStateProperties.FACING, OrientationTools.getFacingFromEntity(pos, placer));
         case NONE -> state;
      };
   }

   public Direction getFrontDirection(BlockState state) {
      return switch (this.getRotationType()) {
         case HORIZROTATION -> (Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING);
         case ROTATION -> (Direction)state.getValue(BlockStateProperties.FACING);
         case NONE -> Direction.NORTH;
      };
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return switch (this.getRotationType()) {
         case HORIZROTATION -> (BlockState)state.setValue(
            BlockStateProperties.HORIZONTAL_FACING, rot.rotate((Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING))
         );
         case ROTATION -> (BlockState)state.setValue(BlockStateProperties.FACING, rot.rotate((Direction)state.getValue(BlockStateProperties.FACING)));
         case NONE -> state;
      };
   }

   public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation rot) {
      state = this.rotate(state, rot);
      if (world.getBlockEntity(pos) instanceof GenericTileEntity genericTileEntity) {
         genericTileEntity.rotateBlock(rot);
      }

      return state;
   }

   public Direction getRightDirection(BlockState state) {
      return this.getFrontDirection(state).getCounterClockWise();
   }

   public Direction getLeftDirection(BlockState state) {
      return this.getFrontDirection(state).getClockWise();
   }

   public static Direction getFrontDirection(RotationType rotationType, BlockState state) {
      return switch (rotationType) {
         case HORIZROTATION -> OrientationTools.getOrientationHoriz(state);
         case ROTATION -> OrientationTools.getOrientation(state);
         case NONE -> Direction.SOUTH;
      };
   }

   @Override
   public TOPDriver getProbeDriver() {
      return this.topDriver;
   }

   @Override
   public List<ItemStack> getItemsForTab() {
      return Collections.emptyList();
   }
}
