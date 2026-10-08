package mcjty.lib.network;

import javax.annotation.Nonnull;
import mcjty.lib.typed.TypedMap;
import net.minecraft.world.entity.player.Player;

public interface IServerCommand {
   boolean execute(@Nonnull Player var1, @Nonnull TypedMap var2);
}
