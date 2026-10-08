package mcjty.lib.blockcommands;

import javax.annotation.Nonnull;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface IRunnableWithResult<TE extends GenericTileEntity> {
   @Nonnull
   TypedMap run(TE var1, Player var2, TypedMap var3);
}
