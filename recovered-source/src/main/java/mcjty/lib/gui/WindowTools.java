package mcjty.lib.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;

public class WindowTools {
   public static void parseAndHandleClient(Identifier guiDescription, Consumer<GuiParser.GuiCommand> consumer) {
      try {
         Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(guiDescription);
         res.ifPresent(resource -> {
            try {
               BufferedReader br = resource.openAsReader();
               GuiParser.parse(br).forEach(consumer);
            } catch (GuiParser.ParserException | IOException var3x) {
               throw new RuntimeException(var3x);
            }
         });
      } catch (RuntimeException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static List<Object> parseString(String s, List<ItemStack> items) {
      List<Object> l = new ArrayList<>();
      String current = "";

      for (int i = 0; i < s.length(); i++) {
         String c = s.substring(i, i + 1);
         if ("@".equals(c)) {
            i++;
            int itemIdx = s.charAt(i) - '0';
            if (itemIdx == 16) {
               current = current + "@";
            } else {
               if (itemIdx < 0 || itemIdx > 9) {
                  throw new IllegalArgumentException(s);
               }

               if (!current.isEmpty()) {
                  l.add(current);
                  current = "";
               }

               ItemStack e = items.get(itemIdx);
               if (!e.isEmpty()) {
                  l.add(e);
               }
            }
         } else {
            current = current + c;
         }
      }

      if (!current.isEmpty()) {
         l.add(current);
      }

      return l;
   }
}
