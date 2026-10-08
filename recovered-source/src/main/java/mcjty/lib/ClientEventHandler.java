package mcjty.lib;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.gui.IKeyReceiver;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent.MouseDragged.Pre;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ClientEventHandler {
   @SubscribeEvent
   public void onItemTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BaseBlock baseBlock) {
         baseBlock.appendHoverText(stack, event.getContext(), event.getToolTip(), event.getFlags());
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public void onMouseDragged(Pre event) {
      if (event.getScreen() instanceof IKeyReceiver container && container.getWindow() != null) {
         WindowManager manager = container.getWindow().getWindowManager();
         if (manager != null && manager.getModalWindows().findFirst().isPresent()) {
            manager.mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton());
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public void onMouseScolled(net.neoforged.neoforge.client.event.ScreenEvent.MouseScrolled.Pre event) {
      if (event.getScreen() instanceof IKeyReceiver container && container.getWindow() != null) {
         WindowManager manager = container.getWindow().getWindowManager();
         if (manager != null
            && manager.getModalWindows().findFirst().isPresent()
            && container.mouseScrolledFromEvent(event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY())) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public void onMouseClicked(net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonPressed.Pre event) {
      if (event.getScreen() instanceof IKeyReceiver container && container.getWindow() != null) {
         WindowManager manager = container.getWindow().getWindowManager();
         if (manager != null
            && manager.getModalWindows().findFirst().isPresent()
            && container.mouseClickedFromEvent(event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public void onMouseReleased(net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonReleased.Pre event) {
      if (event.getScreen() instanceof IKeyReceiver container && container.getWindow() != null) {
         WindowManager manager = container.getWindow().getWindowManager();
         if (manager != null
            && manager.getModalWindows().findFirst().isPresent()
            && container.mouseReleasedFromEvent(event.getMouseX(), event.getMouseY(), event.getButton())) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public void onGuiInput(net.neoforged.neoforge.client.event.ScreenEvent.CharacterTyped.Pre event) {
      if (event.getScreen() instanceof IKeyReceiver container && container.getWindow() != null) {
         Widget<?> focus;
         if (container.getWindow().getWindowManager() == null) {
            focus = container.getWindow().getTextFocus();
         } else {
            focus = container.getWindow().getWindowManager().getTextFocus();
         }

         if (focus != null) {
            event.setCanceled(true);
            container.charTypedFromEvent((char)event.getCodePoint());
         }
      }
   }

   @SubscribeEvent
   public void onKeyboardInput(net.neoforged.neoforge.client.event.ScreenEvent.KeyPressed.Pre event) {
      if (event.getScreen() instanceof IKeyReceiver container && container.getWindow() != null) {
         Widget<?> focus;
         if (container.getWindow().getWindowManager() == null) {
            focus = container.getWindow().getTextFocus();
         } else {
            focus = container.getWindow().getWindowManager().getTextFocus();
         }

         if (focus != null) {
            event.setCanceled(true);
            container.keyTypedFromEvent(event.getKeyCode(), event.getScanCode());
         }
      }
   }
}
