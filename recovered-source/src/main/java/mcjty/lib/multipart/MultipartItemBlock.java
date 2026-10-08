package mcjty.lib.multipart;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MultipartItemBlock extends BlockItem {
   public MultipartItemBlock(Block block) {
      super(block, new Properties());
   }

   public MultipartItemBlock(Block block, Properties properties) {
      super(block, properties);
   }

   protected boolean canPlace(@Nonnull BlockPlaceContext context, @Nonnull BlockState state) {
      return true;
   }

   @Nonnull
   public InteractionResult place(BlockPlaceContext context) {
      return InteractionResult.SUCCESS;
   }

   private boolean canFitInside(Block block, Level world, BlockPos pos, PartSlot slot) {
      return false;
   }

   @Nullable
   private BlockEntity createTileEntity(BlockPos pos, BlockState state) {
      return state.getBlock() instanceof EntityBlock entityBlock ? entityBlock.newBlockEntity(pos, state) : null;
   }

   protected boolean placeBlock(@Nonnull BlockPlaceContext context, @Nonnull BlockState state) {
      return false;
   }
}
