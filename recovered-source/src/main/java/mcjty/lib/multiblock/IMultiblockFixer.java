package mcjty.lib.multiblock;

import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

public interface IMultiblockFixer<T extends IMultiblock> {
   void initialize(MultiblockDriver<T> var1, Level var2, T var3, int var4);

   void merge(MultiblockDriver<T> var1, Level var2, T var3, T var4);

   void distribute(MultiblockDriver<T> var1, Level var2, T var3, List<Pair<Integer, Set<BlockPos>>> var4);
}
