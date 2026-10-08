package mcjty.lib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Math;
import org.joml.Quaternionf;

public class HudRenderHelper {
   public static void renderHudItems(
      PoseStack matrixStack,
      MultiBufferSource buffer,
      List<Pair<ItemStack, String>> messages,
      HudRenderHelper.HudPlacement hudPlacement,
      HudRenderHelper.HudOrientation hudOrientation,
      Direction orientation,
      double x,
      double y,
      double z,
      float scale
   ) {
      matrixStack.pushPose();
      if (hudPlacement == HudRenderHelper.HudPlacement.HUD_FRONT) {
         matrixStack.translate((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
      } else if (hudPlacement == HudRenderHelper.HudPlacement.HUD_CENTER) {
         matrixStack.translate((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
      } else {
         matrixStack.translate((float)x + 0.5F, (float)y + 1.75F, (float)z + 0.5F);
      }

      Quaternionf quaternion = new Quaternionf(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
      switch (hudOrientation) {
         case HUD_SOUTH:
            matrixStack.mulPose(new Quaternionf().setAngleAxis(-getHudAngle(orientation), 0.0F, 1.0F, 0.0F));
            break;
         case HUD_TOPLAYER_HORIZ:
            float yaw = Minecraft.getInstance().gameRenderer.getMainCamera().yRot();
            matrixStack.mulPose(new Quaternionf().rotateY(Math.toRadians(-yaw + 180.0F)));
            break;
         case HUD_TOPLAYER:
            matrixStack.mulPose(quaternion);
            matrixStack.mulPose(fromXYZ(0.0F, 3.14159F, 0.0F));
      }

      if (hudPlacement == HudRenderHelper.HudPlacement.HUD_FRONT || hudPlacement == HudRenderHelper.HudPlacement.HUD_ABOVE_FRONT) {
         matrixStack.translate(0.0F, -0.25F, 0.46249998F);
      } else if (hudPlacement != HudRenderHelper.HudPlacement.HUD_CENTER) {
         matrixStack.translate(0.0F, -0.25F, -0.037499994F);
      }

      renderText(matrixStack, buffer, Minecraft.getInstance().font, messages, 11, scale);
      matrixStack.popPose();
   }

   private static Quaternionf fromXYZ(float pX, float pY, float pZ) {
      Quaternionf quaternion = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
      quaternion.mul(new Quaternionf((float)Math.sin(pX / 2.0F), 0.0F, 0.0F, (float)Math.cos(pX / 2.0F)));
      quaternion.mul(new Quaternionf(0.0F, (float)Math.sin(pY / 2.0F), 0.0F, (float)Math.cos(pY / 2.0F)));
      quaternion.mul(new Quaternionf(0.0F, 0.0F, (float)Math.sin(pZ / 2.0F), (float)Math.cos(pZ / 2.0F)));
      return quaternion;
   }

   public static void renderHud(
      PoseStack stack,
      MultiBufferSource buffer,
      List<String> messages,
      HudRenderHelper.HudPlacement hudPlacement,
      HudRenderHelper.HudOrientation hudOrientation,
      Direction orientation,
      double x,
      double y,
      double z,
      float scale
   ) {
      renderHudItems(
         stack,
         buffer,
         messages.stream().map(s -> Pair.of(ItemStack.EMPTY, s)).collect(Collectors.toList()),
         hudPlacement,
         hudOrientation,
         orientation,
         x,
         y,
         z,
         scale
      );
   }

   private static float getHudAngle(Direction orientation) {
      float f3 = 0.0F;
      if (orientation != null) {
         f3 = switch (orientation) {
            case NORTH -> (float) java.lang.Math.PI;
            case WEST -> (float) (java.lang.Math.PI / 2);
            case EAST -> (float) (-java.lang.Math.PI / 2);
            default -> 0.0F;
         };
      }

      return f3;
   }

   private static void renderText(
      PoseStack matrixStack, MultiBufferSource buffer, Font fontrenderer, List<Pair<ItemStack, String>> messages, int lines, float scale
   ) {
      matrixStack.translate(-0.5F, 0.5F, 0.07F);
      float f3 = 0.0075F;
      matrixStack.scale(f3 * scale, -f3 * scale, f3);
      renderLog(matrixStack, buffer, fontrenderer, messages, lines);
   }

   private static void renderLog(PoseStack matrixStack, MultiBufferSource buffer, Font fontrenderer, List<Pair<ItemStack, String>> messages, int lines) {
      int currenty = 7;
      int height = 10;
      int logsize = messages.size();
      int i = 0;

      for (Pair<ItemStack, String> pair : messages) {
         ItemStack stack = (ItemStack)pair.getLeft();
         String s = (String)pair.getRight();
         if (i >= logsize - lines && currenty + height <= 124) {
            String prefix = "";
            if (!stack.isEmpty()) {
               matrixStack.pushPose();
               matrixStack.translate(14.0F, currenty + 4.0F, 0.0F);
               matrixStack.scale(10.0F, -10.0F, 16.0F);
               prefix = "    ";
               matrixStack.popPose();
            }

            fontrenderer.drawInBatch(
               fontrenderer.plainSubstrByWidth(prefix + s, 115),
               7.0F,
               currenty,
               16777215,
               false,
               matrixStack.last().pose(),
               buffer,
               DisplayMode.NORMAL,
               0,
               15728880
            );
            currenty += height;
         }

         i++;
      }
   }

   public static enum HudOrientation {
      HUD_SOUTH,
      HUD_TOPLAYER_HORIZ,
      HUD_TOPLAYER;
   }

   public static enum HudPlacement {
      HUD_ABOVE,
      HUD_ABOVE_FRONT,
      HUD_FRONT,
      HUD_CENTER;
   }
}
