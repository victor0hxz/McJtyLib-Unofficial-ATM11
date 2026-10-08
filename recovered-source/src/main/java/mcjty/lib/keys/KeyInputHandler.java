package mcjty.lib.keys;

import mcjty.lib.client.ClientManualHelper;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;

public class KeyInputHandler {
   @SubscribeEvent
   public void onKeyInput(Pre event) {
      KeyMapping kb = KeyBindings.openManual;
      boolean doStuff = kb != null && kb.isDown();
      if (doStuff) {
         ClientManualHelper.openManualFromGui();
      }
   }
}
