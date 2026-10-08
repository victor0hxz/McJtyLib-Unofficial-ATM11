package mcjty.lib.gui.widgets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import mcjty.lib.gui.GuiParser;

public abstract class AbstractContainerWidget<P extends AbstractContainerWidget<P>> extends AbstractWidget<P> {
   private final List<Widget<?>> children = new ArrayList<>();

   @Override
   public void setBounds(Rectangle bounds) {
      this.markLayoutDirty();
      super.setBounds(bounds);
   }

   @Override
   public void bounds(int x, int y, int w, int h) {
      this.markLayoutDirty();
      super.bounds(x, y, w, h);
   }

   @Override
   public Widget<?> getWidgetAtPosition(double x, double y) {
      x -= this.bounds.x;
      y -= this.bounds.y;

      for (Widget<?> child : this.children) {
         if (child.in(x, y) && child.isVisible()) {
            return child.getWidgetAtPosition(x, y);
         }
      }

      return this;
   }

   @Override
   public boolean mouseScrolled(double x, double y, double dx, double dy) {
      x -= this.bounds.x;
      y -= this.bounds.y;

      for (Widget<?> child : this.children) {
         if (child.in(x, y) && child.isVisible() && child.mouseScrolled(x, y, dx, dy)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean containsWidget(Widget<?> w) {
      if (w == this) {
         return true;
      } else {
         for (Widget<?> child : this.children) {
            if (child.containsWidget(w)) {
               return true;
            }
         }

         return false;
      }
   }

   public P children(Widget<?>... children) {
      for (Widget<?> child : children) {
         this.addChild(child);
      }

      return this.getThis();
   }

   private P addChild(Widget<?> child) {
      if (child == null) {
         throw new RuntimeException("THIS IS NOT POSSIBLE!");
      } else {
         this.children.add(child);
         this.markLayoutDirty();
         return this.getThis();
      }
   }

   public P removeChild(Widget<?> child) {
      this.children.remove(child);
      this.markLayoutDirty();
      return this.getThis();
   }

   public void removeChildren() {
      this.children.clear();
      this.markLayoutDirty();
   }

   public int getChildCount() {
      return this.children.size();
   }

   public List<Widget<?>> getChildren() {
      return this.children;
   }

   public <T extends Widget<?>> T getChild(int index) {
      return (T)this.children.get(index);
   }

   public <T extends Widget<?>> T findChild(String name) {
      for (Widget<?> child : this.children) {
         if (name.equals(child.getName())) {
            return (T)child;
         }
      }

      return null;
   }

   public Widget<?> findChildRecursive(String name) {
      for (Widget<?> child : this.children) {
         if (name.equals(child.getName())) {
            return child;
         }

         if (child instanceof AbstractContainerWidget) {
            Widget<?> widget = ((AbstractContainerWidget)child).findChildRecursive(name);
            if (widget != null) {
               return widget;
            }
         }
      }

      return null;
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      command.commands().forEach(cmd -> {
         String type = cmd.getId();
         Widget<?> widget = Widgets.createWidget(type);
         if (widget != null) {
            widget.readFromGuiCommand(cmd);
            this.children.add(widget);
         }
      });
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);

      for (Widget<?> child : this.children) {
         GuiParser.GuiCommand childCommand = child.createGuiCommand();
         child.fillGuiCommand(childCommand);
         command.command(childCommand);
      }
   }
}
