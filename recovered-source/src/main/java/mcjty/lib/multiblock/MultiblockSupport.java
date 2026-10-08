package mcjty.lib.multiblock;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

public class MultiblockSupport {
   public static <T extends IMultiblock> boolean merge(Level level, BlockPos thisPos, MultiblockDriver<T> driver) {
      BiFunction<Level, BlockPos, IMultiblockConnector> holderGetter = driver.getHolderGetter();
      IMultiblockFixer<T> fixer = driver.getFixer();
      BiPredicate<T, T> mergeChecker = driver.getMergeChecker();
      Set<Integer> adjacentGeneratorIds = new HashSet<>();

      for (Direction direction : OrientationTools.DIRECTION_VALUES) {
         BlockPos pos = thisPos.relative(direction);
         IMultiblockConnector holder = holderGetter.apply(level, pos);
         if (holder != null) {
            int multiblockId = holder.getMultiblockId();
            adjacentGeneratorIds.add(multiblockId);
         }
      }

      IMultiblockConnector thisHolder = holderGetter.apply(level, thisPos);
      int thisId = thisHolder.getMultiblockId();
      T thisMb = driver.get(thisId);
      if (adjacentGeneratorIds.isEmpty()) {
         return false;
      } else if (adjacentGeneratorIds.size() == 1) {
         int mbId = adjacentGeneratorIds.iterator().next();
         if (mergeChecker.test(driver.get(mbId), thisMb)) {
            fixer.merge(driver, level, driver.get(mbId), thisMb);
            thisHolder.setMultiblockId(mbId);
            return true;
         } else {
            return false;
         }
      } else {
         boolean merged = false;

         for (Integer id : adjacentGeneratorIds) {
            T adjacentMb = driver.get(id);
            if (mergeChecker.test(thisMb, adjacentMb)) {
               fixer.merge(driver, level, thisMb, adjacentMb);
               merged = true;
            }
         }

         adjacentGeneratorIds.add(thisId);
         setBlocksToNetwork(level, thisPos, adjacentGeneratorIds, null, thisId, driver);
         return merged;
      }
   }

   public static <T extends IMultiblock> void addBlock(Level level, BlockPos thisPos, MultiblockDriver<T> driver, T newMb) {
      BiFunction<Level, BlockPos, IMultiblockConnector> holderGetter = driver.getHolderGetter();
      IMultiblockFixer<T> fixer = driver.getFixer();
      int mbId = driver.createId();
      driver.createOrUpdate(mbId, newMb);
      fixer.initialize(driver, level, newMb, mbId);
      IMultiblockConnector thisHolder = holderGetter.apply(level, thisPos);
      thisHolder.setMultiblockId(mbId);
      merge(level, thisPos, driver);
   }

   public static <T extends IMultiblock> Set<BlockPos> findMultiblock(Level level, BlockPos pos, MultiblockDriver<T> driver) {
      Set<BlockPos> positions = new HashSet<>();
      IMultiblockConnector holder = driver.getHolderGetter().apply(level, pos);
      if (holder != null) {
         findMultiblockInt(level, pos, driver.getHolderGetter(), positions);
      }

      return positions;
   }

   private static <T extends IMultiblock> void findMultiblockInt(
      Level level, BlockPos pos, BiFunction<Level, BlockPos, IMultiblockConnector> getter, Set<BlockPos> positions
   ) {
      positions.add(pos);

      for (Direction direction : OrientationTools.DIRECTION_VALUES) {
         BlockPos p = pos.relative(direction);
         if (!positions.contains(p) && getter.apply(level, p) != null) {
            findMultiblockInt(level, p, getter, positions);
         }
      }
   }

   private static <T extends IMultiblock> void setBlocksToNetwork(
      Level level, BlockPos c, @Nonnull Set<Integer> ids, @Nullable Set<BlockPos> done, int newId, MultiblockDriver<T> driver
   ) {
      IMultiblockConnector connector = driver.getHolderGetter().apply(level, c);
      if (connector != null) {
         if (done != null) {
            done.add(c);
         }

         connector.setMultiblockId(newId);

         for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos newC = c.relative(direction);
            if (done == null || !done.contains(newC)) {
               IMultiblockConnector con = driver.getHolderGetter().apply(level, newC);
               if (con != null) {
                  int id = con.getMultiblockId();
                  if (id != newId && ids.contains(id)) {
                     setBlocksToNetwork(level, newC, ids, done, newId, driver);
                  }
               }
            }
         }
      }
   }

   public static <T extends IMultiblock> void removeBlock(Level level, BlockPos thisPos, MultiblockDriver<T> driver) {
      BiFunction<Level, BlockPos, IMultiblockConnector> holderGetter = driver.getHolderGetter();
      IMultiblockConnector thisHolder = holderGetter.apply(level, thisPos);
      T thisData = driver.get(thisHolder.getMultiblockId());

      for (Direction direction : OrientationTools.DIRECTION_VALUES) {
         BlockPos newC = thisPos.relative(direction);
         IMultiblockConnector holder = holderGetter.apply(level, newC);
         if (holder != null) {
            setBlocksToNetwork(level, newC, Collections.singleton(holder.getMultiblockId()), null, -1, driver);
         }
      }

      List<Pair<Integer, Set<BlockPos>>> todo = new ArrayList<>();
      int idToUse = thisHolder.getMultiblockId();

      for (Direction directionx : OrientationTools.DIRECTION_VALUES) {
         BlockPos newC = thisPos.relative(directionx);
         IMultiblockConnector holder = holderGetter.apply(level, newC);
         if (holder != null && holder.getMultiblockId() == -1) {
            if (idToUse == -1) {
               idToUse = driver.createId();
            }

            Set<BlockPos> done = Sets.newHashSet();
            done.add(thisPos);
            setBlocksToNetwork(level, newC, Collections.singleton(-1), done, idToUse, driver);
            done.remove(thisPos);
            todo.add(Pair.of(idToUse, done));
            idToUse = -1;
         }
      }

      driver.getFixer().distribute(driver, level, thisData, todo);
   }
}
