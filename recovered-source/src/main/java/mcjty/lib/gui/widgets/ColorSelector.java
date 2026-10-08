package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ColorChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypeConvertors;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

public class ColorSelector extends AbstractLabel<ColorSelector> {
   public static final String TYPE_COLORSELECTOR = "colorselector";
   public static final Key<Integer> PARAM_COLOR = new Key<>("color", Type.INTEGER);
   private Integer currentColor = null;
   private List<ColorChoiceEvent> choiceEvents = null;

   public ColorSelector() {
      this.text("");
   }

   public ColorSelector currentColor(Integer color) {
      this.currentColor = color;
      return this;
   }

   public Integer getCurrentColor() {
      return this.currentColor;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         if (this.isEnabled()) {
            Integer color = this.getCurrentColorSafe();
            this.drawStyledBoxNormal(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1, 0xFF000000 | color);
            RenderHelper.drawLeftTriangle(graphics, xx + this.bounds.width - 10, yy + this.bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
            RenderHelper.drawRightTriangle(graphics, xx + this.bounds.width - 4, yy + this.bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
         } else {
            this.drawStyledBoxDisabled(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            RenderHelper.drawLeftTriangle(graphics, xx + this.bounds.width - 10, yy + this.bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
            RenderHelper.drawRightTriangle(graphics, xx + this.bounds.width - 4, yy + this.bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
         }

         super.drawOffset(gui, graphics, x, y, 0, 1);
      }
   }

   private Integer getCurrentColorSafe() {
      Integer color = this.getCurrentColor();
      if (color == null) {
         color = 0;
      }

      return color;
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         this.createColorSelectorWindow(this.window, (int)x, (int)y);
      }

      return null;
   }

   private static int parseIntSafe(String s) {
      try {
         return Integer.parseInt(s);
      } catch (NumberFormatException var2) {
         return 0;
      }
   }

   private void setSelectedColor(TextField red, TextField green, TextField blue, Button current, int color) {
      red.text(String.valueOf(color >> 16 & 0xFF));
      green.text(String.valueOf(color >> 8 & 0xFF));
      blue.text(String.valueOf(color & 0xFF));
      current.color(this.calculateContrastingColor(color));
      current.filledBackground(0xFF000000 | color);
   }

   private int getInputColor(TextField red, TextField green, TextField blue) {
      int r = parseIntSafe(red.getText());
      int g = parseIntSafe(green.getText());
      int b = parseIntSafe(blue.getText());
      return (r << 16) + (g << 8) + b;
   }

   private int calculateContrastingColor(int color) {
      int red = color >> 16 & 0xFF;
      int green = color >> 8 & 0xFF;
      int blue = color & 0xFF;
      red = red + 128 & 0xFF;
      green = green + 128 & 0xFF;
      blue = blue + 128 & 0xFF;
      return (red << 16) + (green << 8) + blue;
   }

   private void createColorSelectorWindow(Window window, int x, int y) {
      Minecraft mc = Minecraft.getInstance();
      Screen gui = window.getWindowManager().getGui();
      Panel modalDialog = Widgets.positional().filledRectThickness(2);
      int wx = (int)(window.getToplevel().getBounds().getX() + 20.0);
      int wy = (int)(window.getToplevel().getBounds().getY() + 20.0);
      modalDialog.bounds(wx, wy, 240, 160);
      int cc = this.getCurrentColorSafe();
      Button current = Widgets.button(5, 108, 95, 27, "Current").color(this.calculateContrastingColor(cc)).filledBackground(0xFF000000 | cc);
      TextField red = Widgets.textfield(5, 5, 30, 15);
      TextField green = Widgets.textfield(38, 5, 30, 15);
      TextField blue = Widgets.textfield(71, 5, 30, 15);
      this.setSelectedColor(red, green, blue, current, cc);
      red.addTextEnterEvent(newText -> {
         this.currentColor = this.getInputColor(red, green, blue);
         current.color(this.calculateContrastingColor(this.currentColor));
         current.filledBackground(0xFF000000 | this.currentColor);
         this.fireChoiceEvents(this.currentColor);
      });
      green.addTextEnterEvent(newText -> {
         this.currentColor = this.getInputColor(red, green, blue);
         current.color(this.calculateContrastingColor(this.currentColor));
         current.filledBackground(0xFF000000 | this.currentColor);
         this.fireChoiceEvents(this.currentColor);
      });
      blue.addTextEnterEvent(newText -> {
         this.currentColor = this.getInputColor(red, green, blue);
         current.color(this.calculateContrastingColor(this.currentColor));
         current.filledBackground(0xFF000000 | this.currentColor);
         this.fireChoiceEvents(this.currentColor);
      });

      for (DyeColor color : DyeColor.values()) {
         int i = color.ordinal();
         int xx = i % 4;
         int yy = i / 4;
         Button colorLabel = Widgets.button(5 + xx * 20, 23 + yy * 20, 18, 18, null)
            .color(color.getTextColor())
            .tooltips(color.getSerializedName())
            .filledBackground(0xFF000000 | color.getTextColor())
            .event(() -> {
               this.currentColor = color.getTextColor() & 16777215;
               this.setSelectedColor(red, green, blue, current, this.currentColor);
               this.fireChoiceEvents(this.currentColor);
            });
         modalDialog.children(colorLabel);
      }

      ImageLabel colors = new ImageLabel()
         .image(Identifier.fromNamespaceAndPath("mcjtylib", "textures/gui/colorpicker.png"), 0, 0)
         .setTextureDimensions(128, 128)
         .hint(105, 5, 128, 128);
      Button close = Widgets.button(90, 140, 60, 15, "Close");
      modalDialog.children(close, colors, red, green, blue, current);
      Window modalWindow = window.getWindowManager().createModalWindow(modalDialog);
      colors.event((u, v, colorx) -> {
         this.currentColor = colorx & 16777215;
         this.setSelectedColor(red, green, blue, current, this.currentColor);
         this.fireChoiceEvents(this.currentColor);
      });
      close.event(() -> window.getWindowManager().closeWindow(modalWindow));
   }

   public ColorSelector event(ColorChoiceEvent event) {
      if (this.choiceEvents == null) {
         this.choiceEvents = new ArrayList<>();
      }

      this.choiceEvents.add(event);
      return this;
   }

   public void removeChoiceEvent(ColorChoiceEvent event) {
      if (this.choiceEvents != null) {
         this.choiceEvents.remove(event);
      }
   }

   private void fireChoiceEvents(Integer color) {
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, "choice").put(PARAM_COLOR, color).build());
      if (this.choiceEvents != null) {
         for (ColorChoiceEvent event : this.choiceEvents) {
            event.choiceChanged(color);
         }
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      command.removeParameter(1);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("colorselector");
   }

   @Override
   public <T> void setGenericValue(T value) {
      this.currentColor(TypeConvertors.toInt(value));
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return this.getCurrentColor();
   }
}
