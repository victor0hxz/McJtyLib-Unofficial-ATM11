package mcjty.lib.gui;

import java.util.function.Consumer;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.gui.widgets.Widgets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class GuiPopupTools {
   public static void askSomething(
      Minecraft mc, Screen gui, WindowManager windowManager, int x, int y, String title, String initialValue, Consumer<String> callback
   ) {
      Panel ask = Widgets.vertical().filledBackground(-10066330, -5592406).filledRectThickness(1);
      ask.bounds(x, y, 100, 60);
      Window askWindow = windowManager.createModalWindow(ask);
      ask.children(Widgets.label(title));
      TextField input = new TextField().addTextEnterEvent(newText -> {
         windowManager.closeWindow(askWindow);
         callback.accept(newText);
      });
      input.text(initialValue);
      ask.children(input);
      Panel buttons = Widgets.horizontal().desiredWidth(100).desiredHeight(18);
      buttons.children(Widgets.button("Ok").event(() -> {
         windowManager.closeWindow(askWindow);
         callback.accept(input.getText());
      }));
      buttons.children(Widgets.button("Cancel").event(() -> windowManager.closeWindow(askWindow)));
      ask.children(buttons);
   }

   public static void showMessage(Minecraft mc, Screen gui, WindowManager windowManager, int x, int y, String title) {
      Panel ask = Widgets.vertical().filledBackground(-10066330, -5592406).filledRectThickness(1);
      ask.bounds(x, y, 200, 40);
      Window askWindow = windowManager.createModalWindow(ask);
      ask.children(Widgets.label(title).desiredWidth(200));
      Panel buttons = Widgets.horizontal().desiredWidth(100).desiredHeight(18);
      buttons.children(Widgets.button("Cancel").event(() -> windowManager.closeWindow(askWindow)));
      ask.children(buttons);
   }
}
