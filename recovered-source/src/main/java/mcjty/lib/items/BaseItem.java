package mcjty.lib.items;

import java.util.Collections;
import java.util.List;
import mcjty.lib.api.ITabExpander;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class BaseItem extends Item implements ITabExpander {
   public BaseItem(Properties properties) {
      super(properties);
   }

   @Override
   public List<ItemStack> getItemsForTab() {
      return Collections.emptyList();
   }
}
