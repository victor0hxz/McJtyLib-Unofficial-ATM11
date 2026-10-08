package mcjty.lib.gui;

import mcjty.lib.McJtyLib;
import mcjty.lib.gui.widgets.Button;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widgets;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketOpenManual;
import mcjty.lib.network.PacketSetGuiStyle;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;

public class GuiSideWindow {
   private GuiStyle style;
   private Window sideWindow;
   private Button guiButton;
   private final Identifier manual;
   private final Identifier manualNode;
   private final int page;

   public GuiSideWindow(Identifier manual, Identifier manualNode, int page) {
      this.manual = manual;
      this.manualNode = manualNode;
      this.page = page;
   }

   public void initGui(Minecraft mc, Screen gui, int guiLeft, int guiTop, int xSize, int ySize) {
      PreferencesProperties properties = McJtyLib.getPreferencesProperties(mc.player);
      this.style = properties != null ? properties.getStyle() : GuiStyle.STYLE_FLAT_GRADIENT;
      Button helpButton = Widgets.button(1, 1, 16, 16, "?").tooltips("Open manual").event(() -> this.help(mc));
      this.guiButton = Widgets.button(1, 19, 16, 16, "s").event(() -> this.changeStyle());
      this.setStyleTooltip();
      Panel sidePanel = Widgets.positional().children(this.guiButton, helpButton);
      int sideLeft = guiLeft + xSize;
      int sideTop = guiTop + (ySize - 20) / 2 - 8;
      sidePanel.bounds(sideLeft, sideTop, 20, 40);
      this.sideWindow = new Window(gui, sidePanel);
   }

   private void help(Minecraft mc) {
      if (this.manual != null) {
         Networking.sendToServer(PacketOpenManual.create(this.manual, this.manualNode, this.page));
      }
   }

   private void setStyleTooltip() {
      this.guiButton.tooltips("Gui style:", this.style.getStyle());
   }

   private void changeStyle() {
      int next = this.style.ordinal() + 1;
      if (next >= GuiStyle.values().length) {
         next = 0;
      }

      this.style = GuiStyle.values()[next];
      Networking.sendToServer(PacketSetGuiStyle.create(this.style.getStyle()));
      this.setStyleTooltip();
   }

   public Window getWindow() {
      return this.sideWindow;
   }
}
