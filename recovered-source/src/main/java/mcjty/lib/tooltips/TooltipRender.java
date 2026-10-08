package mcjty.lib.tooltips;

import com.mojang.datafixers.util.Either;
import java.util.List;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.keys.KeyBindings;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent.GatherComponents;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.apache.commons.lang3.tuple.Pair;

public class TooltipRender {
   private static final int STACKS_PER_LINE = 8;
   public static ITooltipSettings lastUsedTooltipItem = null;

   @SubscribeEvent
   public void onMakeTooltip(ItemTooltipEvent event) {
      Minecraft mc = Minecraft.getInstance();
      ItemStack stack = event.getItemStack();
      if (stack.getItem() instanceof ITooltipExtras extras) {
         List<Pair<ItemStack, Integer>> items = extras.getItems(stack);
         if (!items.isEmpty()) {
            List<Component> tooltip = event.getToolTip();
            int count = items.size();
            int lines = ((count - 1) / 8 + 1) * 2;
            int width = Math.min(8, count) * 18;
            String spaces = "";

            while (mc.font.width(spaces) < width) {
               spaces = spaces + " ";
            }

            for (int j = 0; j < lines; j++) {
               tooltip.add(ComponentFactory.literal(spaces));
            }
         }
      }
   }

   private static ITooltipSettings getSettings(Item item) {
      if (item instanceof ITooltipSettings) {
         return (ITooltipSettings)item;
      } else {
         return item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof ITooltipSettings ? (ITooltipSettings)((BlockItem)item).getBlock() : null;
      }
   }

   @SubscribeEvent
   public void onItemTooltipEvent(ItemTooltipEvent event) {
      Item item = event.getItemStack().getItem();
      ITooltipSettings settings = getSettings(item);
      lastUsedTooltipItem = settings;
      if (settings != null) {
         ManualEntry entry = settings.getManualEntry();
         if (entry.manual() != null && KeyBindings.openManual != null && !KeyBindings.openManual.isUnbound() && !SafeClientTools.isSneaking()) {
            Component translationKey = KeyBindings.openManual.getTranslatedKeyMessage();
            event.getToolTip()
               .add(
                  ComponentFactory.literal("<Press ")
                     .withStyle(ChatFormatting.YELLOW)
                     .append(translationKey)
                     .append(ComponentFactory.literal(" for help>").withStyle(ChatFormatting.YELLOW))
               );
         }
      }
   }

   @SubscribeEvent
   public void onTooltipGatherComponents(GatherComponents event) {
      Item item = event.getItemStack().getItem();
      ITooltipSettings settings = getSettings(item);
      lastUsedTooltipItem = settings;
      if (settings != null) {
         event.setMaxWidth(Math.max(event.getMaxWidth(), settings.getMaxWidth()));
      }

      this.onTooltipAddIcons(event);
   }

   protected void onTooltipAddIcons(GatherComponents event) {
      ItemStack stack = event.getItemStack();
      if (stack.getItem() instanceof ITooltipExtras extras) {
         List<Pair<ItemStack, Integer>> items = extras.getItems(stack);
         List<Either<FormattedText, TooltipComponent>> components = event.getTooltipElements();
         components.add(Either.right(new ClientTooltipIcon(items, 8)));
      }
   }
}
