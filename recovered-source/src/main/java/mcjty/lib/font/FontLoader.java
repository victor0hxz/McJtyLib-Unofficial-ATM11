package mcjty.lib.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

public class FontLoader {
   public static TrueTypeFont loadSystemFont(String name, float defSize, boolean antialias) {
      return loadSystemFont(name, defSize, antialias, 0);
   }

   public static TrueTypeFont loadSystemFont(String name, float defSize, boolean antialias, int type) {
      TrueTypeFont out = null;

      try {
         Font font = new Font(name, type, (int)defSize);
         font = font.deriveFont(defSize);
         out = new TrueTypeFont(font, antialias);
      } catch (RuntimeException var7) {
         Logging.logError("Error loading font!", var7);
      }

      return out;
   }

   public static TrueTypeFont createFont(Identifier res, float defSize, boolean antialias) {
      return createFont(res, defSize, antialias, 0);
   }

   public static TrueTypeFont createFont(Identifier res, float defSize, boolean antialias, int type) {
      return createFont(res, defSize, antialias, type, null);
   }

   public static TrueTypeFont createFont(Identifier res, float defSize, boolean antialias, int type, char[] additionalChars) {
      TrueTypeFont out = null;

      try {
         Font font = Font.createFont(type, ((Resource)Minecraft.getInstance().getResourceManager().getResource(res).get()).open());
         font = font.deriveFont(defSize);
         out = new TrueTypeFont(font, antialias, additionalChars);
      } catch (FontFormatException | IOException | RuntimeException var8) {
         Logging.logError("Error loading font!", var8);
      }

      return out;
   }
}
