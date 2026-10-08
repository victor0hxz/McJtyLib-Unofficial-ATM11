package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.TagSelectorWindow;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.TagChoiceEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class TagSelector extends AbstractLabel<TagSelector> {
   public static final String TYPE_TAGSELECTOR = "tagselector";
   public static final Key<String> PARAM_TAG = new Key<>("tag", Type.STRING);
   private static final Pattern COMPILE = Pattern.compile("[/:]");
   private String currentTag = null;
   private List<TagChoiceEvent> choiceEvents = null;
   private final TagSelectorWindow selector = new TagSelectorWindow();
   private String type = "item";

   public TagSelector() {
      this.text("");
   }

   public TagSelector current(String tag) {
      if (Objects.equals(this.currentTag, tag)) {
         return this;
      } else {
         this.currentTag = tag;
         return this;
      }
   }

   public String getCurrentTag() {
      return this.currentTag;
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

         String tag = this.getCurrentTagSafe();
         String[] split = COMPILE.split(tag);
         this.text(split[split.length - 1]);
         super.drawOffset(gui, graphics, x, y, 0, 1);
      }
   }

   private String getCurrentTagSafe() {
      String tag = this.getCurrentTag();
      if (tag == null) {
         tag = "<unset>";
      }

      return tag;
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         this.selector.create(this.window, this.type, t -> {
            this.current(t);
            this.fireChoiceEvents(t);
         }, this::getCurrentTag, false);
      }

      return null;
   }

   public TagSelector event(TagChoiceEvent event) {
      if (this.choiceEvents == null) {
         this.choiceEvents = new ArrayList<>();
      }

      this.choiceEvents.add(event);
      return this;
   }

   public void removeChoiceEvent(TagChoiceEvent event) {
      if (this.choiceEvents != null) {
         this.choiceEvents.remove(event);
      }
   }

   private void fireChoiceEvents(String tag) {
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, "choice").put(PARAM_TAG, tag).build());
      if (this.choiceEvents != null) {
         for (TagChoiceEvent event : this.choiceEvents) {
            event.tagChanged(tag);
         }
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      this.type = GuiParser.get(command, "type", "item");
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      command.removeParameter(1);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("tagselector");
   }

   @Override
   public <T> void setGenericValue(T value) {
      this.current(value == null ? null : value.toString());
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return this.getCurrentTag();
   }
}
