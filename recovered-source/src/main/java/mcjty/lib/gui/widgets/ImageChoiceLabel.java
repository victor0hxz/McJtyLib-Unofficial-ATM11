package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.StringUtils;

public class ImageChoiceLabel extends AbstractImageLabel<ImageChoiceLabel> {
   public static final String TYPE_IMAGECHOICELABEL = "imagechoicelabel";
   public static final Key<String> PARAM_CHOICE = new Key<>("choice", Type.STRING);
   public static final Key<Integer> PARAM_CHOICE_IDX = new Key<>("choiceIdx", Type.INTEGER);
   public static final boolean DEFAULT_WITH_BORDER = false;
   private final List<ImageChoiceLabel.ChoiceEntry> choiceList = new ArrayList<>();
   private boolean withBorder = false;
   private int highlightedChoice = -1;
   private int currentChoice = -1;
   private List<ChoiceEvent<String>> choiceEvents = null;

   public boolean isWithBorder() {
      return this.withBorder;
   }

   public ImageChoiceLabel withBorder(boolean withBorder) {
      this.withBorder = withBorder;
      return this;
   }

   public int getHighlightedChoice() {
      return this.highlightedChoice;
   }

   public ImageChoiceLabel highlightedChoice(int highlightedChoice) {
      this.highlightedChoice = highlightedChoice;
      return this;
   }

   @Override
   public List<String> getTooltips() {
      return this.currentChoice == -1 ? super.getTooltips() : this.choiceList.get(this.currentChoice).tooltips();
   }

   public ImageChoiceLabel choice(String choice, String tooltips, Identifier image, int u, int v) {
      this.choiceList.add(new ImageChoiceLabel.ChoiceEntry(choice, Arrays.asList(StringUtils.split(tooltips, "\n")), image, u, v));
      if (this.currentChoice == -1) {
         this.setCurrentChoice(0);
      }

      return this;
   }

   public void clear() {
      this.choiceList.clear();
      this.currentChoice = -1;
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         int c = this.currentChoice;
         if (button == 1) {
            if (--c < 0) {
               c = this.choiceList.size() - 1;
            }
         } else if (++c >= this.choiceList.size()) {
            c = 0;
         }

         this.setCurrentChoice(c);
         this.fireChoiceEvents(this.choiceList.get(c).choice());
      }

      return null;
   }

   public void setCurrentChoice(int currentChoice) {
      if (this.currentChoice != currentChoice) {
         this.currentChoice = currentChoice;
         this.image(this.choiceList.get(currentChoice).image(), this.choiceList.get(currentChoice).u(), this.choiceList.get(currentChoice).v());
      }
   }

   public int findChoice(String choice) {
      if (choice == null) {
         return -1;
      } else {
         int i = 0;

         for (ImageChoiceLabel.ChoiceEntry s : this.choiceList) {
            if (choice.equals(s.choice())) {
               return i;
            }

            i++;
         }

         return -1;
      }
   }

   public void setCurrentChoice(String choice) {
      int idx = this.findChoice(choice);
      if (idx != -1) {
         this.setCurrentChoice(idx);
      }
   }

   public int getCurrentChoiceIndex() {
      return this.currentChoice;
   }

   public String getCurrentChoice() {
      return this.currentChoice == -1 ? null : this.choiceList.get(this.currentChoice).choice();
   }

   public ImageChoiceLabel event(ChoiceEvent<String> event) {
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
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, "choice").put(PARAM_CHOICE, choice).put(PARAM_CHOICE_IDX, this.currentChoice).build());
      if (this.choiceEvents != null) {
         for (ChoiceEvent<String> event : this.choiceEvents) {
            event.choiceChanged(choice);
         }
      }
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.withBorder) {
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         if (this.isEnabled()) {
            if (this.currentChoice == this.highlightedChoice) {
               this.drawStyledBoxSelected(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            } else if (this.isHovering()) {
               this.drawStyledBoxHovering(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            } else {
               this.drawStyledBoxNormal(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
            }
         } else {
            this.drawStyledBoxDisabled(this.window, graphics, xx, yy, xx + this.bounds.width - 1, yy + this.bounds.height - 1);
         }
      }

      super.draw(gui, graphics, x, y);
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
                     List<String> tooltips = choiceCmd.findCommand("tooltips")
                        .map(tooltipsCmd -> tooltipsCmd.parameters().map(Object::toString).collect(Collectors.toList()))
                        .orElse(Collections.emptyList());
                     Identifier image = Identifier.parse(GuiParser.get(choiceCmd, "image", ""));
                     int u = GuiParser.getIndexed(choiceCmd, "uv", 0, 0);
                     int v = GuiParser.getIndexed(choiceCmd, "uv", 1, 0);
                     ImageChoiceLabel.ChoiceEntry entry = new ImageChoiceLabel.ChoiceEntry(choice, tooltips, image, u, v);
                     this.choiceList.add(entry);
                  }
               )
         );
      this.withBorder = GuiParser.get(command, "border", false);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      GuiParser.GuiCommand choicesCmd = new GuiParser.GuiCommand("choices");

      for (ImageChoiceLabel.ChoiceEntry entry : this.choiceList) {
         GuiParser.GuiCommand choiceCmd = new GuiParser.GuiCommand("choice").parameter(entry.choice());
         choicesCmd.command(choiceCmd);
         choiceCmd.command(new GuiParser.GuiCommand("uv").parameter(entry.u()).parameter(entry.v()));
         if (entry.tooltips() != null && !entry.tooltips().isEmpty()) {
            GuiParser.GuiCommand tooltipsCmd = new GuiParser.GuiCommand("tooltips");
            choiceCmd.command(tooltipsCmd);

            for (String tt : entry.tooltips()) {
               tooltipsCmd.parameter(tt);
            }
         }

         choiceCmd.command(new GuiParser.GuiCommand("image").parameter(entry.image().toString()));
      }

      command.command(choicesCmd);
      GuiParser.put(command, "border", this.withBorder, false);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("imagechoicelabel");
   }

   @Override
   public <T> void setGenericValue(T value) {
      if (value instanceof Number number) {
         this.setCurrentChoice(number.intValue());
      } else if (value instanceof Boolean bool) {
         this.setCurrentChoice(bool ? 1 : 0);
      } else if (value != null) {
         this.setCurrentChoice(value.toString());
      }
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      if (Type.INTEGER.equals(type)) {
         return this.getCurrentChoiceIndex();
      } else {
         return Type.BOOLEAN.equals(type) ? this.getCurrentChoiceIndex() != 0 : this.getCurrentChoice();
      }
   }

   private record ChoiceEntry(String choice, List<String> tooltips, Identifier image, int u, int v) {
   }
}
