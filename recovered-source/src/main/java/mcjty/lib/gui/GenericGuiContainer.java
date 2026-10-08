package mcjty.lib.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.client.GuiTools;
import mcjty.lib.client.RenderHelper;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.widgets.BlockRender;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.network.PacketServerCommandTyped;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LegacyCapabilities;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import mcjty.lib.varia.Tools;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class GenericGuiContainer<T extends GenericTileEntity, C extends AbstractContainerMenu>
   extends AbstractContainerScreen<C>
   implements IKeyReceiver {
   protected Window window;
   private WindowManager windowManager;
   private final GuiSideWindow sideWindow;
   private int windowImageWidth = -1;
   private int windowImageHeight = -1;

   public void setWindowDimensions(int x, int y) {
      this.windowImageWidth = x;
      this.windowImageHeight = y;
      if (this.width > 0 && this.height > 0 && x > 0 && y > 0) {
         this.leftPos = (this.width - x) / 2;
         this.topPos = (this.height - y) / 2;
      }
   }

   private int effectiveImageWidth() {
      return this.windowImageWidth > 0 ? this.windowImageWidth : this.imageWidth;
   }

   private int effectiveImageHeight() {
      return this.windowImageHeight > 0 ? this.windowImageHeight : this.imageHeight;
   }

   public GenericGuiContainer(C container, Inventory inventory, Component title, ManualEntry manualEntry) {
      super(container, inventory, title);
      this.sideWindow = new GuiSideWindow(manualEntry.manual(), manualEntry.entry(), manualEntry.page());
      this.windowManager = null;
   }

   public GenericGuiContainer(C container, Inventory inventory, Component title, ManualEntry manualEntry, int imageWidth, int imageHeight) {
      super(container, inventory, title, imageWidth, imageHeight);
      this.sideWindow = new GuiSideWindow(manualEntry.manual(), manualEntry.entry(), manualEntry.page());
      this.windowManager = null;
   }

   public <T extends GenericTileEntity> T getBE() {
      return (T)(this.menu instanceof GenericContainer container ? container.getBe() : null);
   }

   public List<Rect2i> getExtraWindowBounds() {
      if (this.sideWindow.getWindow() != null && this.sideWindow.getWindow().getToplevel() != null) {
         List<Rect2i> bounds = new ArrayList<>();
         Rectangle r1 = this.sideWindow.getWindow().getToplevel().getBounds();
         bounds.add(new Rect2i(r1.x, r1.y, r1.width, r1.height));
         if (this.windowManager != null) {
            for (Window w : this.windowManager.getWindows()) {
               Rectangle r = w.getToplevel().getBounds();
               bounds.add(new Rect2i(r.x, r.y, r.width, r.height));
            }
         }

         return bounds;
      } else {
         Logging.getLogger().error(new RuntimeException("Internal error! getExtraWindowBounds() called before initGui!"));
         return Collections.emptyList();
      }
   }

   public void init() {
      this.windowManager = null;
      super.init();
      if (this.windowImageWidth > 0 && this.windowImageHeight > 0) {
         this.leftPos = (this.width - this.windowImageWidth) / 2;
         this.topPos = (this.height - this.windowImageHeight) / 2;
      }

      this.sideWindow.initGui(this.minecraft, this, this.leftPos, this.topPos, this.effectiveImageWidth(), this.effectiveImageHeight());
   }

   protected void registerWindows(WindowManager mgr) {
   }

   protected WindowManager getWindowManager() {
      if (this.windowManager == null) {
         if (this.sideWindow.getWindow() == null) {
            RuntimeException e = new RuntimeException("Internal error! getWindowManager() called before initGui!");
            Logging.getLogger().error(e);
            throw e;
         }

         this.windowManager = new WindowManager(this);
         this.windowManager.addWindow(this.sideWindow.getWindow());
         this.windowManager.addWindow(this.window);
         this.registerWindows(this.windowManager);
      }

      return this.windowManager;
   }

   protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
      this.getWindowManager().drawTooltips(graphics);
   }

   public void drawHoveringText(GuiGraphicsExtractor graphics, List<String> textLines, List<ItemStack> items, int x, int y, Font font) {
      if (!textLines.isEmpty()) {
         int i = 0;
         int linesWithItemStacks = 0;

         for (String s : textLines) {
            int j;
            if (s != null && items != null && s.contains("@") && !items.isEmpty()) {
               List<Object> list = WindowTools.parseString(s, items);
               boolean lineHasItemStacks = false;
               j = 0;

               for (Object o : list) {
                  if (o instanceof String) {
                     j += font.width((String)o);
                  } else {
                     j += 20;
                     lineHasItemStacks = true;
                  }
               }

               if (lineHasItemStacks) {
                  linesWithItemStacks++;
               }
            } else {
               j = font.width(s);
            }

            if (j > i) {
               i = j;
            }
         }

         int xx = x + 12;
         int yy = y - 12;
         int k = 8;
         if (textLines.size() > 1) {
            k += 2 + (textLines.size() - 1) * 10 + linesWithItemStacks * 8;
         }

         if (xx > this.width - this.leftPos - i - 5) {
            xx -= 28 + i;
         }

         if (xx < 4 - this.leftPos) {
            xx = 4 - this.leftPos;
         }

         if (yy > this.height - this.topPos - k - 4) {
            yy = this.height - this.topPos - k - 4;
         } else if (yy < 4 - this.topPos) {
            yy = 4 - this.topPos;
         }

         int l = -267386864;
         graphics.fillGradient(xx - 3, yy - 4, xx + i + 3, yy - 3, l, l);
         graphics.fillGradient(xx - 3, yy + k + 3, xx + i + 3, yy + k + 4, l, l);
         graphics.fillGradient(xx - 3, yy - 3, xx + i + 3, yy + k + 3, l, l);
         graphics.fillGradient(xx - 4, yy - 3, xx - 3, yy + k + 3, l, l);
         graphics.fillGradient(xx + i + 3, yy - 3, xx + i + 4, yy + k + 3, l, l);
         int i1 = 1347420415;
         int j1 = (i1 & 16711422) >> 1 | i1 & 0xFF000000;
         graphics.fillGradient(xx - 3, yy - 3 + 1, xx - 3 + 1, yy + k + 3 - 1, i1, j1);
         graphics.fillGradient(xx + i + 2, yy - 3 + 1, xx + i + 3, yy + k + 3 - 1, i1, j1);
         graphics.fillGradient(xx - 3, yy - 3, xx + i + 3, yy - 3 + 1, i1, i1);
         graphics.fillGradient(xx - 3, yy + k + 2, xx + i + 3, yy + k + 3, j1, j1);
         this.renderTextLines(graphics, textLines, items, font, xx, yy);
      }
   }

   private void renderTextLines(GuiGraphicsExtractor graphics, List<String> textLines, List<ItemStack> items, Font font, int xx, int yy) {
      for (int i = 0; i < textLines.size(); i++) {
         String s1 = textLines.get(i);
         if (s1 != null && items != null && s1.contains("@") && !items.isEmpty()) {
            List<Object> list = WindowTools.parseString(s1, items);
            int curx = xx;
            boolean lineHasItemStacks = false;

            for (Object o : list) {
               if (o instanceof String s2) {
                  graphics.text(font, s2, curx, yy, -1);
                  curx += font.width(s2);
               } else {
                  RenderHelper.renderObject(graphics, curx + 1, yy, o, false);
                  curx += 20;
                  lineHasItemStacks = true;
               }
            }

            if (lineHasItemStacks) {
               yy += 8;
            }
         } else {
            graphics.text(font, s1, xx, yy, -1);
         }

         if (i == 0) {
            yy += 2;
         }

         yy += 10;
      }
   }

   public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int x, int y, float partialTicks) {
      this.drawWindow(graphics, partialTicks, x, y);
   }

   protected void drawWindow(GuiGraphicsExtractor graphics, float partialTicks, int x, int y) {
      if (this.window != null) {
         GenericTileEntity te = this.getBE();
         if (te != null) {
            this.getWindowManager().syncBindings(te);
         }

         this.getWindowManager().draw(graphics);
      }
   }

   public void extractRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
      if (this.window != null) {
         super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
         this.drawStackTooltips(graphics, mouseX, mouseY);
      }
   }

   @Nullable
   public Slot getHoveredSlot() {
      Slot slot = super.getHoveredSlot();
      if (slot == null) {
         return null;
      } else {
         return this.isPartiallyCoveredByModalWindow(slot) ? null : slot;
      }
   }

   public boolean isHovering(@Nonnull Slot slotIn, double mouseX, double mouseY) {
      return this.isPartiallyCoveredByModalWindow(slotIn) ? false : super.isHovering(slotIn, mouseX, mouseY);
   }

   private boolean isPartiallyCoveredByModalWindow(Slot slotIn) {
      int xPos = slotIn.x + this.window.getToplevel().getBounds().x;
      int yPos = slotIn.y + this.window.getToplevel().getBounds().y;
      return this.getWindowManager().getModalWindows().anyMatch(window -> window.getToplevel().getBounds().intersects(new Rectangle(xPos, yPos, 18, 18)));
   }

   protected void drawStackTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
      int x = GuiTools.getRelativeX(this.window.getGui());
      int y = GuiTools.getRelativeY(this.window.getGui());
      if (this.window.getToplevel().getWidgetAtPosition(x, y) instanceof BlockRender blockRender) {
         Object renderItem = blockRender.getRenderItem();
         if (!(renderItem instanceof ItemStack itemStack)) {
            if (renderItem instanceof Block) {
               itemStack = new ItemStack((Block)renderItem);
            } else if (renderItem instanceof Item) {
               itemStack = new ItemStack((Item)renderItem);
            } else {
               itemStack = ItemStack.EMPTY;
            }
         }

         if (!itemStack.isEmpty()) {
            this.customRenderToolTip(graphics, blockRender, itemStack, mouseX, mouseY);
         }
      }
   }

   protected List<Component> addCustomLines(List<Component> oldList, BlockRender blockRender, ItemStack stack) {
      return oldList;
   }

   protected void customRenderToolTip(GuiGraphicsExtractor graphics, BlockRender blockRender, ItemStack stack, int x, int y) {
      List<Component> list;
      if (stack.getItem() == null) {
         list = new ArrayList<>();
      } else {
         TooltipFlag flag = this.getMinecraft().options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL;
         list = stack.getTooltipLines(TooltipContext.of(this.getMinecraft().level), this.getMinecraft().player, flag);
      }

      list = this.addCustomLines(list, blockRender, stack);
      graphics.setTooltipForNextFrame(this.getMinecraft().font, list, Optional.empty(), x, y);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void removed() {
      super.removed();
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      boolean rc = super.mouseClicked(event, doubleClick);
      if (this.window != null) {
         this.getWindowManager().mouseClicked(event.x(), event.y(), event.button());
      }

      return rc;
   }

   public boolean mouseDragged(MouseButtonEvent event, double scaledX, double scaledY) {
      boolean rc = super.mouseDragged(event, scaledX, scaledY);
      if (this.window != null) {
         this.getWindowManager().mouseDragged(event.x(), event.y(), event.button());
      }

      return rc;
   }

   public boolean mouseScrolled(double x, double y, double dx, double dy) {
      boolean rc = super.mouseScrolled(x, y, dx, dy);
      if (this.window != null) {
         this.getWindowManager().mouseScrolled(x, y, dx, dy);
      }

      return false;
   }

   public boolean mouseReleased(MouseButtonEvent event) {
      boolean rc = super.mouseReleased(event);
      if (this.window != null) {
         this.getWindowManager().mouseReleased(event.x(), event.y(), event.button());
      }

      return rc;
   }

   @Override
   public Window getWindow() {
      return this.window;
   }

   public boolean keyPressed(KeyEvent event) {
      boolean b = this.window == null || this.getWindowManager().keyTyped(event.key(), event.scancode());
      return b && super.keyPressed(event);
   }

   @Override
   public boolean mouseClickedFromEvent(double x, double y, int button) {
      WindowManager manager = this.getWindow().getWindowManager();
      manager.mouseClicked(x, y, button);
      return true;
   }

   @Override
   public boolean mouseReleasedFromEvent(double x, double y, int button) {
      WindowManager manager = this.getWindow().getWindowManager();
      manager.mouseReleased(x, y, button);
      return true;
   }

   @Override
   public boolean mouseScrolledFromEvent(double x, double y, double dx, double dy) {
      WindowManager manager = this.getWindow().getWindowManager();
      manager.mouseScrolled(x, y, dx, dy);
      return true;
   }

   @Override
   public void keyTypedFromEvent(int keyCode, int scanCode) {
      if (this.window != null) {
         this.getWindowManager().keyTyped(keyCode, scanCode);
      }
   }

   @Override
   public void charTypedFromEvent(char codePoint) {
      if (this.window != null && this.getWindowManager().charTyped(codePoint)) {
      }
   }

   public <T> void setValue(Value<?, T> value, T v) {
      this.sendServerCommandTyped(
         this.<GenericTileEntity>getBE().getDimension(), GenericTileEntity.COMMAND_SYNC_BINDING.name(), TypedMap.builder().put(value.key(), v).build()
      );
   }

   public void sendServerCommandTyped(String command, TypedMap params) {
      Networking.sendToServer(
         PacketServerCommandTyped.create(this.<GenericTileEntity>getBE().getBlockPos(), this.<GenericTileEntity>getBE().getDimension(), command, params)
      );
   }

   public void sendServerCommandTyped(Command<?> command, TypedMap params) {
      Networking.sendToServer(
         PacketServerCommandTyped.create(this.<GenericTileEntity>getBE().getBlockPos(), this.<GenericTileEntity>getBE().getDimension(), command.name(), params)
      );
   }

   public void sendServerCommandTyped(ResourceKey<Level> dimensionId, String command, TypedMap params) {
      Networking.sendToServer(PacketServerCommandTyped.create(this.<GenericTileEntity>getBE().getBlockPos(), dimensionId, command, params));
   }

   public void sendServerCommand(String modid, String command, @Nonnull TypedMap arguments) {
      Networking.sendToServer(PacketSendServerCommand.create(modid, command, arguments));
   }

   public void sendServerCommand(String modid, String command) {
      Networking.sendToServer(PacketSendServerCommand.create(modid, command, TypedMap.EMPTY));
   }

   public static <C extends GenericContainer, S extends GenericGuiContainer<T, C>, T extends GenericTileEntity> void register(
      RegisterMenuScreensEvent event, MenuType<C> type, GenericGuiContainer.GuiSupplier<C, S, T> guiSupplier
   ) {
      ScreenConstructor<C, S> factory = (container, inventory, title) -> {
         BlockEntity te = SafeClientTools.getClientWorld().getBlockEntity(container.getPos());
         return Tools.safeMap(te, tile -> guiSupplier.create((T)tile, (C)container, inventory), "Invalid be entity!");
      };
      event.register(type, factory);
   }

   protected void updateEnergyBar(EnergyBar energyBar) {
      IEnergyStorage power = (IEnergyStorage)this.<GenericTileEntity>getBE()
         .getLevel()
         .getCapability(LegacyCapabilities.ENERGY_BLOCK, this.<GenericTileEntity>getBE().getBlockPos(), null);
      if (power != null) {
         energyBar.maxValue(power.getMaxEnergyStored());
         energyBar.value(power.getEnergyStored());
      }
   }

   @FunctionalInterface
   public interface GuiSupplier<C extends GenericContainer, S extends GenericGuiContainer, T extends GenericTileEntity> {
      S create(T var1, C var2, Inventory var3);
   }
}
