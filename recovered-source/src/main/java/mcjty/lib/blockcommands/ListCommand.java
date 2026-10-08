package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

public record ListCommand<TE extends GenericTileEntity, T>(String name, IRunnableWithListResult<TE, T> cmd, IRunnableWithList<TE, T> clientCommand)
   implements ICommand {
   public static <E extends GenericTileEntity, S> ListCommand<E, S> create(
      String name, IRunnableWithListResult<E, S> command, IRunnableWithList<E, S> clientCommand
   ) {
      return new ListCommand<>(name, command, clientCommand);
   }
}
