package mcjty.lib.keys;

import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class KeyBindings {
   public static KeyMapping openManual;
   private static final Category CATEGORY = Category.register(Identifier.fromNamespaceAndPath("mcjtylib", "main"));

   public static void init(RegisterKeyMappingsEvent event) {
      openManual = new KeyMapping("key.openManual", Type.KEYSYM, 290, CATEGORY);
      event.register(openManual);
   }
}
