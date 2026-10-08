package mcjty.lib.gui;

import java.util.List;
import mcjty.lib.client.GuiTools;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public abstract class GuiItemScreen extends Screen {
   protected Window window;
   protected int xSize;
   protected int ySize;
   protected int guiLeft;
   protected int guiTop;
   private final ManualEntry manual;
   private GuiSideWindow sideWindow;

   public GuiItemScreen(int xSize, int ySize, ManualEntry manualEntry) {
      super(ComponentFactory.literal("todo"));
      this.xSize = xSize;
      this.ySize = ySize;
      this.sideWindow = new GuiSideWindow(manualEntry.manual(), manualEntry.entry(), manualEntry.page());
      this.manual = manualEntry;
   }

   public void setWindowDimensions(int x, int y) {
      this.xSize = x;
      this.ySize = y;
      this.sideWindow = new GuiSideWindow(this.manual.manual(), this.manual.entry(), this.manual.page());
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void init() {
      super.init();
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
      this.sideWindow.initGui(this.getMinecraft(), this, this.guiLeft, this.guiTop, this.xSize, this.ySize);
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      boolean rc = super.mouseClicked(event, doubleClick);
      this.window.mouseClicked(event.x(), event.y(), event.button());
      this.sideWindow.getWindow().mouseClicked(event.x(), event.y(), event.button());
      return rc;
   }

   public boolean mouseDragged(MouseButtonEvent event, double scaledX, double scaledY) {
      boolean rc = super.mouseDragged(event, scaledX, scaledY);
      this.window.mouseDragged(event.x(), event.y(), event.button());
      this.sideWindow.getWindow().mouseDragged(event.x(), event.y(), event.button());
      return rc;
   }

   public boolean mouseScrolled(double x, double y, double dx, double dy) {
      boolean rc = super.mouseScrolled(x, y, dx, dy);
      this.window.mouseScrolled(x, y, dx, dy);
      this.sideWindow.getWindow().mouseScrolled(x, y, dx, dy);
      return rc;
   }

   public boolean mouseReleased(MouseButtonEvent event) {
      boolean rc = super.mouseReleased(event);
      this.window.mouseReleased(event.x(), event.y(), event.button());
      this.sideWindow.getWindow().mouseReleased(event.x(), event.y(), event.button());
      return rc;
   }

   public boolean keyPressed(KeyEvent event) {
      boolean rc = super.keyPressed(event);
      this.window.keyTyped(event.key(), event.scancode());
      return rc;
   }

   public void drawWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      this.extractBackground(graphics, mouseX, mouseY, partialTicks);
      this.window.draw(graphics);
      this.sideWindow.getWindow().draw(graphics);
      List<String> tooltips = this.window.getTooltips();
      if (tooltips != null) {
         int x = GuiTools.getRelativeX(this);
         int var7 = GuiTools.getRelativeY(this);
      }

      tooltips = this.sideWindow.getWindow().getTooltips();
      if (tooltips != null) {
         int x = GuiTools.getRelativeX(this);
         int var10 = GuiTools.getRelativeY(this);
      }
   }

   protected abstract void renderInternal(GuiGraphicsExtractor var1, int var2, int var3, float var4);

   public void extractRenderState(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick) {
      super.extractRenderState(graphics, pMouseX, pMouseY, pPartialTick);
      this.renderInternal(graphics, pMouseX, pMouseY, pPartialTick);
   }
}
