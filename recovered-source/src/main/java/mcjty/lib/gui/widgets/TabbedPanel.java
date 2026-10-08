package mcjty.lib.gui.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class TabbedPanel extends AbstractContainerWidget<Panel> {
   public static final String TYPE_TABBEDPANEL = "tabbedpanel";
   private Widget<?> current = null;
   private final Map<String, Widget<?>> pages = new HashMap<>();

   public TabbedPanel page(String name, Widget<?> child) {
      this.children(child);
      this.pages.put(name, child);
      return this;
   }

   public Widget<?> getCurrent() {
      return this.current;
   }

   public String getCurrentName() {
      for (Entry<String, Widget<?>> me : this.pages.entrySet()) {
         if (this.current == me.getValue()) {
            return me.getKey();
         }
      }

      return null;
   }

   public TabbedPanel current(Widget<?> current) {
      this.current = current;
      return this;
   }

   public TabbedPanel current(String name) {
      this.current = this.pages.get(name);
      return this;
   }

   @Override
   public Widget<?> getWidgetAtPosition(double x, double y) {
      if (this.current == null) {
         return this;
      } else {
         this.setChildBounds();
         x -= this.bounds.x;
         y -= this.bounds.y;
         return this.current.getWidgetAtPosition(x, y);
      }
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.draw(gui, graphics, x, y);
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         this.drawBox(graphics, xx, yy, -65536);
         this.setChildBounds();
         if (this.current != null) {
            this.current.setWindow(this.window);
            this.current.draw(gui, graphics, xx, yy);
         }
      }
   }

   private void setChildBounds() {
      if (this.isDirty()) {
         for (Widget<?> child : this.getChildren()) {
            child.bounds(0, 0, this.getBounds().width, this.getBounds().height);
         }

         this.markClean();
      }
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      super.mouseClick(x, y, button);
      this.setChildBounds();
      x -= this.bounds.x;
      y -= this.bounds.y;
      return this.current != null && this.current.in(x, y) && this.current.isVisible() ? this.current.mouseClick(x, y, button) : null;
   }

   @Override
   public void mouseRelease(double x, double y, int button) {
      super.mouseRelease(x, y, button);
      this.setChildBounds();
      x -= this.bounds.x;
      y -= this.bounds.y;
      if (this.current != null) {
         this.current.mouseRelease(x, y, button);
      }
   }

   @Override
   public void mouseMove(double x, double y) {
      super.mouseMove(x, y);
      this.setChildBounds();
      x -= this.bounds.x;
      y -= this.bounds.y;
      if (this.current != null) {
         this.current.mouseMove(x, y);
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("tabbedpanel");
   }

   @Override
   public <T> void setGenericValue(T value) {
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return null;
   }
}
