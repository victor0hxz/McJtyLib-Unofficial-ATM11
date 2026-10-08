package mcjty.lib.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import mcjty.lib.api.ITabExpander;
import mcjty.lib.blocks.BaseBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

public class BaseBlockItem extends BlockItem implements ITabExpander {
   public BaseBlockItem(Block block, Properties properties) {
      super(block, properties);
   }

   @Override
   public List<ItemStack> getItemsForTab() {
      return Collections.emptyList();
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flags) {
      super.appendHoverText(stack, context, display, tooltip, flags);
      if (this.getBlock() instanceof BaseBlock baseBlock) {
         List<Component> extra = new ArrayList<>();
         baseBlock.appendHoverText(stack, context, extra, flags);
         extra.forEach(tooltip);
      }
   }
}
