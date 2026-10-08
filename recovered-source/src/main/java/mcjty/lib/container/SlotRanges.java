package mcjty.lib.container;

import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;
import java.util.Set;

public class SlotRanges {
   private final TreeRangeSet<Integer> treeRangeSet = TreeRangeSet.create();

   public void addSingle(int index) {
      this.treeRangeSet.add(Range.singleton(index).canonical(DiscreteDomain.integers()));
   }

   public Set<Range<Integer>> asRanges() {
      return this.treeRangeSet.asRanges();
   }
}
