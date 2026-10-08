package mcjty.lib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.IconArrivesEvent;
import mcjty.lib.gui.events.IconClickedEvent;
import mcjty.lib.gui.events.IconHolderClickedEvent;
import mcjty.lib.gui.events.IconHoverEvent;
import mcjty.lib.gui.events.IconLeavesEvent;
import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.icons.IconManager;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class IconHolder extends AbstractWidget<IconHolder> {
   public static final String TYPE_ICONHOLDER = "iconholder";
   public static final Key<Integer> PARAM_DX = new Key<>("dx", Type.INTEGER);
   public static final Key<Integer> PARAM_DY = new Key<>("dy", Type.INTEGER);
   public static final boolean DEFAULT_SELECTABLE = false;
   public static final int DEFAULT_BORDER = 0;
   public static final int DEFAULT_SELECTED_BORDER_COLOR = -1;
   private IIcon icon;
   private boolean makeCopy = false;
   private List<IconArrivesEvent> iconArrivesEvents = null;
   private List<IconLeavesEvent> iconLeavesEvents = null;
   private List<IconClickedEvent> iconClickedEvents = null;
   private List<IconHolderClickedEvent> iconHolderClickedEvents = null;
   private List<IconHoverEvent> iconHoverEvents = null;
   private boolean selectable = false;
   private int border = 0;
   private Integer borderColor = null;
   private Integer selectedBorderColor = -1;

   public IIcon getIcon() {
      return this.icon;
   }

   public boolean setIcon(IIcon icon) {
      if (this.fireIconArrived(icon)) {
         this.icon = icon;
         return true;
      } else {
         return false;
      }
   }

   public int getBorder() {
      return this.border;
   }

   public IconHolder border(int border) {
      this.border = border;
      return this;
   }

   public Integer getBorderColor() {
      return this.borderColor;
   }

   public IconHolder borderColor(Integer borderColor) {
      this.borderColor = borderColor;
      return this;
   }

   public Integer getSelectedBorderColor() {
      return this.selectedBorderColor;
   }

   public IconHolder selectedBorderColor(Integer selectedBorderColor) {
      this.selectedBorderColor = selectedBorderColor;
      return this;
   }

   public boolean isSelectable() {
      return this.selectable;
   }

   public IconHolder selectable(boolean selectable) {
      this.selectable = selectable;
      return this;
   }

   public boolean isMakeCopy() {
      return this.makeCopy;
   }

   public IconHolder makeCopy(boolean makeCopy) {
      this.makeCopy = makeCopy;
      return this;
   }

   @Override
   public void mouseMove(double x, double y) {
      if (this.isEnabledAndVisible()) {
         int dx = (int)(x - this.bounds.x - this.border);
         int dy = (int)(y - this.bounds.y - this.border);
         this.fireIconHover(this.icon, dx, dy);
      }
   }

   @Override
   public Widget<?> mouseClick(double x, double y, int button) {
      if (this.isEnabledAndVisible()) {
         if (this.selectable) {
            this.window.setTextFocus(this);
         }

         int dx = (int)(x - this.bounds.x - this.border);
         int dy = (int)(y - this.bounds.y - this.border);
         this.fireIconHolderClicked(this.icon, dx, dy);
         if (this.icon != null && this.fireIconClicked(this.icon, dx, dy) && this.fireIconLeaves(this.icon)) {
            IconManager iconManager = this.window.getWindowManager().getIconManager();
            if (this.makeCopy) {
               iconManager.startDragging(this.icon.clone(), this, dx, dy);
            } else {
               iconManager.startDragging(this.icon, this, dx, dy);
               this.icon = null;
            }
         }
      }

      return null;
   }

   @Override
   public void draw(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.draw(gui, graphics, x, y);
         int xx = x + this.bounds.x;
         int yy = y + this.bounds.y;
         if (this.border > 0 && this.borderColor != null && (!this.selectable || this.window.getTextFocus() != this)) {
            RenderHelper.drawFlatBox(graphics, xx, yy, xx + this.bounds.width, yy + this.bounds.height, this.borderColor, -1);
         }

         if (this.icon != null) {
            this.icon.draw(gui, graphics, xx + this.border, yy + this.border);
         }
      }
   }

   @Override
   public void drawPhase2(Screen gui, GuiGraphicsExtractor graphics, int x, int y) {
      if (this.visible) {
         super.drawPhase2(gui, graphics, x, y);
         if (this.border > 0 && this.selectable && this.selectedBorderColor != null && this.window.getTextFocus() == this) {
            int xx = x + this.bounds.x;
            int yy = y + this.bounds.y;
            RenderHelper.drawFlatBox(graphics, xx, yy, xx + this.bounds.width, yy + this.bounds.height, this.selectedBorderColor, -1);
         }
      }
   }

   public IconHolder arrivesEvent(IconArrivesEvent event) {
      if (this.iconArrivesEvents == null) {
         this.iconArrivesEvents = new ArrayList<>();
      }

      this.iconArrivesEvents.add(event);
      return this;
   }

   private boolean fireIconArrived(IIcon icon) {
      this.fireChannelEvents("arrived");
      if (this.iconArrivesEvents != null) {
         for (IconArrivesEvent event : this.iconArrivesEvents) {
            boolean b = event.iconArrives(icon);
            if (!b) {
               return false;
            }
         }
      }

      return true;
   }

   public IconHolder leavesEvent(IconLeavesEvent event) {
      if (this.iconLeavesEvents == null) {
         this.iconLeavesEvents = new ArrayList<>();
      }

      this.iconLeavesEvents.add(event);
      return this;
   }

   private boolean fireIconLeaves(IIcon icon) {
      this.fireChannelEvents("leaves");
      if (this.iconLeavesEvents != null) {
         for (IconLeavesEvent event : this.iconLeavesEvents) {
            boolean b = event.iconLeaves(icon);
            if (!b) {
               return false;
            }
         }
      }

      return true;
   }

   public IconHolder clickedEvent(IconClickedEvent event) {
      if (this.iconClickedEvents == null) {
         this.iconClickedEvents = new ArrayList<>();
      }

      this.iconClickedEvents.add(event);
      return this;
   }

   private boolean fireIconClicked(IIcon icon, int dx, int dy) {
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, "clicked").put(PARAM_DX, dx).put(PARAM_DY, dy).build());
      if (this.iconClickedEvents != null) {
         for (IconClickedEvent event : this.iconClickedEvents) {
            boolean b = event.iconClicked(icon, dx, dy);
            if (!b) {
               return false;
            }
         }
      }

      return true;
   }

   public IconHolder holderClickedEvent(IconHolderClickedEvent event) {
      if (this.iconHolderClickedEvents == null) {
         this.iconHolderClickedEvents = new ArrayList<>();
      }

      this.iconHolderClickedEvents.add(event);
      return this;
   }

   private void fireIconHolderClicked(IIcon icon, int dx, int dy) {
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, "holderclicked").put(PARAM_DX, dx).put(PARAM_DY, dy).build());
      if (this.iconHolderClickedEvents != null) {
         for (IconHolderClickedEvent event : this.iconHolderClickedEvents) {
            event.holderClicked(icon, dx, dy);
         }
      }
   }

   public IconHolder hoverEvent(IconHoverEvent event) {
      if (this.iconHoverEvents == null) {
         this.iconHoverEvents = new ArrayList<>();
      }

      this.iconHoverEvents.add(event);
      return this;
   }

   private void fireIconHover(IIcon icon, int dx, int dy) {
      this.fireChannelEvents(TypedMap.builder().put(Window.PARAM_ID, "hover").put(PARAM_DX, dx).put(PARAM_DY, dy).build());
      if (this.iconHoverEvents != null) {
         for (IconHoverEvent event : this.iconHoverEvents) {
            event.hover(icon, dx, dy);
         }
      }
   }

   @Override
   public void readFromGuiCommand(GuiParser.GuiCommand command) {
      super.readFromGuiCommand(command);
      this.selectable = GuiParser.get(command, "selectable", false);
      this.border = GuiParser.get(command, "border", 0);
      this.selectedBorderColor = GuiParser.get(command, "selectedbordercolor", -1);
   }

   @Override
   public void fillGuiCommand(GuiParser.GuiCommand command) {
      super.fillGuiCommand(command);
      GuiParser.put(command, "selectable", this.selectable, false);
      GuiParser.put(command, "border", this.border, 0);
      GuiParser.put(command, "selectedbordercolor", this.selectedBorderColor, -1);
   }

   @Override
   public GuiParser.GuiCommand createGuiCommand() {
      return new GuiParser.GuiCommand("iconholder");
   }

   @Override
   public <T> void setGenericValue(T value) {
   }

   @Override
   public Object getGenericValue(Type<?> type) {
      return null;
   }
}
