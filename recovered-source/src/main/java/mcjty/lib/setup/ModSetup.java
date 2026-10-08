package mcjty.lib.setup;

import mcjty.lib.McJtyLib;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;

public class ModSetup extends DefaultModSetup {
   public static boolean patchouli = false;

   @Override
   public void init(FMLCommonSetupEvent e) {
      super.init(e);
      NeoForge.EVENT_BUS.register(new ModSetup.EventHandler());
   }

   @Override
   protected void setupModCompat() {
      patchouli = ModList.get().isLoaded("patchouli");
   }

   public static class EventHandler {
      @SubscribeEvent
      public void onPlayerTickEvent(Pre event) {
         if (!event.getEntity().level().isClientSide()) {
            PreferencesProperties properties = McJtyLib.getPreferencesProperties(event.getEntity());
            if (properties != null) {
               properties.tick((ServerPlayer)event.getEntity());
            }
         }
      }
   }
}
