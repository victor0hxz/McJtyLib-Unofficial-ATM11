package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import mcjty.lib.base.StyleConfig;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Scrollable;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.SliderEvent;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class Slider extends AbstractWidget<Slider> {
   public static final String TYPE_SLIDER = "slider";
   public static final boolean DEFAULT_HORIZONTAL = false;
   public static final int DEFAULT_MINIMUM_KNOBSIZE = 4;
   private boolean dragging = false;
   private int dx;
   private int dy;
   private boolean horizontal = false;
   private int minimumKnobSize = 4;
   private List<SliderEvent> sliderEvents = null;
   private Scrollable scrollable;
   private String scrollableName;

   public Scrollable getScrollable() {
      return this.scrollable;
   }

   public String getScrollableName() {
      return this.scrollableName;
   }

   public Slider scrollableName(String scrollableName) {
      this.scrollableName = scrollableName;
      return this;
   }

   public boolean isHorizontal() {
      return this.horizontal;
   }

   public boolean isVertical() {
      return !this.horizontal;
   }

   public Slider minimumKnobSize(int m) {
      this.minimumKnobSize = m;
      return this;
   }

   public Slider horizontal() {
      this.horizontal = true;
      return this;
   }

   public Slider vertical() {
      this.horizontal = false;
      return this;
   }

   private void findScrollable(Window window) {
      if (this.scrollable == null) {
         Widget<?> child = window.findChild(this.scrollableName);
         if (child instanceof Scrollable) {
            this.scrollable = (Scrollable)child;
         }
      }
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.draw(gui, graphics, x, y);
         this.findScrollable(this.window);
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         RenderHelper.drawThickBeveledBox(
            graphics,
            xx,
            yy,
            xx + this.bounds.width - 1,
            yy + this.bounds.height - 1,
            1,
            StyleConfig.colorSliderTopLeft,
            StyleConfig.colorSliderBottomRight,
            StyleConfig.colorSliderFiller
         );
         int divider = this.scrollable.getMaximum() - this.scrollable.getCountSelected();
         if (this.horizontal) {
            int size = this.calculateKnobSize(divider, this.bounds.width);
            int first = this.calculateKnobOffset(divider, size, this.bounds.width);
            if (this.dragging) {
               RenderHelper.drawBeveledBox(
                  graphics,
                  xx + 1 + first,
                  yy + 2,
                  xx + 1 + first + size - 1,
                  yy + this.bounds.height - 4,
                  StyleConfig.colorSliderKnobDraggingTopLeft,
                  StyleConfig.colorSliderKnobDraggingBottomRight,
                  StyleConfig.colorSliderKnobDraggingFiller
               );
            } else if (this.isHovering()) {
               RenderHelper.drawBeveledBox(
                  graphics,
                  xx + 1 + first,
                  yy + 2,
                  xx + 1 + first + size - 1,
                  yy + this.bounds.height - 4,
                  StyleConfig.colorSliderKnobHoveringTopLeft,
                  StyleConfig.colorSliderKnobHoveringBottomRight,
                  StyleConfig.colorSliderKnobHoveringFiller
               );
            } else {
               RenderHelper.drawBeveledBox(
                  graphics,
                  xx + 1 + first,
                  yy + 2,
                  xx + 1 + first + size - 1,
                  yy + this.bounds.height - 4,
                  StyleConfig.colorSliderKnobTopLeft,
                  StyleConfig.colorSliderKnobBottomRight,
                  StyleConfig.colorSliderKnobFiller
               );
            }

            if (size >= 8) {
               RenderHelper.drawVerticalLine(
                  graphics, xx + 1 + first + size / 2 - 1, yy + 3, yy + this.bounds.height - 6, StyleConfig.colorSliderKnobMarkerLine
               );
               if (size >= 10) {
                  RenderHelper.drawVerticalLine(
                     graphics, xx + 1 + first + size / 2 - 2 - 1, yy + 3, yy + this.bounds.height - 6, StyleConfig.colorSliderKnobMarkerLine
                  );
                  RenderHelper.drawVerticalLine(
                     graphics, xx + 1 + first + size / 2 + 2 - 1, yy + 3, yy + this.bounds.height - 6, StyleConfig.colorSliderKnobMarkerLine
                  );
               }
            }
         } else {
            int sizex = this.calculateKnobSize(divider, this.bounds.height);
            int firstx = this.calculateKnobOffset(divider, sizex, this.bounds.height);
            if (this.dragging) {
               RenderHelper.drawBeveledBox(
                  graphics,
                  xx + 1,
                  yy + 1 + firstx,
                  xx + this.bounds.width - 2,
                  yy + 1 + firstx + sizex - 1,
                  StyleConfig.colorSliderKnobDraggingTopLeft,
                  StyleConfig.colorSliderKnobDraggingBottomRight,
                  StyleConfig.colorSliderKnobDraggingFiller
               );
            } else if (this.isHovering()) {
               RenderHelper.drawBeveledBox(
                  graphics,
                  xx + 1,
                  yy + 1 + firstx,
                  xx + this.bounds.width - 2,
                  yy + 1 + firstx + sizex - 1,
                  StyleConfig.colorSliderKnobHoveringTopLeft,
                  StyleConfig.colorSliderKnobHoveringBottomRight,
                  StyleConfig.colorSliderKnobHoveringFiller
               );
            } else {
               RenderHelper.drawBeveledBox(
                  graphics,
                  xx + 1,
                  yy + 1 + firstx,
                  xx + this.bounds.width - 2,
                  yy + 1 + firstx + sizex - 1,
                  StyleConfig.colorSliderKnobTopLeft,
                  StyleConfig.colorSliderKnobBottomRight,
                  StyleConfig.colorSliderKnobFiller
               );
            }

            if (sizex >= 8) {
               RenderHelper.drawHorizontalLine(
                  graphics, xx + 3, yy + 1 + firstx + sizex / 2 - 1, xx + this.bounds.width - 4, StyleConfig.colorSliderKnobMarkerLine
               );
               if (sizex >= 10) {
                  RenderHelper.drawHorizontalLine(
                     graphics, xx + 3, yy + 1 + firstx + sizex / 2 - 2 - 1, xx + this.bounds.width - 4, StyleConfig.colorSliderKnobMarkerLine
                  );
                  RenderHelper.drawHorizontalLine(
                     graphics, xx + 3, yy + 1 + firstx + sizex / 2 + 2 - 1, xx + this.bounds.width - 4, StyleConfig.colorSliderKnobMarkerLine
                  );
               }
            }
         }
      }
   }

   private int calculateKnobOffset(int divider, int size, int boundsSize) {
      int first;
      if (divider <= 0) {
         first = 0;
      } else {
         first = this.scrollable.getFirstSelected() * (boundsSize - 2 - size) / divider;
      }

      return first;
   }

   private int calculateKnobSize(int divider, int boundsSize) {
      int size;
      if (divider <= 0) {
         size = boundsSize - 2;
      } else {
         size = this.scrollable.getCountSelected() * (boundsSize - 2) / this.scrollable.getMaximum();
      }

      if (size < this.minimumKnobSize) {
         size = this.minimumKnobSize;
      }

      return size;
   }

   private void updateScrollable(double x, double y) {
      int divider = this.scrollable.getMaximum() - this.scrollable.getCountSelected();
      int first;
      if (divider <= 0) {
         first = 0;
      } else if (this.horizontal) {
         int size = this.calculateKnobSize(divider, this.bounds.width);
         first = (int)((x - this.bounds.x - this.dx) * divider / (this.bounds.width - 4 - size));
      } else {
         int size = this.calculateKnobSize(divider, this.bounds.height);
         first = (int)((y - this.bounds.y - this.dy) * divider / (this.bounds.height - 4 - size));
      }

      if (first > divider) {
         first = divider;
      }

      if (first < 0) {
         first = 0;
      }

      this.scrollable.setFirstSelected(first);
   }

   public Slider event(SliderEvent event) {
      if (this.sliderEvents == null) {
         this.sliderEvents = new ArrayList<>();
      }

      this.sliderEvents.add(event);
      return this;
   }

   public void removeSliderEvent(SliderEvent event) {
      if (this.sliderEvents != null) {
         this.sliderEvents.remove(event);
      }
   }

   private void fireSliderEvents() {
      this.fireChannelEvents();
      if (this.sliderEvents != null) {
         for (SliderEvent event : this.sliderEvents) {
            event.sliderReleased();
         }
      }
   }

   @Override
   public boolean mouseScrolled(double x, double y, double dx, double dy) {
      int first = this.scrollable.getFirstSelected();
      int divider = this.scrollable.getMaximum() - this.scrollable.getCountSelected();
      if (divider <= 0) {
         first = 0;
      } else if (dy > 0.0) {
         first -= 3;
      } else if (dy < 0.0) {
         first += 3;
      }

      if (first > divider) {
         first = divider;
      }

      if (first < 0) {
         first = 0;
      }

      this.scrollable.setFirstSelected(first);
      return true;
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      super.mouseClick(x, y, button);
      this.dragging = true;
      this.findScrollable(this.window);
      int divider = this.scrollable.getMaximum() - this.scrollable.getCountSelected();
      if (this.horizontal) {
         int size = this.calculateKnobSize(divider, this.bounds.width);
         int first = this.calculateKnobOffset(divider, size, this.bounds.width);
         this.dx = (int)(x - this.bounds.x - first);
         this.dy = 0;
      } else {
         int size = this.calculateKnobSize(divider, this.bounds.height);
         int first = this.calculateKnobOffset(divider, size, this.bounds.height);
         this.dx = 0;
         this.dy = (int)(y - this.bounds.y - first);
      }

      return this;
   }

   @Override
   public void mouseRelease(double x, double y, int button) {
      super.mouseRelease(x, y, button);
      if (this.dragging) {
         this.updateScrollable(x, y);
         this.dragging = false;
         this.fireSliderEvents();
      }
   }

   @Override
   public void mouseMove(double x, double y) {
      super.mouseMove(x, y);
      if (this.dragging) {
         this.updateScrollable(x, y);
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      this.horizontal = GuiParser.get(command, "horizontal", false);
      this.minimumKnobSize = GuiParser.get(command, "minimumknob", 4);
      this.scrollableName = GuiParser.get(command, "scrollable", null);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      GuiParser.put(command, "horizontal", this.horizontal, false);
      GuiParser.put(command, "minimumknob", this.minimumKnobSize, 4);
      GuiParser.put(command, "scrollable", this.scrollableName, null);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("slider");
   }

   @Override
   public <T> void setGenericValue(T value) {
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return null;
   }
}
