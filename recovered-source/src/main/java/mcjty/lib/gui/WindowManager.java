package mcjty.lib.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import mcjty.lib.client.GuiTools;
import mcjty.lib.gui.icons.IconManager;
import mcjty.lib.gui.widgets.AbstractContainerWidget;
import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public class WindowManager {
   private final IconManager iconManager = new IconManager(this);
   private final Screen gui;
   private final List<Window> windows = new ArrayList<>();
   private final List<Window> modalWindows = new ArrayList<>();
   private int mouseWheelX = -1;
   private int mouseWheelY = -1;

   public WindowManager(Screen gui) {
      this.gui = gui;
   }

   public Screen getGui() {
      return this.gui;
   }

   public IconManager getIconManager() {
      return this.iconManager;
   }

   public void clearFocus() {
      Stream.concat(this.windows.stream(), this.modalWindows.stream()).forEach(w -> w.setFocus(null));
   }

   public void setFocus(Widget<?> w) {
      this.clearFocus();
      Stream.concat(this.windows.stream(), this.modalWindows.stream())
         .filter(window -> window.isWidgetOnWindow(w))
         .findFirst()
         .ifPresent(window -> window.setTextFocus(w));
   }

   public Widget<?> getTextFocus() {
      return this.getInteractableWindows().map(Window::getTextFocus).filter(Objects::nonNull).findFirst().orElse(null);
   }

   public WindowManager addWindow(Window w) {
      this.windows.add(w);
      w.setWindowManager(this);
      return this;
   }

   public WindowManager addModalWindow(Window w) {
      this.modalWindows.add(w);
      w.setWindowManager(this);
      return this;
   }

   public Window createModalWindow(AbstractContainerWidget<?> topLevel) {
      Window w = new Window(this.gui, topLevel);
      this.addModalWindow(w);
      return w;
   }

   public void closeWindow(Window window) {
      if (this.windows.contains(window)) {
         this.windows.remove(window);
      } else if (this.modalWindows.contains(window)) {
         this.modalWindows.remove(window);
      }
   }

   public int getMouseWheelX() {
      return this.mouseWheelX;
   }

   public int getMouseWheelY() {
      return this.mouseWheelY;
   }

   public <T extends GenericTileEntity> void syncBindings(T te) {
      this.windows.forEach(window -> window.syncBindings(te));
      this.modalWindows.forEach(window -> window.syncBindings(te));
   }

   public void draw(GuiGraphicsExtractor graphics) {
      this.mouseWheelX = 0;
      this.mouseWheelY = 0;
      this.windows.forEach(window -> window.draw(graphics));
      this.modalWindows.forEach(window -> window.draw(graphics));
      this.iconManager.draw(this.gui, graphics);
   }

   private Stream<Window> getInteractableWindows() {
      if (this.modalWindows.isEmpty()) {
         return this.windows.stream();
      } else {
         Window window = this.modalWindows.get(this.modalWindows.size() - 1);
         return Stream.<Window>builder().add(window).build();
      }
   }

   public List<Window> getWindows() {
      return this.windows;
   }

   public Stream<Window> getModalWindows() {
      return this.modalWindows.stream();
   }

   public void drawTooltips(GuiGraphicsExtractor graphics) {
      int x = GuiTools.getRelativeX(this.gui);
      int y = GuiTools.getRelativeY(this.gui);
      this.getInteractableWindows().forEach(w -> {
         List<String> tooltips = w.getTooltips();
         if (tooltips != null) {
            GenericGuiContainer<?, ?> gui = (GenericGuiContainer<?, ?>)this.gui;
            gui.drawHoveringText(graphics, tooltips, w.getTooltipItems(), x - gui.getGuiLeft(), y - gui.getGuiTop(), gui.getMinecraft().font);
         }
      });
   }

   public Optional<Widget<?>> findWidgetAtPosition(double x, double y) {
      Stream<Widget<?>> s = this.windows.stream().map(w -> w.getWidgetAtPosition(x, y));
      return s.filter(Objects::nonNull).findFirst();
   }

   public void mouseClicked(double x, double y, int button) {
      if (!this.iconManager.isClickHoldToDrag() && this.iconManager.isDragging()) {
         if (button == 1) {
            this.iconManager.cancelDragging();
         } else {
            this.iconManager.stopDragging(x, y);
         }
      } else {
         this.getInteractableWindows().forEach(w -> w.mouseClicked(x, y, button));
      }
   }

   public void mouseDragged(double x, double y, int button) {
      this.getInteractableWindows().forEach(w -> w.mouseDragged(x, y, button));
   }

   public void mouseScrolled(double x, double y, double dx, double dy) {
      this.getInteractableWindows().forEach(w -> w.mouseScrolled(x, y, dx, dy));
   }

   public void mouseReleased(double x, double y, int state) {
      if (this.iconManager.isClickHoldToDrag() && this.iconManager.isDragging()) {
         this.iconManager.stopDragging(x, y);
      }

      this.getInteractableWindows().forEach(w -> w.mouseReleased(x, y, state));
   }

   public boolean keyTyped(int keyCode, int scanCode) {
      return this.getInteractableWindows().noneMatch(w -> w.keyTyped(keyCode, scanCode));
   }

   public boolean charTyped(char codePoint) {
      return this.getInteractableWindows().noneMatch(w -> w.charTyped(codePoint));
   }
}
