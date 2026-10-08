package mcjty.lib.gui.icons;

import java.util.Optional;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.widgets.IconHolder;
import mcjty.lib.gui.widgets.Widget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class IconManager {
   private final WindowManager windowManager;
   private IIcon draggingIcon;
   private IconHolder origin;
   private int dx;
   private int dy;
   private boolean clickHoldToDrag = false;

   public IconManager(WindowManager windowManager) {
      this.windowManager = windowManager;
   }

   public void startDragging(IIcon icon, IconHolder origin, int iconX, int iconY) {
      this.draggingIcon = icon;
      this.origin = origin;
      this.dx = iconX - 2;
      this.dy = iconY - 1;
   }

   public boolean isClickHoldToDrag() {
      return this.clickHoldToDrag;
   }

   public void setClickHoldToDrag(boolean clickHoldToDrag) {
      this.clickHoldToDrag = clickHoldToDrag;
   }

   public void cancelDragging() {
      if (this.draggingIcon != null) {
         if (this.origin != null) {
            this.origin.setIcon(this.draggingIcon);
         }

         this.draggingIcon = null;
         this.origin = null;
      }
   }

   public void stopDragging(double x, double y) {
      if (this.draggingIcon != null) {
         IconHolder iconHolder = this.findClosestIconHolder(x, y);
         if (iconHolder != null && iconHolder.getIcon() == null) {
            if (!iconHolder.setIcon(this.draggingIcon)) {
               if (this.origin != null) {
                  this.origin.setIcon(this.draggingIcon);
               }
            } else if (iconHolder.isSelectable()) {
               this.windowManager.setFocus(iconHolder);
            }
         } else if (this.origin != null) {
            this.origin.setIcon(this.draggingIcon);
         }

         this.draggingIcon = null;
         this.origin = null;
      }
   }

   private IconHolder findClosestIconHolder(double x, double y) {
      Optional<Widget<?>> widget = this.windowManager.findWidgetAtPosition(x, y);
      return widget.isPresent() && widget.get() instanceof IconHolder ? (IconHolder)widget.get() : null;
   }

   public boolean isDragging() {
      return this.draggingIcon != null;
   }

   public void draw(Screen gui, GuiGraphicsExtractor graphics) {
      if (this.draggingIcon != null) {
         this.draggingIcon.draw(gui, graphics, this.getRelativeX() - this.dx, this.getRelativeY() - this.dy);
      }
   }

   private int getRelativeX() {
      Screen gui = this.windowManager.getGui();
      int width = gui.getMinecraft().getWindow().getScreenWidth();
      return width <= 0 ? 0 : (int)gui.getMinecraft().mouseHandler.xpos() * gui.width / width;
   }

   private int getRelativeY() {
      Screen gui = this.windowManager.getGui();
      int height = gui.getMinecraft().getWindow().getScreenHeight();
      return height <= 0 ? 0 : (int)gui.getMinecraft().mouseHandler.ypos() * gui.height / height;
   }
}
