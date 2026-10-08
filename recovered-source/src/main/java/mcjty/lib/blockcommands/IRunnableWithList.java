package mcjty.lib.blockcommands;

import java.util.List;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface IRunnableWithList<TE extends GenericTileEntity, T> {
   void run(TE var1, Player var2, TypedMap var3, List<T> var4);
}
