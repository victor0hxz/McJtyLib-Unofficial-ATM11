package mcjty.lib.varia;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class SafeClientTools {
   public static Level getClientWorld() {
      return Minecraft.getInstance().level;
   }

   public static Level getWorld() {
      return Minecraft.getInstance().level;
   }

   public static Player getClientPlayer() {
      return Minecraft.getInstance().player;
   }

   public static HitResult getClientMouseOver() {
      return Minecraft.getInstance().hitResult;
   }

   public static boolean isSneaking() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 344);
   }

   public static boolean isCtrlKeyDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 345);
   }

   public static boolean isJumpKeyDown() {
      return Minecraft.getInstance().options.keyJump.isDown();
   }
}
