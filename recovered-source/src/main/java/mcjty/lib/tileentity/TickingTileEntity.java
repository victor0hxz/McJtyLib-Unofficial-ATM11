package mcjty.lib.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TickingTileEntity extends GenericTileEntity {
   public TickingTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public void tick() {
      if (this.level != null) {
         if (this.level.isClientSide()) {
            this.tickClient();
         } else {
            this.tickServer();
         }
      }
   }

   protected void tickServer() {
   }

   protected void tickClient() {
   }
}
