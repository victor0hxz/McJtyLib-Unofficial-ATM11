package mcjty.lib.multipart;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;

public record PartPos(@Nonnull BlockPos pos, @Nonnull PartSlot slot) {
   public static PartPos create(@Nonnull BlockPos pos, @Nonnull PartSlot slot) {
      return new PartPos(pos, slot);
   }
}
