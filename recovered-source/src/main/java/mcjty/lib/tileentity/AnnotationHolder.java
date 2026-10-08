package mcjty.lib.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import mcjty.lib.blockcommands.IRunnable;
import mcjty.lib.blockcommands.IRunnableWithList;
import mcjty.lib.blockcommands.IRunnableWithListResult;
import mcjty.lib.blockcommands.IRunnableWithResult;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.registries.DeferredBlock;

public class AnnotationHolder {
   public static final Map<Class<? extends GenericTileEntity>, AnnotationHolder> annotations = new HashMap<>();
   final Map<String, IRunnable<?>> serverCommands = new HashMap<>();
   final Map<String, IRunnableWithResult<?>> serverCommandsWithResult = new HashMap<>();
   final Map<String, IRunnable<?>> clientCommands = new HashMap<>();
   final Map<String, IRunnableWithListResult<?, ?>> serverCommandsWithListResult = new HashMap<>();
   final Map<String, IRunnableWithList<?, ?>> clientCommandsWithList = new HashMap<>();
   final Map<String, ValueHolder<?, ?>> valueMap = new HashMap<>();
   final List<AnnotationHolder.CapHolder> caps = new ArrayList<>();

   public int getCapSize() {
      return this.caps.size();
   }

   public <B, C> AnnotationHolder.CapHolder<B, C> getCapHolder(int i) {
      return this.caps.get(i);
   }

   public record CapHolder<B, C>(BlockCapability<B, C> capability, Function<? super GenericTileEntity, Object> function, DeferredBlock<?> block) {
   }
}
