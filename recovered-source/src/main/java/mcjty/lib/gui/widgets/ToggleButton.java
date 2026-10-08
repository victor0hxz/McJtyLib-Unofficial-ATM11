package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ButtonEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypeConvertors;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class ToggleButton extends AbstractLabel<ToggleButton> {
   public static final String TYPE_TOGGLEBUTTON = "togglebutton";
   public static final Key<Boolean> PARAM_ON = new Key<>("on", Type.BOOLEAN);
   public static final boolean DEFAULT_CHECKMARKER = false;
   private List<ButtonEvent> buttonEvents = null;
   private boolean pressed = false;
   private boolean checkMarker = false;

   public boolean isPressed() {
      return this.pressed;
   }

   public ToggleButton pressed(boolean pressed) {
      if (this.pressed == pressed) {
         return this;
      } else {
         this.pressed = pressed;
         return this;
      }
   }

   @Override
   public int getDesiredWidth() {
      int w = this.desiredWidth;
      if (this.isDynamic()) {
         return w;
      } else {
         if (w == -1) {
            w = this.mc.font.width(this.getText()) + 6 + (this.checkMarker ? 10 : 0);
         }

         return w;
      }
   }

   public boolean isCheckMarker() {
      return this.checkMarker;
   }

   public ToggleButton checkMarker(boolean checkMarker) {
      this.checkMarker = checkMarker;
      return this;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         if (this.isEnabled()) {
            if (this.pressed) {
               this.drawStyledBoxSelected(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            } else if (this.isHovering()) {
               this.drawStyledBoxHovering(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            } else {
               this.drawStyledBoxNormal(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            }

            if (this.checkMarker) {
               RenderHelper.drawBeveledBox(
                  graphics,
                  xx + 2,
                  yy + this.bounds.height / 2 - 4,
                  xx + 10,
                  yy + this.bounds.height / 2 + 4,
                  StyleConfig.colorToggleNormalBorderTopLeft,
                  StyleConfig.colorToggleNormalBorderBottomRight,
                  StyleConfig.colorToggleNormalFiller
               );
               if (this.pressed) {
                  graphics.text(this.mc.font, "v", xx + 3, yy + this.bounds.height / 2 - 4, StyleConfig.colorToggleTextNormal);
               }
            }
         } else {
            this.drawStyledBoxDisabled(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            if (this.checkMarker) {
               RenderHelper.drawBeveledBox(
                  graphics,
                  xx + 2,
                  yy + this.bounds.height / 2 - 4,
                  xx + 10,
                  yy + this.bounds.height / 2 + 4,
                  StyleConfig.colorToggleDisabledBorderTopLeft,
                  StyleConfig.colorToggleDisabledBorderBottomRight,
                  StyleConfig.colorToggleDisabledFiller
               );
               if (this.pressed) {
                  graphics.text(this.mc.font, "v", xx + 3, yy + this.bounds.height / 2 - 4, StyleConfig.colorToggleTextDisabled);
               }
            }
         }

         super.drawOffset(gui, graphics, x, y, this.checkMarker ? 6 : 0, 1);
      }
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         this.pressed = !this.pressed;
         this.fireButtonEvents();
         return this;
      } else {
         return null;
      }
   }

   public ToggleButton event(ButtonEvent event) {
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
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, "enter").put(PARAM_ON, this.isPressed()).build());
      if (this.buttonEvents != null) {
         for (ButtonEvent event : this.buttonEvents) {
            event.buttonClicked();
         }
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      this.checkMarker = GuiParser.get(command, "check", false);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      GuiParser.put(command, "check", this.checkMarker, false);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("togglebutton");
   }

   @Override
   public <T> void setGenericValue(T value) {
      this.pressed(TypeConvertors.toBoolean(value));
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return this.isPressed();
   }
}
