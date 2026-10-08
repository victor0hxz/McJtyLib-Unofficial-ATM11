package mcjty.lib.gui.widgets;

import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.layout.AbstractLayout;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.Layout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.VerticalAlignment;
import mcjty.lib.gui.layout.VerticalLayout;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class Panel extends AbstractContainerWidget<Panel> {
   public static final String TYPE_PANEL = "panel";
   private Layout layout = new HorizontalLayout();
   private Widget<?> focus = null;

   public Panel layout(Layout layout) {
      this.layout = layout;
      this.markLayoutDirty();
      return this;
   }

   @Override
   public Widget<?> getWidgetAtPosition(double x, double y) {
      if (this.isDirty()) {
         this.layout.doLayout(this.getChildren(), this.bounds.width, this.bounds.height);
         this.markClean();
      }

      return super.getWidgetAtPosition(x, y);
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.draw(gui, graphics, x, y);
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         if (this.isDirty()) {
            this.layout.doLayout(this.getChildren(), this.bounds.width, this.bounds.height);
            this.markClean();
         }

         for (Widget<?> child : this.getChildren()) {
            child.setWindow(this.window);
            child.draw(gui, graphics, xx, yy);
         }
      }
   }

   @Override
   public void drawPhase2(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.drawPhase2(gui, graphics, x, y);
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;

         for (Widget<?> child : this.getChildren()) {
            child.drawPhase2(gui, graphics, xx, yy);
         }
      }
   }

   @Override
   public Widget<Panel> mouseClick(double x, double y, int button) {
      super.mouseClick(x, y, button);
      x -= this.bounds.x;
      y -= this.bounds.y;

      for (Widget<?> child : this.getChildren()) {
         if (child.in(x, y) && child.isVisible()) {
            this.focus = child.mouseClick(x, y, button);
            return this;
         }
      }

      return null;
   }

   @Override
   public void mouseRelease(double x, double y, int button) {
      super.mouseRelease(x, y, button);
      x -= this.bounds.x;
      y -= this.bounds.y;
      if (this.focus != null) {
         this.focus.mouseRelease(x, y, button);
         this.focus = null;
      } else {
         for (Widget<?> child : this.getChildren()) {
            if (child.in(x, y) && child.isVisible()) {
               child.mouseRelease(x, y, button);
               return;
            }
         }
      }
   }

   @Override
   public void mouseMove(double x, double y) {
      super.mouseMove(x, y);
      x -= this.bounds.x;
      y -= this.bounds.y;
      if (this.focus != null) {
         this.focus.mouseMove(x, y);
      } else {
         for (Widget<?> child : this.getChildren()) {
            if (child.in(x, y) && child.isVisible()) {
               child.mouseMove(x, y);
               return;
            }
         }
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      String l = command.getOptionalPar(1, "");
      if ("horizontal".equals(l)) {
         this.layout = new HorizontalLayout();
      } else if ("vertical".equals(l)) {
         this.layout = new VerticalLayout();
      } else {
         this.layout = new PositionalLayout();
      }

      AbstractLayout<?> abstractLayout = (AbstractLayout<?>)this.layout;
      abstractLayout.setSpacing(GuiParser.get(command, "spacing", 5));
      abstractLayout.setHorizontalAlignment(HorizontalAlignment.getByName(GuiParser.get(command, "horizalign", AbstractLayout.DEFAULT_HORIZONTAL_ALIGN.name())));
      abstractLayout.setVerticalAlignment(VerticalAlignment.getByName(GuiParser.get(command, "vertalign", AbstractLayout.DEFAULT_VERTICAL_ALIGN.name())));
      abstractLayout.setHorizontalMargin(GuiParser.get(command, "horizmargin", 5));
      abstractLayout.setVerticalMargin(GuiParser.get(command, "vertmargin", 2));
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      if (this.layout instanceof HorizontalLayout) {
         command.parameter("horizontal");
      } else if (this.layout instanceof VerticalLayout) {
         command.parameter("vertical");
      } else if (this.layout instanceof PositionalLayout) {
         command.parameter("positional");
      }

      if (this.layout instanceof AbstractLayout<?> abstractLayout) {
         GuiParser.put(command, "spacing", abstractLayout.getSpacing(), 5);
         GuiParser.put(command, "horizalign", abstractLayout.getHorizontalAlignment().name(), AbstractLayout.DEFAULT_HORIZONTAL_ALIGN.name());
         GuiParser.put(command, "vertalign", abstractLayout.getVerticalAlignment().name(), AbstractLayout.DEFAULT_VERTICAL_ALIGN.name());
         GuiParser.put(command, "horizmargin", abstractLayout.getHorizontalMargin(), 5);
         GuiParser.put(command, "vertmargin", abstractLayout.getVerticalMargin(), 2);
      }
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("panel");
   }

   @Override
   public <T> void setGenericValue(T value) {
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return null;
   }
}
