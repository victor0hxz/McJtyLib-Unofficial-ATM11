package mcjty.lib.tooltips;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

public interface ITooltipExtras {
   int NOERROR = -1;
   int NOAMOUNT = -2;

   List<Pair<ItemStack, Integer>> getItems(ItemStack var1);
}
