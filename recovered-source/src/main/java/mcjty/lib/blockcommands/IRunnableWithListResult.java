package mcjty.lib.blockcommands;

import java.util.List;
import javax.annotation.Nonnull;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface IRunnableWithListResult<TE extends GenericTileEntity, T> {
   @Nonnull
   List<T> run(TE var1, Player var2, TypedMap var3);
}
