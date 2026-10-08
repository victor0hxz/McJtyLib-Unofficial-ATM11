package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

public record Command<TE extends GenericTileEntity>(String name, IRunnable<TE> cmd) implements ICommand {
   public static <E extends GenericTileEntity> Command<E> create(String name, IRunnable<E> command) {
      return new Command<>(name, command);
   }
}
