package mcjty.lib.tooltips;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

public class ClientTooltipIcon implements ClientTooltipComponent, TooltipComponent {
   protected List<Pair<ItemStack, Integer>> itemStack;
   protected int itemsPerLine;

   public ClientTooltipIcon(List<Pair<ItemStack, Integer>> pItemStack, int pItemsPerLine) {
      this.itemStack = pItemStack;
      this.itemsPerLine = pItemsPerLine;
   }

   public int getHeight(Font font) {
      return Math.max(1, this.itemStack.size() / this.itemsPerLine * 20);
   }

   public int getWidth(Font font) {
      return Math.min(this.itemsPerLine, this.itemStack.size()) * 21 + 4;
   }

   public void extractImage(Font font, int offsetX, int offsetY, int width, int height, GuiGraphicsExtractor graphics) {
      int j = 0;

      for (Pair<ItemStack, Integer> item : this.itemStack) {
         int x = offsetX + Math.floorMod(j, this.itemsPerLine) * 21;
         int y = offsetY + Math.floorDiv(j, this.itemsPerLine) * 20 - 20;
         graphics.item((ItemStack)item.getLeft(), x, y, x * y * 31);
         graphics.itemDecorations(Minecraft.getInstance().font, (ItemStack)item.getLeft(), x, y, "");
         renderItemCount(font, x, y + 20, ((ItemStack)item.getLeft()).getCount(), (Integer)item.getRight());
         j++;
      }
   }

   private static void renderItemCount(Font font, int x, int y, int count, int errorAmount) {
      if (errorAmount != -2) {
         if (count != 1 || errorAmount != 0) {
            BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
            String s1 = count == Integer.MAX_VALUE ? "∞" : Integer.toString(count);
            PoseStack pose = new PoseStack();
            pose.translate(0.0, 0.0, 200.0);
            drawStringToWidth(font, source, pose, x, y, 18, 16777215, s1);
            if (errorAmount >= 0) {
               String s2 = "(" + Integer.toString(errorAmount) + ")";
               drawStringToWidth(font, source, pose, x + 8, y + 17, 18, -65536, s2);
            }

            source.endBatch();
         }
      }
   }

   private static void drawStringToWidth(Font font, BufferSource buffersource, PoseStack pose, int x, int y, int width, int color, String str) {
      pose.pushPose();
      pose.translate(x + width, y, 0.0F);
      int strWidth = font.width(str);
      if (strWidth > width) {
         pose.scale((float)width / strWidth, (float)width / strWidth, 1.0F);
      }

      font.drawInBatch(str, -strWidth, -9, color, true, pose.last().pose(), buffersource, DisplayMode.NORMAL, 0, 15728880);
      pose.popPose();
   }
}
