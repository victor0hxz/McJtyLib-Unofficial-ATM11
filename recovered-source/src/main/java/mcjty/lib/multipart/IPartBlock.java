package mcjty.lib.multipart;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IPartBlock {
   @Nonnull
   PartSlot getSlotFromState(Level var1, BlockPos var2, BlockState var3);
}
