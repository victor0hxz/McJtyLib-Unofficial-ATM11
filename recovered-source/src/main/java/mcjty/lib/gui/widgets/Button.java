package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.events.ButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class Button extends AbstractLabel<Button> {
   public static final String TYPE_BUTTON = "button";
   private List<ButtonEvent> buttonEvents = null;
   private boolean pressed = false;

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         if (this.isEnabledAndVisible()) {
            if (this.pressed) {
               this.drawStyledBoxSelected(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            } else if (this.isHovering()) {
               this.drawStyledBoxHovering(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            } else {
               this.drawStyledBoxNormal(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            }
         } else {
            this.drawStyledBoxDisabled(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
         }

         super.drawOffset(gui, graphics, x, y, 0, 1);
      }
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         this.pressed = true;
         return this;
      } else {
         return null;
      }
   }

   @Override
   public void mouseRelease(double x, double y, int button) {
      super.mouseRelease(x, y, button);
      if (this.pressed) {
         this.pressed = false;
         if (this.isEnabledAndVisible()) {
            this.fireButtonEvents();
         }
      }
   }

   public Button event(ButtonEvent event) {
      if (this.buttonEvents == null) {
         this.buttonEvents = new ArrayList<>();
      }

      this.buttonEvents.add(event);
      return this;
   }

   public void removeButtonEvent(ButtonEvent event) {
      if (this.buttonEvents != null) {
         this.buttonEvents.remove(event);
      }
   }

   private void fireButtonEvents() {
      this.fireChannelEvents();
      if (this.buttonEvents != null) {
         for (ButtonEvent event : this.buttonEvents) {
            event.buttonClicked();
         }
      }
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("button");
   }
}
