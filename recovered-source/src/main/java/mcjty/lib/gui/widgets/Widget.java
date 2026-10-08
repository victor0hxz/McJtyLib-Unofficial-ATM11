package mcjty.lib.gui.widgets;

import java.awt.Rectangle;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import mcjty.lib.gui.GuiParser;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.LayoutHint;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.typed.Type;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public interface Widget<P extends Widget<P>> {
   int SIZE_UNKNOWN = -1;

   String getName();

   Window getWindow();

   void setWindow(Window var1);

   P name(String var1);

   P channel(String var1);

   boolean containsWidget(Widget<?> var1);

   void bounds(int var1, int var2, int var3, int var4);

   int getDesiredSize(Widget.Dimension var1);

   P desiredWidth(int var1);

   int getDesiredWidth();

   P desiredHeight(int var1);

   int getDesiredHeight();

   P tooltips(String... var1);

   P tooltipItems(ItemStack... var1);

   List<String> getTooltips();

   List<ItemStack> getTooltipItems();

   P enabled(boolean var1);

   P enabledFlags(String... var1);

   @Nonnull
   Set<Integer> getEnabledFlags();

   boolean isEnabled();

   boolean isEnabledAndVisible();

   P hovering(boolean var1);

   boolean isHovering();

   P visible(boolean var1);

   boolean isVisible();

   Rectangle getBounds();

   boolean in(double var1, double var3);

   Widget<?> getWidgetAtPosition(double var1, double var3);

   void draw(Screen var1, GuiGraphicsExtractor var2, int var3, int var4);

   void drawPhase2(Screen var1, GuiGraphicsExtractor var2, int var3, int var4);

   Widget<?> mouseClick(double var1, double var3, int var5);

   void mouseRelease(double var1, double var3, int var5);

   void mouseMove(double var1, double var3);

   boolean mouseScrolled(double var1, double var3, double var5, double var7);

   boolean keyTyped(int var1, int var2);

   boolean charTyped(char var1);

   P hint(LayoutHint var1);

   default P hint(int x, int y, int width, int height) {
      return this.hint(new PositionalLayout.PositionalHint(x, y, width, height));
   }

   LayoutHint getLayoutHint();

   P userObject(Object var1);

   Object getUserObject();

   void readFromGuiCommand(GuiParser.GuiCommand var1);

   GuiParser.GuiCommand createGuiCommand();

   void fillGuiCommand(GuiParser.GuiCommand var1);

   <T> void setGenericValue(T var1);

   Object getGenericValue(Type<?> var1);

   void markLayoutDirty();

   public static enum Dimension {
      DIMENSION_WIDTH,
      DIMENSION_HEIGHT;
   }
}
