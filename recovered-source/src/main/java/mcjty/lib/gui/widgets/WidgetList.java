package mcjty.lib.gui.widgets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Scrollable;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SelectionEvent;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class WidgetList extends AbstractContainerWidget<WidgetList> implements Scrollable {
   public static final String TYPE_WIDGETLIST = "widgetlist";
   public static final Key<Integer> PARAM_INDEX = new Key<>("index", Type.INTEGER);
   public static final int DEFAULT_ROWHEIGHT = 16;
   public static final boolean DEFAULT_PROPAGATE = false;
   public static final boolean DEFAULT_NOSELECTION = false;
   public static final boolean DEFAULT_INVISIBLE_SELECTION = false;
   public static final boolean DEFAULT_DRAW_HORIZONTAL_LINES = true;
   public static final int DEFAULT_LEFT_MARGIN = 2;
   public static final int DEFAULT_TOP_MARGIN = 1;
   private int rowheight = 16;
   private int first = 0;
   private int selected = -1;
   private long prevTime = -1L;
   private boolean propagateEventsToChildren = false;
   private List<SelectionEvent> selectionEvents = null;
   private Set<Integer> hilightedRows = new HashSet<>();
   private boolean noselection = false;
   private boolean invisibleselection = false;
   private boolean drawHorizontalLines = true;
   private int leftMargin = 2;
   private int topMargin = 1;

   public WidgetList() {
      this.filledRectThickness(-1);
      this.filledBackground(StyleConfig.colorListBackground);
   }

   public int getRowheight() {
      return this.rowheight;
   }

   public WidgetList rowheight(int rowheight) {
      this.rowheight = rowheight;
      return this;
   }

   public boolean isDrawHorizontalLines() {
      return this.drawHorizontalLines;
   }

   public WidgetList drawHorizontalLines(boolean drawHorizontalLines) {
      this.drawHorizontalLines = drawHorizontalLines;
      return this;
   }

   public int getLeftMargin() {
      return this.leftMargin;
   }

   public WidgetList leftMargin(int leftMargin) {
      this.leftMargin = leftMargin;
      return this;
   }

   public int getTopMargin() {
      return this.topMargin;
   }

   public WidgetList topMargin(int topMargin) {
      this.topMargin = topMargin;
      return this;
   }

   public int getSelected() {
      return this.selected >= this.getChildren().size() ? -1 : this.selected;
   }

   public WidgetList selected(int selected) {
      this.selected = selected;
      return this;
   }

   public boolean isPropagateEventsToChildren() {
      return this.propagateEventsToChildren;
   }

   public boolean isInvisibleSelection() {
      return this.invisibleselection;
   }

   public WidgetList invisibleSelection(boolean invisibleselection) {
      this.invisibleselection = invisibleselection;
      return this;
   }

   public WidgetList noSelectionMode(boolean m) {
      this.noselection = m;
      return this;
   }

   public boolean isNoSelection() {
      return this.noselection;
   }

   public WidgetList propagateEventsToChildren(boolean propagateEventsToChildren) {
      this.propagateEventsToChildren = propagateEventsToChildren;
      return this;
   }

   public void addHilightedRow(int index) {
      this.hilightedRows.add(index);
   }

   public void clearHilightedRows() {
      this.hilightedRows.clear();
   }

   @Override
   public Widget<?> getWidgetAtPosition(double x, double y) {
      x -= this.bounds.x;
      y -= this.bounds.y;
      this.doLayout();

      for (int i = this.first; i < this.first + this.getCountSelected() && i < this.getChildren().size(); i++) {
         Widget<?> child = this.getChildren().get(i);
         if (child.in(x, y) && child.isVisible()) {
            return child.getWidgetAtPosition(x, y);
         }
      }

      return this;
   }

   private void doLayout() {
      int top = 0;

      for (int i = this.first; i < this.first + this.getCountSelected() && i < this.getChildren().size(); i++) {
         Widget<?> child = this.getChildren().get(i);
         int rh = this.rowheight == -1 ? child.getDesiredHeight() : this.rowheight;
         child.bounds(0, top, this.bounds.width, rh);
         top += rh;
      }
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         this.mouseScrolled(x, y, 0.0, 0.0);
         super.draw(gui, graphics, x, y);
         int xx = x + this.bounds.x + this.leftMargin;
         int yy = y + this.bounds.y + this.topMargin;
         int top = 0;

         for (int i = this.first; i < this.first + this.getCountSelected() && i < this.getChildren().size(); i++) {
            Widget<?> child = this.getChildren().get(i);
            int rh = this.rowheight == -1 ? child.getDesiredHeight() : this.rowheight;
            child.bounds(0, top, this.bounds.width, rh);
            boolean hilighted = this.hilightedRows.contains(i);
            if (top + rh - 1 < this.bounds.height - 3 && this.drawHorizontalLines) {
               RenderHelper.drawHorizontalLine(graphics, xx + 2, yy + top + rh - 1, xx + this.bounds.width - 7, StyleConfig.colorListSeparatorLine);
            }

            if (!this.invisibleselection) {
               if (i == this.selected && hilighted) {
                  RenderHelper.drawHorizontalGradientRect(
                     xx,
                     yy + top + 1,
                     xx + this.bounds.width - 5,
                     yy + top + rh - 2,
                     StyleConfig.colorListSelectedHighlightedGradient1,
                     StyleConfig.colorListSelectedHighlightedGradient2
                  );
               } else if (i == this.selected) {
                  RenderHelper.drawHorizontalGradientRect(
                     xx,
                     yy + top + 1,
                     xx + this.bounds.width - 5,
                     yy + top + rh - 2,
                     StyleConfig.colorListSelectedGradient1,
                     StyleConfig.colorListSelectedGradient2
                  );
               } else if (hilighted) {
                  RenderHelper.drawHorizontalGradientRect(
                     xx,
                     yy + top + 1,
                     xx + this.bounds.width - 5,
                     yy + top + rh - 2,
                     StyleConfig.colorListHighlightedGradient1,
                     StyleConfig.colorListHighlightedGradient2
                  );
               }
            }

            if (this.isEnabledAndVisible()) {
               child.setWindow(this.window);
               child.draw(gui, graphics, xx, yy);
            } else {
               boolean en = child.isEnabled();
               child.enabled(false);
               child.setWindow(this.window);
               child.draw(gui, graphics, xx, yy);
               child.enabled(en);
            }

            top += rh;
         }
      }
   }

   @Override
   public void drawPhase2(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.drawPhase2(gui, graphics, x, y);
         int xx = x + this.bounds.x + this.leftMargin;
         int yy = y + this.bounds.y + this.topMargin;
         int top = 0;

         for (int i = this.first; i < this.first + this.getCountSelected() && i < this.getChildren().size(); i++) {
            Widget<?> child = this.getChildren().get(i);
            int rh = this.rowheight == -1 ? child.getDesiredHeight() : this.rowheight;
            if (this.isEnabledAndVisible()) {
               child.drawPhase2(gui, graphics, xx, yy);
            }

            top += rh;
         }
      }
   }

   @Override
   public void mouseMove(double x, double y) {
      if (this.isEnabledAndVisible()) {
         if (!this.noselection) {
            int newSelected = -1;
            int top = this.bounds.y;

            for (int i = this.first; i < this.first + this.getCountSelected() && i < this.getChildren().size(); i++) {
               int rh = this.rowheight == -1 ? this.getChildren().get(i).getDesiredHeight() : this.rowheight;
               Rectangle r = new Rectangle(this.bounds.x, top, this.bounds.width, rh);
               if (r.contains(x, y)) {
                  newSelected = i;
                  break;
               }

               top += rh;
            }

            if (this.propagateEventsToChildren && newSelected != -1) {
               Widget<?> child = this.getSelectedWidgetSafe(newSelected);
               double xx = x - this.bounds.x;
               double yy = y - this.bounds.y;
               if (child != null && child.in(xx, yy) && child.isVisible()) {
                  child.mouseMove(xx, yy);
               }
            }
         }
      }
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (!this.isEnabledAndVisible()) {
         return null;
      } else if (this.noselection) {
         return null;
      } else {
         int newSelected = -1;
         int top = this.bounds.y;

         for (int i = this.first; i < this.first + this.getCountSelected() && i < this.getChildren().size(); i++) {
            int rh = this.rowheight == -1 ? this.getChildren().get(i).getDesiredHeight() : this.rowheight;
            Rectangle r = new Rectangle(this.bounds.x, top, this.bounds.width, rh);
            if (r.contains(x, y)) {
               newSelected = i;
               break;
            }

            top += rh;
         }

         if (newSelected != this.selected) {
            this.selected = newSelected;
            this.fireSelectionEvents(this.selected);
         }

         if (this.propagateEventsToChildren && this.selected != -1) {
            Widget<?> child = this.getSelectedWidgetSafe(this.selected);
            double xx = x - this.bounds.x;
            double yy = y - this.bounds.y;
            if (child != null && child.in(xx, yy) && child.isVisible()) {
               child.mouseClick(xx, yy, button);
            }
         }

         long t = System.currentTimeMillis();
         if (this.prevTime != -1L && t - this.prevTime < 250L) {
            this.fireDoubleClickEvent(this.selected);
         }

         this.prevTime = t;
         return null;
      }
   }

   @Override
   public void mouseRelease(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         if (!this.noselection) {
            if (this.propagateEventsToChildren && this.selected != -1) {
               Widget<?> child = this.getSelectedWidgetSafe(this.selected);
               double xx = x - this.bounds.x;
               double yy = y - this.bounds.y;
               if (child != null && child.in(xx, yy) && child.isVisible()) {
                  child.mouseRelease(xx, yy, button);
               }
            }

            super.mouseRelease(x, y, button);
         }
      }
   }

   private Widget<?> getSelectedWidgetSafe(int sel) {
      return sel < this.getChildren().size() ? this.getChildren().get(sel) : null;
   }

   @Override
   public boolean mouseScrolled(double x, double y, double dx, double dy) {
      int divider = this.getMaximum() - this.getCountSelected();
      if (divider <= 0) {
         this.first = 0;
      } else if (dy > 0.0) {
         this.first -= 3;
      } else if (dy < 0.0) {
         this.first += 3;
      }

      if (this.first > divider) {
         this.first = divider;
      }

      if (this.first < 0) {
         this.first = 0;
      }

      return true;
   }

   @Override
   public int getMaximum() {
      return this.getChildren().size();
   }

   @Override
   public int getCountSelected() {
      if (this.bounds == null) {
         return 0;
      } else if (this.rowheight != -1) {
         return this.bounds.height / this.rowheight;
      } else {
         int totalh = 0;
         int cnt = 0;

         for (int i = this.first; i < this.getChildren().size(); i++) {
            int rh = this.getChildren().get(i).getDesiredHeight();
            if (totalh + rh > this.bounds.height) {
               break;
            }

            totalh += rh;
            cnt++;
         }

         return cnt;
      }
   }

   @Override
   public int getFirstSelected() {
      return this.first;
   }

   @Override
   public void setFirstSelected(int first) {
      this.first = first;
   }

   public WidgetList event(SelectionEvent event) {
      if (this.selectionEvents == null) {
         this.selectionEvents = new ArrayList<>();
      }

      this.selectionEvents.add(event);
      return this;
   }

   public void removeSelectionEvent(SelectionEvent event) {
      if (this.selectionEvents != null) {
         this.selectionEvents.remove(event);
      }
   }

   private void fireSelectionEvents(int index) {
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, "select").put(PARAM_INDEX, index).build());
      if (this.selectionEvents != null) {
         for (SelectionEvent event : this.selectionEvents) {
            event.select(index);
         }
      }
   }

   private void fireDoubleClickEvent(int index) {
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, "doubleclick").put(PARAM_INDEX, index).build());
      if (this.selectionEvents != null) {
         for (SelectionEvent event : this.selectionEvents) {
            event.doubleClick(index);
         }
      }
   }

   public WidgetList removeChild(Widget<?> child) {
      int index = this.getChildren().indexOf(child);
      if (index != -1) {
         Set<Integer> newHighlights = new HashSet<>();

         for (Integer i : this.hilightedRows) {
            if (i < index) {
               newHighlights.add(i);
            } else if (i > index) {
               newHighlights.add(i - 1);
            }
         }

         this.hilightedRows = newHighlights;
      }

      return (WidgetList)super.removeChild(child);
   }

   @Override
   public void removeChildren() {
      super.removeChildren();
      this.hilightedRows.clear();
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      this.rowheight = GuiParser.get(command, "rowheight", 16);
      this.propagateEventsToChildren = GuiParser.get(command, "propagate", false);
      this.noselection = GuiParser.get(command, "noselection", false);
      this.invisibleselection = GuiParser.get(command, "invisibleselection", false);
      this.drawHorizontalLines = GuiParser.get(command, "horizontallines", true);
      this.leftMargin = GuiParser.get(command, "leftmargin", 2);
      this.topMargin = GuiParser.get(command, "topmargin", 1);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      GuiParser.put(command, "rowheight", this.rowheight, 16);
      GuiParser.put(command, "propagate", this.propagateEventsToChildren, false);
      GuiParser.put(command, "noselection", this.noselection, false);
      GuiParser.put(command, "invisibleselection", this.invisibleselection, false);
      GuiParser.put(command, "horizontallines", this.drawHorizontalLines, true);
      GuiParser.put(command, "leftmargin", this.leftMargin, 2);
      GuiParser.put(command, "topmargin", this.topMargin, 1);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("widgetlist");
   }

   @Override
   public <T> void setGenericValue(T value) {
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return null;
   }
}
