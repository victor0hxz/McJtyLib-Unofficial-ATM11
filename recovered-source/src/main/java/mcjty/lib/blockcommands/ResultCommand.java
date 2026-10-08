package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

public class ResultCommand<TE extends GenericTileEntity> implements ICommand {
   private final String name;
   private final IRunnableWithResult<TE> cmd;
   private final IRunnable<TE> clientCommand;

   private ResultCommand(String name, IRunnableWithResult<TE> cmd, IRunnable<TE> clientCommand) {
      this.name = name;
      this.clientCommand = clientCommand;
      this.cmd = cmd;
   }

   @Override
   public String name() {
      return this.name;
   }

   public IRunnableWithResult<TE> getCmd() {
      return this.cmd;
   }

   public IRunnable<TE> getClientCommand() {
      return this.clientCommand;
   }

   public static <E extends GenericTileEntity> ResultCommand<E> create(String name, IRunnableWithResult<E> command, IRunnable<E> clientCommand) {
      return new ResultCommand<>(name, command, clientCommand);
   }
}
