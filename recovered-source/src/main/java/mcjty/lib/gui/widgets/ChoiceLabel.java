package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class ChoiceLabel extends AbstractLabel<ChoiceLabel> {
   public static final String TYPE_CHOICELABEL = "choicelabel";
   public static final Key<String> PARAM_CHOICE = new Key<>("choice", Type.STRING);
   public static final Key<Integer> PARAM_CHOICE_IDX = new Key<>("choiceIdx", Type.INTEGER);
   private List<String> choiceList = new ArrayList<>();
   private Map<String, List<String>> tooltipMap = new HashMap<>();
   private String currentChoice = null;
   private List<ChoiceEvent<String>> choiceEvents = null;

   public ChoiceLabel() {
      this.text("");
   }

   public ChoiceLabel choices(String... choices) {
      for (String choice : choices) {
         this.choiceList.add(choice);
         if (this.currentChoice == null) {
            this.currentChoice = choice;
            this.text(this.currentChoice);
            this.fireChoiceEvents(this.currentChoice);
         }
      }

      return this;
   }

   public ChoiceLabel choiceTooltip(String choice, String... tooltips) {
      this.tooltipMap.put(choice, Arrays.asList(tooltips));
      return this;
   }

   public ChoiceLabel choice(String choice) {
      if (Objects.equals(this.currentChoice, choice)) {
         return this;
      } else {
         this.currentChoice = choice;
         this.text(this.currentChoice);
         return this;
      }
   }

   public String getCurrentChoice() {
      return this.currentChoice;
   }

   @Override
   public List<String> getTooltips() {
      List<String> tooltips = this.tooltipMap.get(this.currentChoice);
      return tooltips == null ? super.getTooltips() : tooltips;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         if (this.isEnabled()) {
            if (this.isHovering()) {
               this.drawStyledBoxHovering(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            } else {
               this.drawStyledBoxNormal(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            }

            RenderHelper.drawLeftTriangle(graphics, xx + this.bounds.width - 10, yy + this.bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
            RenderHelper.drawRightTriangle(graphics, xx + this.bounds.width - 4, yy + this.bounds.height / 2, StyleConfig.colorCycleButtonTriangleNormal);
         } else {
            this.drawStyledBoxDisabled(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            RenderHelper.drawLeftTriangle(graphics, xx + this.bounds.width - 10, yy + this.bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
            RenderHelper.drawRightTriangle(graphics, xx + this.bounds.width - 4, yy + this.bounds.height / 2, StyleConfig.colorCycleButtonTriangleDisabled);
         }

         super.drawOffset(gui, graphics, x, y, -3, 1);
      }
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         if (this.choiceList.isEmpty()) {
            return null;
         }

         int index = this.choiceList.indexOf(this.currentChoice);
         if (button != 1 && !SafeClientTools.isSneaking()) {
            if (++index >= this.choiceList.size()) {
               index = 0;
            }
         } else if (--index < 0) {
            index = this.choiceList.size() - 1;
         }

         this.currentChoice = this.choiceList.get(index);
         this.text(this.currentChoice);
         this.fireChoiceEvents(this.currentChoice);
      }

      return null;
   }

   public ChoiceLabel event(ChoiceEvent<String> event) {
      if (this.choiceEvents == null) {
         this.choiceEvents = new ArrayList<>();
      }

      this.choiceEvents.add(event);
      return this;
   }

   public void removeChoiceEvent(ChoiceEvent<String> event) {
      if (this.choiceEvents != null) {
         this.choiceEvents.remove(event);
      }
   }

   private void fireChoiceEvents(String choice) {
      this.fireChannelEvents(
         TypedMap.builder().put(Window.PARAM_ID, "choice").put(PARAM_CHOICE, choice).put(PARAM_CHOICE_IDX, this.choiceList.indexOf(choice)).build()
      );
      if (this.choiceEvents != null) {
         for (ChoiceEvent<String> event : this.choiceEvents) {
            event.choiceChanged(choice);
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
                     String choice = choiceCmd.getOptionalPar(0, "");
                     this.choiceList.add(choice);
                     choiceCmd.findCommand("tooltips")
                        .ifPresent(tooltipsCmd -> this.tooltipMap.put(choice, tooltipsCmd.parameters().map(Object::toString).collect(Collectors.toList())));
                  }
               )
         );
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      command.removeParameter(1);
      GuiParser.GuiCommand choicesCmd = new GuiParser.GuiCommand("choices");

      for (String s : this.choiceList) {
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
      return new GuiParser.GuiCommand("choicelabel");
   }

   @Override
   public <T> void setGenericValue(T value) {
      if (value instanceof Number number) {
         this.choice(this.choiceList.get(number.intValue()));
      } else if (value instanceof Boolean bool) {
         this.choice(this.choiceList.get(bool ? 1 : 0));
      } else if (value instanceof String str) {
         this.choice(str);
      } else {
         super.setGenericValue(value);
      }
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      if (Type.INTEGER.equals(type)) {
         return this.choiceList.indexOf(this.getCurrentChoice());
      } else if (Type.BOOLEAN.equals(type)) {
         return this.choiceList.indexOf(this.getCurrentChoice()) != 0;
      } else {
         return Type.STRING.equals(type) ? this.getCurrentChoice() : this.getCurrentChoice();
      }
   }
}
