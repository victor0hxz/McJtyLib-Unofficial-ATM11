package mcjty.lib.base;

import mcjty.lib.varia.Logging;
import net.neoforged.fml.event.config.ModConfigEvent.Loading;
import net.neoforged.fml.event.config.ModConfigEvent.Reloading;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class GeneralConfig {
   private static final Builder SERVER_BUILDER = new Builder();
   private static final Builder CLIENT_BUILDER = new Builder();
   public static final ModConfigSpec SERVER_CONFIG = SERVER_BUILDER.build();
   public static final ModConfigSpec CLIENT_CONFIG = CLIENT_BUILDER.build();
   public static final String CATEGORY_GENERAL = "general";
   public static IntValue maxInfuse;
   public static BooleanValue manageOwnership;

   public static void init(Builder SERVER_BUILDER) {
      SERVER_BUILDER.comment("General settings for all mods using mcjtylib").push("general");
      Logging.doLogging = SERVER_BUILDER.comment("If true dump a lot of logging information about various things. Useful for debugging")
         .define("logging", false);
      manageOwnership = SERVER_BUILDER.comment(
            "If true then blocks using mcjtylib will have ownership tagged on them (useful for the rftools security manager)"
         )
         .define("manageOwnership", true);
      maxInfuse = SERVER_BUILDER.comment("The maximum amount of dimensional shards that can be infused in a single machine")
         .defineInRange("maxInfuse", 256, 1, Integer.MAX_VALUE);
      SERVER_BUILDER.pop();
   }

   public static void onLoad(Loading configEvent) {
      if (configEvent.getConfig().getSpec() == CLIENT_CONFIG) {
         StyleConfig.updateColors();
      }
   }

   public static void onFileChange(Reloading configEvent) {
      StyleConfig.updateColors();
   }

   static {
      StyleConfig.init(CLIENT_BUILDER);
      init(SERVER_BUILDER);
   }
}
