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
import mcjty.lib.gui.ITranslatableEnum;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class EnumChoiceLabel extends AbstractLabel<EnumChoiceLabel> {
   public static final String TYPE_ENUM_CHOICE_LABEL = "enumchoicelabel";
   public static final Key<Integer> PARAM_CHOICE = new Key<>("choice", Type.INTEGER);
   public static final Key<Integer> PARAM_CHOICE_IDX = new Key<>("choiceIdx", Type.INTEGER);
   private Integer choiceIndex = null;
   private ITranslatableEnum<?>[] enumChoices = null;
   private final Map<ITranslatableEnum<?>, List<String>> tooltipMap = new HashMap<>();
   private List<ChoiceEvent<ITranslatableEnum<?>>> choiceEvents = null;

   public EnumChoiceLabel() {
      this.text("");
   }

   private void setCurrentChoice(ITranslatableEnum<?> enumChoice) {
      this.choiceIndex = enumChoice.ordinal();
   }

   public EnumChoiceLabel choices(ITranslatableEnum<?>[] choices) {
      this.enumChoices = choices;
      if (this.choiceIndex == null) {
         this.choiceIndex = choices[0].ordinal();
         this.text(choices[0].getI18n());
         this.fireChoiceEvents(choices[0]);
      }

      if (this.tooltipMap.isEmpty()) {
         for (ITranslatableEnum<?> choice : choices) {
            this.setChoiceTooltip(choice);
         }
      }

      return this;
   }

   public EnumChoiceLabel setChoiceTooltip(ITranslatableEnum<?> choice) {
      this.tooltipMap.put(choice, Arrays.asList(choice.getI18nSplitedTooltip()));
      return this;
   }

   public EnumChoiceLabel choice(ITranslatableEnum<?> choice) {
      if (Objects.equals(this.choiceIndex, choice.ordinal())) {
         return this;
      } else {
         this.choiceIndex = choice.ordinal();
         this.text(choice.getI18n());
         return this;
      }
   }

   @Override
   public List<String> getTooltips() {
      List<String> tooltips = this.tooltipMap.get(this.enumChoices[this.choiceIndex]);
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
         if (this.enumChoices.length == 0) {
            return null;
         }

         if (button != 1 && !SafeClientTools.isSneaking()) {
            Integer var7 = this.choiceIndex;
            this.choiceIndex = this.choiceIndex + 1;
            if (this.choiceIndex >= this.enumChoices.length) {
               this.choiceIndex = 0;
            }
         } else {
            Integer var6 = this.choiceIndex;
            this.choiceIndex = this.choiceIndex - 1;
            if (this.choiceIndex < 0) {
               this.choiceIndex = this.enumChoices.length - 1;
            }
         }

         this.text(this.enumChoices[this.choiceIndex].getI18n());
         this.fireChoiceEvents(this.enumChoices[this.choiceIndex]);
      }

      return null;
   }

   public EnumChoiceLabel event(ChoiceEvent<ITranslatableEnum<?>> event) {
      if (this.choiceEvents == null) {
         this.choiceEvents = new ArrayList<>();
      }

      this.choiceEvents.add(event);
      return this;
   }

   public void removeChoiceEvent(ChoiceEvent<ITranslatableEnum<?>> event) {
      if (this.choiceEvents != null) {
         this.choiceEvents.remove(event);
      }
   }

   private void fireChoiceEvents(ITranslatableEnum<?> choice) {
      this.fireChannelEvents(
         TypedMap.builder().put(Window.PARAM_ID, "choice").put(PARAM_CHOICE, choice.ordinal()).put(PARAM_CHOICE_IDX, choice.ordinal()).build()
      );
      if (this.choiceEvents != null) {
         for (ChoiceEvent<ITranslatableEnum<?>> event : this.choiceEvents) {
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
                     Integer enumIndex = choiceCmd.getOptionalPar(0, 0);
                     choiceCmd.findCommand("tooltips")
                        .ifPresent(
                           tooltipsCmd -> this.tooltipMap
                              .put(this.enumChoices[enumIndex], tooltipsCmd.parameters().map(Object::toString).collect(Collectors.toList()))
                        );
                  }
               )
         );
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      command.removeParameter(1);
      GuiParser.GuiCommand choicesCmd = new GuiParser.GuiCommand("choices");

      for (ITranslatableEnum<?> s : this.enumChoices) {
         GuiParser.GuiCommand choiceCmd = new GuiParser.GuiCommand("choice").parameter(s.ordinal());
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
      return new GuiParser.GuiCommand("enumchoicelabel");
   }
}
