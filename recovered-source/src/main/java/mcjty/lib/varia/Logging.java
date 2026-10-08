package mcjty.lib.varia;

import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging {
   private static Logging instance = null;
   public static long prevTicks = -1L;
   private final Logger logger = LogManager.getLogger("mcjtylib");
   public static boolean debugMode = false;
   public static BooleanValue doLogging;

   private Logging() {
      instance = this;
   }

   private static Logging getInstance() {
      if (instance == null) {
         instance = new Logging();
      }

      return instance;
   }

   public static void logError(String msg) {
      getLogger().log(Level.ERROR, msg);
   }

   public static void log(net.minecraft.world.level.Level world, BlockEntity te, String message) {
      if ((Boolean)doLogging.get()) {
         long ticks = world.getGameTime();
         if (ticks != prevTicks) {
            prevTicks = ticks;
            getInstance().logger.log(Level.INFO, "=== Time " + ticks + " ===");
         }

         String id = te.getBlockPos().getX() + "," + te.getBlockPos().getY() + "," + te.getBlockPos().getZ() + ": ";
         getInstance().logger.log(Level.INFO, id + message);
      }
   }

   public static Logger getLogger() {
      return getInstance().logger;
   }

   public static void logError(String msg, Throwable e) {
      getLogger().error(msg, e);
   }

   public static void log(String message) {
      getInstance().logger.log(Level.INFO, message);
   }

   public static void logDebug(String message) {
      if (debugMode) {
         getInstance().logger.log(Level.INFO, message);
      }
   }

   public static void message(@Nonnull Player player, String message) {
      player.sendSystemMessage(ComponentFactory.literal(message));
   }

   public static void warn(@Nonnull Player player, String message) {
      player.sendSystemMessage(ComponentFactory.literal(message).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
   }
}
