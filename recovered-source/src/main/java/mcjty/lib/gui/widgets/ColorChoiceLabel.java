package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ColorChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypeConvertors;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class ColorChoiceLabel extends AbstractLabel<ColorChoiceLabel> {
   public static final String TYPE_COLORCHOICELABEL = "colorchoicelabel";
   public static final Key<Integer> PARAM_COLOR = new Key<>("color", Type.INTEGER);
   private final List<Integer> colorList = new ArrayList<>();
   private final Map<Integer, List<String>> tooltipMap = new HashMap<>();
   private Integer currentColor = null;
   private List<ColorChoiceEvent> choiceEvents = null;

   public ColorChoiceLabel() {
      this.text("");
   }

   public ColorChoiceLabel colors(Integer... colors) {
      for (Integer color : colors) {
         this.colorList.add(color);
         if (this.currentColor == null) {
            this.currentColor = color;
            this.fireChoiceEvents(this.currentColor);
         }
      }

      return this;
   }

   public ColorChoiceLabel choiceTooltip(Integer color, String... tooltips) {
      this.tooltipMap.put(color, Arrays.asList(tooltips));
      return this;
   }

   public ColorChoiceLabel currentColor(Integer color) {
      this.currentColor = color;
      return this;
   }

   public Integer getCurrentColor() {
      return this.currentColor;
   }

   @Override
   public List<String> getTooltips() {
      List<String> tooltips = this.tooltipMap.get(this.currentColor);
      return tooltips == null ? super.getTooltips() : tooltips;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         if (this.isEnabled()) {
            this.drawStyledBoxNormal(
               this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1, 0xFF000000 | this.getCurrentColor()
            );
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

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         int index = this.colorList.indexOf(this.currentColor);
         if (button == 1) {
            if (--index < 0) {
               index = this.colorList.size() - 1;
            }
         } else if (++index >= this.colorList.size()) {
            index = 0;
         }

         this.currentColor = this.colorList.get(index);
         this.fireChoiceEvents(this.currentColor);
      }

      return null;
   }

   public ColorChoiceLabel event(ColorChoiceEvent event) {
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
      command.findCommand("choices")
         .ifPresent(
            cmd -> cmd.commands()
               .forEach(
                  choiceCmd -> {
                     Integer color = choiceCmd.getOptionalPar(0, 0);
                     this.colorList.add(color);
                     choiceCmd.findCommand("tooltips")
                        .ifPresent(tooltipsCmd -> this.tooltipMap.put(color, tooltipsCmd.parameters().map(Object::toString).collect(Collectors.toList())));
                  }
               )
         );
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      command.removeParameter(1);
      GuiParser.GuiCommand choicesCmd = new GuiParser.GuiCommand("choices");

      for (Integer s : this.colorList) {
         GuiParser.GuiCommand choiceCmd = new GuiParser.GuiCommand("choice").parameter(s);
         choicesCmd.command(choiceCmd);
         List<String> tooltips = this.tooltipMap.get(s);
         if (tooltips != null && !tooltips.isEmpty()) {
            GuiParser.GuiCommand tooltipsCmd = new GuiParser.GuiCommand("tooltips");
            choiceCmd.command(tooltipsCmd);

            for (String tt : tooltips) {
               tooltipsCmd.parameter(tt);
            }
         }
      }

      command.command(choicesCmd);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("colorchoicelabel");
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
