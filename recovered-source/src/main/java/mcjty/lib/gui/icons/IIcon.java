package mcjty.lib.gui.icons;

import java.util.Map;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

public interface IIcon {
   void draw(Screen var1, GuiGraphicsExtractor var2, int var3, int var4);

   void addOverlay(IIcon var1);

   void removeOverlay(String var1);

   void clearOverlays();

   boolean hasOverlay(String var1);

   void addData(String var1, Object var2);

   void removeData(String var1);

   void clearData();

   Map<String, Object> getData();

   IIcon clone();

   String getID();
}
